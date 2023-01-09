package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Center {
    private final int id;
    private final Map<Integer, Treatment> treatments;
    private final Map<Treatment, Integer> maxCapacity;
    private final ArrayList<Appointment> appointments;

    public Center(int id) {
        this.id = id;
        this.treatments = new HashMap<>();
        this.maxCapacity = new HashMap<>();
        this.appointments = new ArrayList<>();
    }

    public void addTreatment(Treatment treatment) {
        if(!treatments.containsKey(treatment.getId())) {
            this.treatments.put(treatment.getId(), treatment);
            this.maxCapacity.put(treatment, 1);
        }
    }

    public Treatment getTreatment(int id) {
        return this.treatments.get(id);
    }

    public void initializeDefaultCenter() {
        this.addTreatment(new Treatment(1,50,120));
        this.addTreatment(new Treatment(2,20,20));
        this.addTreatment(new Treatment(3,40,30));
        this.addTreatment(new Treatment(4,100,60));
        this.addTreatment(new Treatment(5,30,30));
        this.setMaxCapacity(1, 3);
        this.setMaxCapacity(2, 1);
        this.setMaxCapacity(3, 1);
        this.setMaxCapacity(4, 2);
        this.setMaxCapacity(5, 1);
    }

    public void setMaxCapacity(int idTreatment, int capacity) {
        Treatment tr = treatments.get(idTreatment);
        if(tr != null) {
            maxCapacity.put(tr, capacity);
        }
    }

    public void addAppointment(Appointment appointment) {
        Treatment tr = appointment.getTreatment();
        if(tr != null && treatments.containsKey(tr.getId())) {
            appointments.add(appointment);
        }
    }

    public int getId() {
        return id;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public Map<Integer, Treatment> getTreatments() {
        return treatments;
    }

    public Map<Treatment, Integer> getMaxCapacities() {
        return maxCapacity;
    }

    public int getMaxCapacity(int idTreatment) {
        Treatment tr = treatments.get(idTreatment);
        if(tr == null) {
            return 0;
        }
        Integer cap = maxCapacity.get(tr);
        return cap == null ? 0 : cap;
    }

    @Override
    public String toString() {
        return "Center{" +
                "id=" + id +
                '}';
    }
}
