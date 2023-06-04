

import java.io.*;
import java.net.Socket;


public class Client {
    public String ip;
    public int port;
    public Socket socket;
    public BufferedReader reader;
    public Timer timer;
    public boolean ininitiaze; //инициализирован?
    public DataInputStream dIn;
    public DataOutputStream dOut;

    /**Запрещено изменение в Client классе.*/
    private InfoAccount account;

    public String RsaPublicKeyClient;

    public Client(Socket socket) {
        reconnect(socket);
    }
    public void reconnect(Socket socket) {
        this.socket = socket;
        timer = new Timer(200);
        account = null;
        ininitiaze = false;

        if (socket != null) {
            ip = socket.getInetAddress().getHostAddress();
            port = socket.getPort();
            RsaPublicKeyClient = null;

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                dIn = new DataInputStream(socket.getInputStream());
                dOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setInfoAccount(InfoAccount info) { this.account = info; }

    public String getNickname() {
        if (!ininitiaze) return null;
        return new String(account.nickname);
    }
    public int getScore() {
        if (!ininitiaze) return 0;
        return account.score;
    }
    public String getNameGroup() {
        if (!ininitiaze) return null;
        if (account.group == null)
            return "null";
        return account.group.getName();
    }
    public Group getGroup() {
        return account.group;
    }
}
