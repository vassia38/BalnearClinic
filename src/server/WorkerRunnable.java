package server;

import domain.*;
import service.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerRunnable implements Runnable{
    private final Socket clientSocket;
    private final Controller controller;
    private AppointmentRequest lastSuccessfulRequest;
    private final AtomicBoolean shouldStop;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public WorkerRunnable(Socket clientSocket, Controller controller, AtomicBoolean shouldStop) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.shouldStop = shouldStop;
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            Object request = input.readObject();
            Object response = handleRequest((Request) request);
            if (response != null) {
                sendResponse((Response) response);
            }

            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    private Object handleRequest(Request request) {
        if (request instanceof AppointmentRequest appointmentRequest) {
            System.out.println("Handling appointment request " + appointmentRequest.getRequest());
            var status = controller.processAppointmentRequest(appointmentRequest);
            if (status.getStatus() == AppointmentStatus.SUCCESS) {
                // TODO: use mutex here
                lastSuccessfulRequest = appointmentRequest;
            }

            return status;
        }
        if (request instanceof PaymentRequest) {
            System.out.println("Handling pay request " + request);
            return controller.processPayment(lastSuccessfulRequest);
        }
        if (request instanceof CancellationRequest) {
            System.out.println("Handling cancel payment " + request);
            return controller.cancelPayment(lastSuccessfulRequest);
        }
        return null;
    }

    private void sendResponse(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}