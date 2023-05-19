package com.example.corpoints.cserver;

import android.app.Activity;
import android.widget.Toast;

import com.example.corpoints.MainActivity;
import com.example.corpoints.StartIdentActivity;
import com.example.corpoints.cserver.MyAccount;
import com.example.corpoints.ui.GroupActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 Server of a client
 Singleton
 */
public class Server {

    private static transient Socket client;
    private static transient BufferedReader in;
    private static transient BufferedWriter out;
    private static transient volatile String requery;
    private static Thread readerp;
    private static Thread pinger;

    /**Запрет на изменение в другом классе. Работает как дружеский класс */
    public static MyAccount myAccount  = new MyAccount(); //private security //каждое изменение параметра влечет за собой изменение серверных параметров

    private Server() { }

    public static boolean StartProtocolIdentefication(String name, String password, StartIdentActivity.TypeIdent type)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new Socket("corppoints.ru", 49432); //подключиться к TCP

                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                    Thread.sleep(1000);

                    Send("{\"Protocol\":\"" + type + "\",\"Name\":\"" + name + "\",\"Password\":\""+password+"\"}");

                    while (true) { //*
                        if (in.ready()) {
                            requery = in.readLine();
                            break;
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Request " + requery);
        try {
            JSONObject quer = new JSONObject(requery);

            String protocol = quer.get("Protocol").toString();

            if ((protocol.equals("aut") || protocol.equals("reg")) &&
                    quer.get("itog").toString().equals("y")) {

                myAccount.name = name;
                myAccount.password = password;

                //Sleep 100
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                readerp = new Thread(new reader());
                readerp.start();
                StartPinger();
            }
            else
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UpdateInfoProtocolAccount();
        return true;
    }

    public static void Send(String req) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (out != null) {
                        out.write(req + "\n");
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**Читатель. Читает протоколы сервера и выполняет их функции. */
    public static class reader implements Runnable
    {
        private static Activity activity; //test //private and setActivity
        private static Activity group_activity;

        @Override
        public void run() {

            while (true)
            {
                try {
                    if (!in.ready()) continue;

                    String requery = in.readLine();
                    System.out.println("Request " + requery);
                    JSONObject json = new JSONObject(requery);
                    String protocol = json.get("Protocol").toString();

                    if (protocol.equals("infoMyAccount")) //протокол добавления информации об учащемся
                    {
                        myAccount.score = Integer.valueOf(json.get("Your_score").toString()); //can be error
                        myAccount.name = json.get("Your_name").toString();
                        myAccount.myGroup = json.get("Your_group").toString();
                        System.out.println("mygroup " + json.get("Your_group").toString());
                    }

                    if (protocol.equals("ListAccounts")) {
                        String list = json.get("accounts").toString();
                        JSONArray array = new JSONArray(list);
                        String[] strarray = new String[array.length()];

                        for (int j = 0; j < strarray.length; j++) {
                            JSONObject json_data = array.getJSONObject(j);
                            strarray[j] = json_data.get("name").toString();
                        }

                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity)activity).setListUsers(strarray);
                                }
                            });
                        }
                    }

                    if (protocol.equals("updateGroup")) {
                        String list = json.get("groups").toString();
                        JSONArray array = new JSONArray(list);
                        String[] strarray = new String[array.length()];
                        String[] scorearray = new String[array.length()];
                        myAccount.NameAdminGroup = json.get("admin").toString();

                        for (int j = 0; j < strarray.length; j++) {
                            JSONObject json_data = array.getJSONObject(j);
                            strarray[j] = json_data.get("name").toString();
                            scorearray[j] = json_data.get("score").toString();

                            if (strarray[j].equals(myAccount.getNameGroup()))
                                myAccount.ScoreGroup = Integer.valueOf(scorearray[j]);
                        }

                        for (int j = 1; j < scorearray.length; j++) {
                            if (Integer.valueOf(scorearray[j]) > Integer.valueOf(scorearray[j-1])) {
                                String copyj = scorearray[j];
                                scorearray[j] = scorearray[j-1];
                                scorearray[j-1] = copyj;

                                copyj = strarray[j];
                                strarray[j] = strarray[j-1];
                                strarray[j-1] = copyj;
                            }
                        }

                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity)activity).setListGroups(strarray, scorearray);
                                }
                            });
                        }
                    }

                    if (protocol.equals("updateUsersGroup")) {
                        String list = json.get("users").toString();
                        JSONArray array = new JSONArray(list);

                        String[] strarray = new String[array.length()];
                        for (int j = 0; j < strarray.length; j++) {
                            JSONObject json_data = array.getJSONObject(j);
                            strarray[j] = json_data.get("name").toString();
                        }

                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity)activity).setUsersGroup(strarray);
                                }
                            });
                        }
                    }

                    if (protocol.equals("InfoRequests")) {
                        String list = json.get("Requests").toString();
                        JSONArray array = new JSONArray(list);
                        String[] strarray = new String[array.length()];

                        for (int j = 0; j < strarray.length; j++) {
                            JSONObject json_data = array.getJSONObject(j);
                            strarray[j] = json_data.get("name").toString();
                        }

                        if (group_activity != null) {
                            group_activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((GroupActivity)group_activity).setRequests(strarray);
                                }
                            });
                        }
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("Socket closed"))
                        break;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Thread is closed");
        }

        public static void setActivity(Activity activity_) { activity = activity_; }
        public static void setGroupActivity(Activity activity_) { group_activity = activity_; }
    }

    private static void StartPinger() {
        if (pinger != null) return;

        pinger = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(180000);
                        Send("\n");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        pinger.start();
    }

    //Protocols
    public static void UpdateInfoProtocolAccount()  { Send("{\"Protocol\":\"infoMyAccount\"}"); }
    public static void UpdateInfoProtocolAccounts() { Send("{\"Protocol\":\"ListAccounts\"}"); }
    public static void UpdateInfoProtocolGroups()   { Send("{\"Protocol\":\"updateGroup\"}");}
    public static void UpdateInfoProtocolUsersGroup(String nameGroup) {
        Send("{\"Protocol\":\"updateUsersGroup\",\"nameGroup\":\"" + nameGroup + "\"}");}
    public static void ProtocolInfoRequests() { Send("{\"Protocol\":\"InfoRequests\"}");}
    public static void ProtocolCreateGroup(String name) {
        Send("{\"Protocol\":\"createGroup\",\"nameGroup\":\"" + name + "\"}"); }
    public static void ProtocolLeaveGroup() {
        Send("{\"Protocol\":\"LeaveGroup\"}");
        myAccount.myGroup = "null"; //get result true/false?
    }
    public static void ProtocolSendScore(String name_getter, int score) {
        Send("{\"Protocol\":\"sendScore\", \"Me\":\"" + myAccount.name + "\",\"Who\":\""
                + name_getter + "\",\"qpoints\":\"" + String.valueOf(score) + "\"}"); }
    public static void ProtocolSendScoreToGroup(int score) {
        Send("{\"Protocol\":\"SendScoreToGroup\",\"qpoints\":\"" +  String.valueOf(score) + "\"}");
    }
    //отправить заявку на вступление в группу
    public static void ProtocolJoinToGroup(String nameGroup) {
        Send("{\"Protocol\":\"JoinToGroup\",\"NameGroup\":\"" + nameGroup + "\"}");}
    public static void ProtocolAcceptRequest(String nickname) {
        Send("{\"Protocol\":\"AcceptRequest\",\"NameNewUser\":\"" + nickname + "\"}"); }
    public static void ProtocolRejectRequest(String nickname) {
        Send("{\"Protocol\":\"RejectRequest\",\"NameNewUser\":\"" + nickname + "\"}"); }

    public static MyAccount synchronizationMyAccount() {
        while (true) {
            if (myAccount.name != null && myAccount.score != -1)
                break;
            /*if (client == null)
                break;*/
        }

        return myAccount;
    }
    /*public static void Close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
