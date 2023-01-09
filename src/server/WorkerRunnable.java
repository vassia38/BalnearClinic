package server;

import domain.Appointment;
import domain.Treatment;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WorkerRunnable implements Runnable{
    protected Socket clientSocket;
    protected String serverText;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {
        try {
            ObjectInputStream input  = new ObjectInputStream(clientSocket.getInputStream());
            Appointment appointment = (Appointment) input.readObject();
            System.out.println("Received appointment for center " + appointment.getCenterId() + ": time=" + appointment.getTime() + " " + appointment.getTreatment());

            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();
            output.write((  "HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                            this.serverText + " - " +
                            time +
                            "").getBytes());
            System.out.println("Request processed: " + time);

            output.close();
            input.close();
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}