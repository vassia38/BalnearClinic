package client;

import java.net.*;
import java.io.*;

import domain.Appointment;
import domain.Treatment;
import service.Controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

public class Client {
    private final int n, m, port;
    private int duration;
    private final int interval;
    private final Controller controller;
    private final String address;

    public Client(String address, int port, int n, int m, int duration, int interval, Controller controller) {
        this.address = address;
        this.port = port;
        this.n = n;
        this.m = m;
        this.duration = duration;
        this.interval = interval;
        this.controller = controller;
    }

    public void makeRandomAppointment(){
        Socket socket;
        DataInputStream input;
        ObjectOutputStream output;

        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        long date = zdt.toInstant().toEpochMilli();
        Random rn = new Random(date);

        try {
            while (duration > 0) {
                Thread.sleep(interval * 1000L);
                duration -= interval;

                socket = new Socket(address, port);
                System.out.println("Connected");
                input = new DataInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());

                int centerId = rn.nextInt(1, n + 1);
                int treatmentId = rn.nextInt(1, m + 1);
                Treatment tr = controller.getCenter(centerId).getTreatment(treatmentId);

                int hour = rn.nextInt(10, 19);
                int minute = rn.nextInt(0, 59);
                minute = minute / 10 * 10; // appointments can only be made in steps of 10 minutes
                LocalTime time = LocalTime.of(hour, minute);

                Appointment appointment = new Appointment(centerId, tr, time);
                int cancel = rn.nextInt() % 2;
                if (cancel == 1) {
                    appointment.cancel();
                }
                output.writeObject(appointment);

                socket.close();
                input.close();
                output.close();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
