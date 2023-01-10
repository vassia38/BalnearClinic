package domain;

import java.io.Serializable;
import java.time.LocalTime;

public class Appointment implements Serializable {
    private final String cnp;
    private final int centerId;
    private final int treatmentId;
    private final LocalTime time;
    private int cancel;

    public Appointment(String cnp, int centerId, int treatmentId, LocalTime time) {
        this.cnp = cnp;
        this.centerId = centerId;
        this.treatmentId = treatmentId;
        this.time = time;
        this.cancel = 0;
    }

    public String getCnp() {
        return cnp;
    }
    public int getCenterId() {
        return centerId;
    }
    public int getTreatmentId() {
        return treatmentId;
    }
    public void cancel() {
        this.cancel = 1;
    }

    public LocalTime getTime() {
        return time;
    }
}
