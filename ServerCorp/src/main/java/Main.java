

public class Main {
    public static void main(String[] args) {

        Server server = new Server();
        server.listener();

        //Перехват завершения программы и после вызов протокола завершения.
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                server.CloseServer();
            }
        }, "Shutdown-thread"));
    }
}
