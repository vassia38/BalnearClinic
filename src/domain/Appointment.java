package domain;

import java.io.Serializable;
import java.time.LocalTime;

public class Appointment implements Serializable {
    private final int centerId;
    private final Treatment treatment;
    private final LocalTime time;
    private int cancel;

    public Appointment(int centerId, Treatment treatment, LocalTime time) {
        this.centerId = centerId;
        this.treatment = treatment;
        this.time = time;
        this.cancel = 0;
    }

    public int getCenterId() {
        return centerId;
    }
    public Treatment getTreatment() {
        return treatment;
    }
    public void cancel() {
        this.cancel = 1;
    }

    public LocalTime getTime() {
        return time;
    }
}
