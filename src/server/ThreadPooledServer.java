package server;

import service.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooledServer implements Runnable {
    private int duration, interval;
    protected int serverPort = 8080, poolSize = 10;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread= null;
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
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();
        while( !isStopped()){
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            this.threadPool.execute(
                    new WorkerRunnable(clientSocket,"Thread Pooled Server")
            );
        }

        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
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
