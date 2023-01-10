package domain;

public class AppointmentRequest implements Request{
    private final Appointment appointment;

    public AppointmentRequest(Appointment appointment) {
        this.appointment = appointment;
    }

    public Appointment getRequest() {
        return this.appointment;
    }
}
