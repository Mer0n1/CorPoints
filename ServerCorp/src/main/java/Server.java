
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.naming.Name;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private volatile ServerSocket serverSocket;
    private volatile OutputStream output;
    private volatile List<Client> clients;
    private volatile List<Group> groups; //link
    private volatile BaseData bd;
    private Thread pinger, reader, listener;

    public Server() {
        try {
            serverSocket = new ServerSocket(5556);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clients = new ArrayList<>();
        groups = new ArrayList<>();
        bd = new BaseData();
        groups = bd.getGroups();
        groups.get(0).addRequest("sdf");
        groups.get(0).addRequest("Sd"); ///test

        reader();
        CheckAliveClients();
    }

    private void reader() {
        reader = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    for (int j = 0; j < clients.size(); j++) {
                        try {
                            Client client = clients.get(j); //bug if forEach
                            if (client.reader.ready()) {

                                String request = client.reader.readLine();

                                //Check ping protocol
                                if(request.isEmpty() && request != null)
                                    client.timer.start(); //сброс таймера

                                //Json request
                                JSONObject jsonObject = (JSONObject) JSONValue.parse(request);
                                if (jsonObject == null) continue;

                                //protocol?
                                String protocol = jsonObject.get("Protocol").toString();
                                System.out.println("Key: " + protocol);

                                if (!(protocol.equals("aut") || protocol.equals("reg")) &&
                                        !client.ininitiaze) continue; //skip if client not initialize

                                if (protocol.equals("aut")) { //autorization
                                    String itog;
                                    if (bd.CheckData(jsonObject.get("Name").toString(),
                                            jsonObject.get("Password").toString())) {

                                        bd.initializeClient(client, jsonObject.get("Name").toString());
                                        itog = "{\"Protocol\":\"aut\",\"itog\":\"y\"}";
                                    }
                                    else
                                        itog = "{\"Protocol\":\"aut\",\"itog\":\"n\"}";
                                    writeTo(client, itog);
                                }

                                if (protocol.equals("reg")) { //registration
                                    String itog = null;
                                    String name = jsonObject.get("Name").toString();
                                    String password = jsonObject.get("Password").toString();

                                    if (bd.addAccount(name, password)) {
                                        bd.initializeClient(client, name);

                                        itog = "{\"Protocol\":\"reg\",\"itog\":\"y\"}";
                                    } else
                                        itog = "{\"Protocol\":\"reg\",\"itog\":\"n\"}";

                                    writeTo(client, itog);
                                }


                                if (protocol.equals("ListAccounts")) { //write list users to client
                                    JSONObject itog = new JSONObject();
                                    itog.put("Protocol", "ListAccounts");
                                    itog.put("accounts", bd.getListJSONAccounts());
                                    writeTo(client, itog.toString());
                                }

                                if (protocol.equals("infoMyAccount")) {
                                    JSONObject itog = new JSONObject();
                                    itog.put("Protocol", "infoMyAccount");
                                    itog.put("Your_name",  client.getNickname());
                                    itog.put("Your_score", client.getScore());
                                    itog.put("Your_group", client.getNameGroup());
                                    writeTo(client, itog.toString());
                                }

                                if (protocol.equals("sendScore")) {
                                    String from_whom = jsonObject.get("Me").toString(); //от кого
                                    String to_whom   = jsonObject.get("Who").toString(); //к кому
                                    String score     = jsonObject.get("qpoints").toString();//сколько

                                    bd.SendScore(from_whom, to_whom, Integer.valueOf(score));
                                }

                                if (protocol.equals("createGroup")) {
                                    String nameGroup = jsonObject.get("nameGroup").toString();
                                    String nameAdmin = client.getNickname();

                                    bd.addGroup(nameGroup, nameAdmin);
                                }
                                if (protocol.equals("updateGroup")) { //write list groups to client
                                    JSONObject itog = new JSONObject();
                                    JSONArray array = new JSONArray();

                                    for (int n = 0; n < groups.size(); n++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("name", groups.get(n).getName());
                                        obj.put("score", groups.get(n).getGroupScore());
                                        array.add(obj);
                                    }
                                    String NameAdminGroup = "null";
                                    if (client.getGroup() != null)
                                        NameAdminGroup = client.getGroup().getNameAdmin();

                                    itog.put("Protocol", "updateGroup");
                                    itog.put("groups", array);
                                    itog.put("admin", NameAdminGroup);
                                    writeTo(client, itog.toString());
                                }

                                if (protocol.equals("updateUsersGroup")) { //write users of group to client
                                    JSONObject itog = new JSONObject();
                                    String nameGroup = jsonObject.get("nameGroup").toString();

                                    String array = "null";
                                    for (int c = 0; c < groups.size(); c++)
                                        if (groups.get(c).getName().equals(nameGroup)) {
                                            array = groups.get(c).getListJSONUsersGroup();
                                            break;
                                        }

                                    itog.put("Protocol", "updateUsersGroup");
                                    itog.put("nameGroup", nameGroup);
                                    itog.put("users", array);
                                    writeTo(client, itog.toString());
                                }

                                if (protocol.equals("LeaveGroup")) {
                                    Group group = client.getGroup();
                                    if (group == null) continue; //если пользователь не состоит в группе //send error*

                                    //group.removeUser(client.getNickname()); //удаляем из группы
                                    bd.removeUserFromGroup(client.getNickname());
                                }

                                if (protocol.equals("JoinToGroup")) {
                                    if (client.getGroup() != null) continue; //send error*

                                    String namegroup = jsonObject.get("NameGroup").toString();
                                    String nameAccount = client.getNickname();
                                    String nameAdminGroup = "null";
                                    //Client adminGroup = null;

                                    for (int n = 0; n < groups.size(); n++)
                                        if (groups.get(n).getName().equals(namegroup)) {
                                            groups.get(n).addRequest(nameAccount);
                                            nameAdminGroup = groups.get(n).getNameAdmin();
                                            break;
                                        }
                                    /*
                                    for (Client client1 : clients)
                                        if (client1.getNickname().equals(nameAdminGroup)) {
                                            adminGroup = client1;
                                            break;
                                        }
                                    if (adminGroup == null) continue;

                                    //есть вариант сразу отправлять обновление данных
                                    writeTo(adminGroup, "{\"Protocol\":\"UpdateRequests\"");*/
                                }
                                if (protocol.equals("AcceptRequest")) {
                                    String nameNewUser = jsonObject.get("NameNewUser").toString();
                                    bd.addUserToGroup(nameNewUser, client.getNameGroup());
                                    client.getGroup().removeRequest(nameNewUser);
                                }
                                if (protocol.equals("RejectRequest")) {
                                    client.getGroup().removeRequest(jsonObject.get("NameNewUser").toString());
                                }

                                if (protocol.equals("InfoRequests")) {
                                    JSONObject itog = new JSONObject();
                                    if (!client.getNickname().equals(client.getGroup().getNameAdmin()))
                                        continue; //some defense

                                    itog.put("Requests", client.getGroup().getListJSONRequests());
                                    itog.put("Protocol", "InfoRequests");
                                    writeTo(client, itog.toString());
                                }
                                if(protocol.equals("SendScoreToGroup")) {
                                    bd.SendScoreToGroup(jsonObject.get("qpoints").toString(), client.getGroup(), client.getNickname());
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            }
        });
        reader.start();
    }

    public void listener() {

        listener = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Server is listening on port " + 5556);

                    F: while (true) {
                        Socket socket = serverSocket.accept();
                        System.out.println("New client connected");

                        //Check existing client
                        String address = socket.getInetAddress().getHostAddress();
                        for (int j = 0; j < clients.size(); j++)
                            if (address.equals(clients.get(j).ip)) {
                                clients.get(j).reconnect(socket);
                                continue F;
                            }

                        //add new client
                        Client client = new Client(socket);
                        clients.add(client);
                    }

                } catch (IOException ex) {
                    System.out.println("Server exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

        });
        listener.start();
    }

    private void CheckAliveClients() {
        if (pinger != null) return;

        pinger = new Thread(new Runnable() { //be object
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    for (int j = 0; j < clients.size(); j++)
                        if (!clients.get(j).timer.isAlive()) {
                            System.out.println("Disconnect");
                            clients.remove(j);
                        }
                }
            }
        });
        pinger.start();
    }

    public void writeTo(Client client, String message) {
        if (client == null)
            return;
        if (client.socket == null)
            return;


        try {
            output = client.socket.getOutputStream();
            output.write((message + "\n").getBytes());
            output.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**Метод закрытия и очистки сервера */
    public void CloseServer() { //возможно придется разослать всем клиентам протокол завершения
        bd.UpdateAllGroups();

        reader.interrupt();
        listener.interrupt();
        pinger.interrupt();
    }
}
