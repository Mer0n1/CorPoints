package com.example.corpoints.cserver;

import android.app.Activity;
import android.widget.Toast;

import com.example.corpoints.MainActivity;
import com.example.corpoints.StartIdentActivity;
import com.example.corpoints.ui.GroupActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

    //Aes, rsa keys
    private static Map<String, Object> RsaKey;
    private static String RsaPrivateKey;
    private static String RsaPublicKey;
    private static String RsaPublicKeyServer;
    private static SecretKey AesKey;
    private static String algorithm;
    private static DataInputStream dIn;
    private static DataOutputStream dOut;

    /**Запрет на изменение в другом классе. Работает как дружеский класс */
    public static MyAccount myAccount  = new MyAccount(); //private security //каждое изменение параметра влечет за собой изменение серверных параметров

    private Server() { }

    public static boolean StartProtocolIdentefication(String name, String password, StartIdentActivity.TypeIdent type, Activity activity)
    {

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    client = new Socket("corppoints.ru", 49432); //подключиться к TCP

                    in   = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    out  = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    dOut = new DataOutputStream(client.getOutputStream());
                    dIn  = new DataInputStream(client.getInputStream());
                    algorithm = "AES/CBC/PKCS5Padding";

                    Thread.sleep(1000);

                    //Создадим пару ключей RSA
                    RsaKey = rsa.initKey();
                    RsaPrivateKey = rsa.getPrivateKey(RsaKey);
                    RsaPublicKey  = rsa.getPublicKey(RsaKey);

                    //Примем json запрос содержащий публичный ключ RSA сервера
                    while (true)
                        if (in.ready()) {
                            requery = in.readLine();
                            break;
                        }

                    //Изымем публичный ключ
                    JSONObject quer = new JSONObject(requery);
                    RsaPublicKeyServer = quer.get("Key").toString(); //сделать через dIn

                    //Отправим свой ключ
                    String req = ("{\"Protocol\":\"RsaKey\",\"Key\":\"" + RsaPublicKey + "\"}");
                    Send_Security(req);

                    //примем ключ Aes
                    int length_pk = dIn.readInt();
                    byte[] aeskey = new byte[length_pk];
                    dIn.readFully(aeskey, 0, aeskey.length);

                    //byte[] decodebyte = rsa.decryptByPrivateKey(aeskey, RsaPrivateKey);
                    AesKey = new SecretKeySpec(aeskey, 0, aeskey.length, "AES"); //байты в SecretKey
                    //------Пересылка ключей завершена

                    //Отправим зашифрованные данные протокола идентефикации
                    String name_     = aes.encrypt(algorithm, name, AesKey);
                    String password_ = aes.encrypt(algorithm, password, AesKey);
                    //typeString     = aes.encrypt(algorithm, (type == StartIdentActivity.TypeIdent.aut) ? "aut" : "reg", AesKey);
                    req = "{\"Protocol\":\"" + type + "\",\"Name\":\"" + name_ + "\",\"Password\":\""+password_ +"\"}";
                    Send_Security(req);

                    //Читаем ответ на авторизацию
                    requery = LateReading();
                    requery = aes.decrypt(algorithm, requery, AesKey);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            tr.start();
            tr.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Обрабатываем ответ на идентефикацию
        try {
            JSONObject quer = new JSONObject(requery);

            String protocol = quer.get("Protocol").toString();

            if ((protocol.equals("aut") || protocol.equals("reg")) &&
                    quer.get("itog").toString().equals("y")) {

                myAccount.name = name;
                myAccount.password = password;

                readerp = new Thread(new reader());
                readerp.start();
                StartPinger();
            }
            else {
                Toast.makeText(activity, "Ошибка идентефикации", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UpdateInfoProtocolAccount();
        return true;
    }


    /**Читатель. Читает протоколы сервера и выполняет их функции.*/
    public static class reader implements Runnable
    {
        private static Activity activity;
        private static Activity group_activity;

        @Override
        public void run() {

            while (true)
            {
                try {
                    if (client.isClosed()) break;
                    if (!in.ready()) continue;

                    String requery = in.readLine();
                    requery = aes.decrypt(algorithm, requery, AesKey);

                    JSONObject json = new JSONObject(requery);
                    String protocol = json.get("Protocol").toString();

                    if (protocol.equals("infoMyAccount")) //протокол добавления информации об учащемся
                    {
                        myAccount.score   = Integer.valueOf(json.get("Your_score").toString()); //can be error
                        myAccount.name    = json.get("Your_name").toString();
                        myAccount.myGroup = json.get("Your_group").toString();
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

                    Thread.sleep(15);

                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("Socket closed"))
                        break;
                } catch (JSONException | NoSuchPaddingException | NoSuchAlgorithmException
                        | InvalidAlgorithmParameterException | InvalidKeyException |
                        BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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

    /** Обычное отправление данных на сервер */
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
    public static void SendWithAes(String req) {
        try {
            Send(aes.encrypt(algorithm, req, AesKey));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                InvalidAlgorithmParameterException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /** Тип отправки: безопасный */
    public static void Send_Security(String req) {
        try {
            dOut.writeInt(req.getBytes().length);
            dOut.write(req.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Безопасное ожидание чтения данных. Читает зашифрованные rsa данные */
    public static void LateReading_Security(byte[] request) {
        while (true) {
            try {
                if (in.ready()) {
                    dIn.readFully(request, 0, request.length);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Обычное ожидание чтения данных. Незаошифрованные json запросы принимаются таким образом */
    public static String LateReading() {
        while (true) {
            try {
                if (in.ready())
                    return in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Protocols
    public static void UpdateInfoProtocolAccount()  { SendWithAes("{\"Protocol\":\"infoMyAccount\"}"); }
    public static void UpdateInfoProtocolAccounts() { SendWithAes("{\"Protocol\":\"ListAccounts\"}"); }
    public static void UpdateInfoProtocolGroups()   { SendWithAes("{\"Protocol\":\"updateGroup\"}");}
    public static void UpdateInfoProtocolUsersGroup(String nameGroup) {
        SendWithAes("{\"Protocol\":\"updateUsersGroup\",\"nameGroup\":\"" + nameGroup + "\"}");}
    public static void ProtocolInfoRequests() { SendWithAes("{\"Protocol\":\"InfoRequests\"}");}
    public static void ProtocolCreateGroup(String name) {
        SendWithAes("{\"Protocol\":\"createGroup\",\"nameGroup\":\"" + name + "\"}"); }
    public static void ProtocolLeaveGroup() {
        SendWithAes("{\"Protocol\":\"LeaveGroup\"}");
        myAccount.myGroup = "null"; //get result true/false?
    }
    public static void ProtocolSendScore(String name_getter, int score) {
        SendWithAes("{\"Protocol\":\"sendScore\", \"Me\":\"" + myAccount.name + "\",\"Who\":\""
                + name_getter + "\",\"qpoints\":\"" + String.valueOf(score) + "\"}"); }
    public static void ProtocolSendScoreToGroup(int score) {
        SendWithAes("{\"Protocol\":\"SendScoreToGroup\",\"qpoints\":\"" +  String.valueOf(score) + "\"}");
    }
    //отправить заявку на вступление в группу
    public static void ProtocolJoinToGroup(String nameGroup) {
        SendWithAes("{\"Protocol\":\"JoinToGroup\",\"NameGroup\":\"" + nameGroup + "\"}");}
    public static void ProtocolAcceptRequest(String nickname) {
        SendWithAes("{\"Protocol\":\"AcceptRequest\",\"NameNewUser\":\"" + nickname + "\"}"); }
    public static void ProtocolRejectRequest(String nickname) {
        SendWithAes("{\"Protocol\":\"RejectRequest\",\"NameNewUser\":\"" + nickname + "\"}"); }
    public static void ProtocolLeave() { SendWithAes("{\"Protocol\":\"Leave\"}");}


    public static MyAccount synchronizationMyAccount() {
        while (true) {
            if (myAccount.name != null && myAccount.score != -1)
                break;
            /*if (client == null)
                break;*/
        }

        return myAccount;
    }
    public static void Close() {
        try {
            ProtocolLeave();
            Thread.sleep(200);

            readerp.interrupt();
            pinger.interrupt();
            client.close();

            readerp = null;
            pinger  = null;
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
