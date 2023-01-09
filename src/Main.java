import client.Client;
import domain.Center;
import server.ThreadPooledServer;
import service.Controller;

public class Main {
    private static Controller controller;
    public static void main(String[] args) {
        int n = 3, m = 5;
        int p = 10;
        int n_clients = 10;
        int sim_duration_seconds = 3 * 60;
        int sim_client_interval = 2;
        int sim_server_interval = 5;
        int port = 9000;
        String address = "127.0.0.1";

        controller = Controller.getInstance();
        initializeCentersDefault(n);

        ThreadPooledServer server = new ThreadPooledServer(port, p, sim_duration_seconds, sim_server_interval, controller);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread[] clientThreads = new Thread[n_clients];
        Client cl = new Client(address, port, n, m, sim_duration_seconds, sim_client_interval,  controller);

        try {
            // CLIENT -> RANDOM APPOINTMENT (+ RANDOM CANCELATION)
            for(int i = 0; i < n_clients; ++i) {
                clientThreads[i] = new Thread(cl::makeRandomAppointment);
                clientThreads[i].start();
            }
            for(int i = 0; i < n_clients; ++i) {
                clientThreads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Stopping Server");
        server.stop();
    }

    private static void initializeCentersDefault(int n) {
        Center center1 = new Center(1);
        center1.initializeDefaultCenter();
        controller.addCenter(center1);

        int m = 5;
        for(int i = 2; i <= n; ++i) {
            Center center = new Center(i);
            center.initializeDefaultCenter();
            for(int j = 1; j <= m; ++j) {
                center.setMaxCapacity(j, (i-1) * center1.getMaxCapacity(j));
            }
            controller.addCenter(center);
        }
    }
}