package domain;

public class AppointmentResponse implements Response{
    private final AppointmentStatus status;

    public AppointmentResponse(AppointmentStatus status) {
        this.status = status;
    }
    public AppointmentStatus getStatus() {
        return this.status;
    }
}
