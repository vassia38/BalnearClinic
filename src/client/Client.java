package client;

import java.net.*;
import java.io.*;

import domain.Appointment;
import domain.AppointmentRequest;
import service.Controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

public class Client {
    private final int n, m, port;
    private final int duration;
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

    public void simulate() {
        String nametag = "Client Thread " + Thread.currentThread().getId();
        int current_thread_duration = duration;

        try {
            while (current_thread_duration > 0) {
                System.out.println(nametag + " duration left: " + current_thread_duration);
                Thread.sleep(interval * 1000L);
                current_thread_duration -= interval;

                Socket socket = new Socket(address, port);
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                System.out.println(nametag + " Connected");

                output.writeObject(new AppointmentRequest(getRandomAppointment()));
                Object response = input.readObject();

                socket.close();
                input.close();
                output.close();
                System.out.println(nametag + " Disconnected");
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Appointment getRandomAppointment() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        long date = zdt.toInstant().toEpochMilli();
        Random rn = new Random(date);

        int centerId = rn.nextInt(1, n + 1);
        int treatmentId = rn.nextInt(1, m + 1);

        int hour = rn.nextInt(10, 19);
        int minute = rn.nextInt(0, 59);
        minute = minute / 10 * 10; // appointments can only be made in steps of 10 minutes
        LocalTime time = LocalTime.of(hour, minute);

        Appointment appointment = new Appointment(String.valueOf(Thread.currentThread().getId()),centerId, treatmentId, time);
        int cancel = rn.nextInt() % 2;
        if (cancel == 1) {
            appointment.cancel();
        }

        return appointment;
    }
}
