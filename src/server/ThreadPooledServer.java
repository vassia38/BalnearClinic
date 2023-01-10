package server;

import service.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPooledServer implements Runnable {
    private final int duration;
    private final int interval;
    protected int serverPort = 8080, poolSize = 10;
    protected ServerSocket serverSocket = null;
    private ScheduledExecutorService scheduledVerification;
    private ExecutorService executor;
    protected AtomicBoolean shouldStop = new AtomicBoolean();
    protected ExecutorService threadPool;
    private final Controller controller;

    public ThreadPooledServer(int port, int poolSize, int duration, int interval, Controller controller) {
        if(port > 0)
            this.serverPort = port;
        if(poolSize > 0)
            this.poolSize = poolSize;
        threadPool = Executors.newFixedThreadPool(this.poolSize);
        this.duration = duration;
        this.interval = interval;
        this.controller = controller;
    }

    public void run() {
        scheduledVerification = Executors.newSingleThreadScheduledExecutor();
        scheduledVerification.scheduleAtFixedRate(
            () -> {
                try {
                    controller.checkIntegrity();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            },
            0, interval, TimeUnit.SECONDS
        );
        executor = Executors.newFixedThreadPool(poolSize);

        try {
            openServerSocket();
            while (!shouldStop.get()) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected ...");
                executor.submit(new WorkerRunnable(client, controller, shouldStop));
            }
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        } finally {
            System.out.println("Shutting down");
            stop();
        }
    }

    public synchronized void stop() {
        try {
            shouldStop.set(true);
            executor.shutdownNow();
            scheduledVerification.shutdownNow();
            serverSocket.close();
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
    }
}
