import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class Server {
    private volatile ServerSocket serverSocket;
    private volatile OutputStream output;
    private volatile List<Client> clients;
    private volatile List<Client> queue; //the connection stage
    private volatile List<Group> groups; //link
    private volatile BaseData bd;
    private Thread pinger, reader, listener, reader_security;

    //rsa, aes keys
    public SecretKey AesKey;
    public IvParameterSpec ivParameterSpec;
    public String algorithm;
    public Map<String, Object> RsaKey;
    public String RsaPrivateKey;
    public String RsaPublicKey;

    public Server() {
        try {
            serverSocket = new ServerSocket(5556);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clients = new ArrayList<>();
        queue = new ArrayList<>();
        groups = new ArrayList<>();
        bd = new BaseData();
        groups = bd.getGroups();
        //groups.get(0).addRequest("sdf"); groups.get(0).addRequest("Sd"); ///test

        reader();
        ReaderSecurity();
        CheckAliveClients();

        try {
            //aes
            AesKey = aes.generateKey(128);
            ivParameterSpec = aes.generateIv();
            algorithm = "AES/CBC/PKCS5Padding";

            //rsa
            RsaKey = rsa.initKey();
            RsaPrivateKey = rsa.getPrivateKey(RsaKey);
            RsaPublicKey  = rsa.getPublicKey(RsaKey);
        } catch (NoSuchAlgorithmException e) {
            //throw new RuntimeException(e);
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
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

                                request = aes.decrypt(algorithm, request, AesKey);

                                //Json request
                                JSONObject jsonObject = (JSONObject) JSONValue.parse(request);
                                if (jsonObject == null) continue;

                                //protocol?
                                String protocol = jsonObject.get("Protocol").toString();
                                System.out.println("Key: " + protocol);


                                if (protocol.equals("ListAccounts")) { //write list users to client
                                    ProtocolInfoListAccounts(client);
                                }

                                if (protocol.equals("infoMyAccount")) {
                                    ProtocolInfoAccount(client);
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
                                    ProtocolUpdateListGroups(client);
                                }

                                if (protocol.equals("updateUsersGroup")) { //write users of group to client
                                    String nameGroup = jsonObject.get("nameGroup").toString();
                                    ProtocolUpdateUsersGroup(client, nameGroup);
                                }

                                if (protocol.equals("LeaveGroup")) {
                                    Group group = client.getGroup();
                                    if (group == null) continue; //если пользователь не состоит в группе //send error*
                                    bd.removeUserFromGroup(client.getNickname());
                                    ProtocolInfoAccount(client);
                                }

                                if (protocol.equals("JoinToGroup")) {
                                    if (client.getGroup() != null) continue; //send error*

                                    String namegroup = jsonObject.get("NameGroup").toString();
                                    String nameAccount = client.getNickname();
                                    String nameAdminGroup = "null";

                                    for (int n = 0; n < groups.size(); n++)
                                        if (groups.get(n).getName().equals(namegroup)) {
                                            groups.get(n).addRequest(nameAccount);
                                            nameAdminGroup = groups.get(n).getNameAdmin();
                                            break;
                                        }
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
                                    if (!client.getNickname().equals(client.getGroup().getNameAdmin()))
                                        continue; //some defense

                                    ProtocolInfoRequests(client);
                                }
                                if(protocol.equals("SendScoreToGroup")) {
                                    bd.SendScoreToGroup(jsonObject.get("qpoints").toString(), client.getGroup(), client.getNickname());
                                }
                                if (protocol.equals("Leave")) {
                                    clients.remove(client);
                                }

                            }
                            Thread.sleep(20);
                        } catch (IOException e) {
                        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                                 IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                                 InvalidKeyException e) {
                            //e.printStackTrace();
                            System.out.println("LOG: Error aes decrypting ... Client: " + clients.get(j).ip);
                        } catch (InterruptedException e) {
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

                    while (true) {
                        Socket socket = serverSocket.accept();
                        System.out.println("New client connected");

                        //Check existing client
                        String address = socket.getInetAddress().getHostAddress();
                        for (Client client : clients)
                            if (address.equals(client.ip)) {
                                //client.reconnect(socket);
                                clients.remove(client);
                                break;
                            }

                        //add new client
                        Client client = new Client(socket);
                        queue.add(client);

                        writeTo(client, "{\"Protocol\":\"RsaKey\",\"Key\":\"" + RsaPublicKey + "\"}");
                    }

                } catch (IOException ex) {
                    System.out.println("Server exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        listener.start();
    }

    private void ReaderSecurity() {
        if (reader_security != null) return;

        reader_security = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    for (int j = 0; j < queue.size(); j++) {
                        Client client = queue.get(j);

                        try {
                            if (client.reader.ready()) {
                                //Примем запрос
                                int length_pk = client.dIn.readInt();
                                byte[] publickeyserver = new byte[length_pk];
                                client.dIn.readFully(publickeyserver, 0, publickeyserver.length);
                                String str = new String(publickeyserver);

                                //Json request
                                JSONObject jsonObject = (JSONObject) JSONValue.parse(str);
                                if (jsonObject == null) continue;

                                //protocol?
                                String protocol = jsonObject.get("Protocol").toString();
                                System.out.println("Key: " + protocol);

                                if (protocol.equals("RsaKey")) { //обмен ключами rsa и aes

                                    client.RsaPublicKeyClient = jsonObject.get("Key").toString();

                                    //тут пересылаем AES ключ
                                    String encodedKey = Base64.getEncoder().encodeToString(AesKey.getEncoded());
                                    byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
                                    /*byte[] encbyte = rsa.encryptByPublicKey(decodedKey, client.RsaPublicKeyClient);*/

                                    client.dOut.writeInt(decodedKey.length);
                                    client.dOut.write(decodedKey);
                                }


                                if (protocol.equals("aut")) {
                                    String itog;
                                    if (client.RsaPublicKeyClient != null) {
                                        //Расшифруем ...
                                        String name = jsonObject.get("Name").toString();
                                        String password = jsonObject.get("Password").toString();

                                        String nameD = aes.decrypt(algorithm, name, AesKey);
                                        String passD = aes.decrypt(algorithm, password, AesKey);

                                        //Проверка данных
                                        if (bd.CheckAutAccount(nameD, passD)) {
                                            bd.initializeClient(client, nameD);

                                            itog = "{\"Protocol\":\"aut\",\"itog\":\"y\"}";
                                            clients.add(client);
                                            queue.remove(client);
                                        }
                                        else
                                            itog = "{\"Protocol\":\"aut\",\"itog\":\"n\"}";

                                        writeToWithAes(client, itog);
                                    }
                                }

                                if (protocol.equals("reg")) { //registration
                                    String itog = null;
                                    if (client.RsaPublicKeyClient != null) {
                                        String name = jsonObject.get("Name").toString();
                                        String password = jsonObject.get("Password").toString();

                                        name = aes.decrypt(algorithm, name, AesKey);
                                        password = aes.decrypt(algorithm, password, AesKey);

                                        if (bd.addAccount(name, password)) {

                                            bd.initializeClient(client, name);

                                            itog = "{\"Protocol\":\"reg\",\"itog\":\"y\"}";
                                        } else
                                            itog = "{\"Protocol\":\"reg\",\"itog\":\"n\"}";

                                        //после чего перемещаем его из очереди в контейнер пользователей
                                        clients.add(client);
                                        queue.remove(client);

                                        writeToWithAes(client, itog);
                                    }
                                }

                            }
                        } catch (IOException e) {
                            System.out.println("Error");
                        } catch (Exception e) {
                            System.out.println("Error");
                        }
                    }
                    try {
                        Thread.sleep(20); //60 циклов в сек*
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        reader_security.start();
    }

    private void CheckAliveClients() { //пинговщик
        if (pinger != null) return;

        pinger = new Thread(new Runnable() { //be object
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000); //частота 1 в сек
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

    public void writeToWithAes(Client client, String message)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String Aes_message = aes.encrypt(algorithm, message, AesKey);
        writeTo(client, Aes_message);
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

    /** Протоколы отправки каких либо данных */
    public void ProtocolInfoAccount(Client client)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        JSONObject itog = new JSONObject();
        itog.put("Protocol", "infoMyAccount");
        itog.put("Your_name",  client.getNickname());
        itog.put("Your_score", client.getScore());
        itog.put("Your_group", client.getNameGroup());
        writeToWithAes(client, itog.toString());
    }
    public void ProtocolInfoListAccounts(Client client)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        JSONObject itog = new JSONObject();
        itog.put("Protocol", "ListAccounts");
        itog.put("accounts", bd.getListJSONAccounts());

        writeToWithAes(client, itog.toString());
    }
    public void ProtocolUpdateListGroups(Client client)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
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
        writeToWithAes(client, itog.toString());
    }
    public void ProtocolUpdateUsersGroup(Client client, String name_group)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        JSONObject itog = new JSONObject();

        String array = "null";
        for (int c = 0; c < groups.size(); c++)
            if (groups.get(c).getName().equals(name_group)) {
                array = groups.get(c).getListJSONUsersGroup();
                break;
            }

        itog.put("Protocol", "updateUsersGroup");
        itog.put("nameGroup", name_group);
        itog.put("users", array);
        writeToWithAes(client, itog.toString());
    }
    public void ProtocolInfoRequests(Client client)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        JSONObject itog = new JSONObject();
        itog.put("Requests", client.getGroup().getListJSONRequests());
        itog.put("Protocol", "InfoRequests");
        writeToWithAes(client, itog.toString());
    }


    /**Метод закрытия и очистки сервера */
    public void CloseServer() { //возможно придется разослать всем клиентам протокол завершения
        bd.UpdateAllGroups();
        bd.UpdateAllAccounts();

        reader.interrupt();
        listener.interrupt();
        pinger.interrupt();
    }
}
