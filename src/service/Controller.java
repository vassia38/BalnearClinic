package service;

import domain.*;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Controller {
    private final Map<Integer, Center> centers;
    private static Controller instance = null;
    private final Lock appointmentLock = new ReentrantLock();
    private final Lock paymentLock = new ReentrantLock();
    private final Lock verificationLock = new ReentrantLock();

    private Controller() {
        centers = new HashMap<>();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void addCenter(Center center) {
        this.centers.put(center.getId(), center);
    }

    public Center getCenter(int id) {
        return this.centers.get(id);
    }

    public AppointmentResponse processAppointmentRequest(AppointmentRequest request) {
        Appointment app = request.getRequest();
        int centerId = app.getCenterId();
        Center center = this.getCenter(centerId);
        if(center == null) {
            return new AppointmentResponse(AppointmentStatus.FAILURE);
        }

        int treatmentId = app.getTreatmentId();
        Treatment treatment = center.getTreatment(treatmentId);
        if(treatment == null) {
            return new AppointmentResponse(AppointmentStatus.FAILURE);
        }

        var maxClientsPerTreatment = center.getMaxCapacity(treatment.getId());
        var minutesStart = app.getTime();
        var duration = treatment.getDuration();

        try {
            appointmentLock.lock();
            List<Appointment> filtered = center.getAppointmentsByTreatment(treatmentId);
            ArrayList<Interval> intervals = new ArrayList<>();
            for(Appointment ap : filtered) {
                intervals.add(new Interval(ap, treatment));
            }
            var maxOverlap = maximumOverlappingIntervals(intervals);
            System.out.println("Max overlap is " + maxOverlap);
            if (maxOverlap > maxClientsPerTreatment) {
                return new AppointmentResponse(AppointmentStatus.FAILURE);
            }
            center.addAppointment(app);
            // TODO FILE SAVING
            // saveAppointmentRequest(request);
            return new AppointmentResponse(AppointmentStatus.SUCCESS);
        } finally {
            appointmentLock.unlock();
        }
    }

    public Response processPayment(AppointmentRequest request) {
        /*var sum = config.getTreatmentsCost()[lastProgramRequest.getTreatmentType()];
        try {
            paymentLock.lock();
            savePayment(new Payment(
                            LocalDate.now(),
                            lastProgramRequest.getCnp(),
                            sum,
                            lastProgramRequest.getLocation(),
                            lastProgramRequest.getTreatmentType(),
                            lastProgramRequest.getTreatmentTime()
                    )
            );
            return new PaymentResponse(PaymentStatus.SUCCESS);
        } finally {
            paymentLock.unlock();
        }*/
        // remove this next line
        return new PaymentResponse(PaymentStatus.SUCCESS);
    }

    public Response cancelPayment(AppointmentRequest request) {
        /*try {
            appointmentLock.lock();
            paymentLock.lock();
            int indexToDelete = -1;
            for (int i = 0; i < intervals.size(); i++) {
                var interval = intervals.get(i);
                if (interval.getTreatmentType() == lastProgramRequest.getTreatmentType() &&
                        interval.getCnp().equals(lastProgramRequest.getCnp()) &&
                        interval.getLocation() == lastProgramRequest.getLocation() &&
                        lastProgramRequest.getTreatmentTime().equals(getHourFromMinutes(interval.getMinutesStart()))) {
                    indexToDelete = i;
                    break;
                }
            }
            intervals.remove(indexToDelete);

            deleteProgramFromFile(lastProgramRequest);
            var sum = config.getTreatmentsCost()[lastProgramRequest.getTreatmentType()];
            savePayment(new Payment(
                            LocalDate.now(),
                            lastProgramRequest.getCnp(),
                            (-1) * sum,
                            lastProgramRequest.getLocation(),
                            lastProgramRequest.getTreatmentType(),
                            lastProgramRequest.getTreatmentTime()
                    )
            );
        } finally {
            paymentLock.unlock();
            appointmentLock.unlock();
        }*/

        return new PaymentResponse(PaymentStatus.SUCCESS);
    }

    private Integer maximumOverlappingIntervals(List<Interval> intervals) {
        int max_event_tm = 0;
        var count = getIntervalOverlapArray(intervals);
        if(count.length == 0) {
            return 0;
        }
        for (int i = 0; i < count.length; i++) {
            if (count[max_event_tm] < count[i]) {
                max_event_tm = i;
            }
        }

        return count[max_event_tm];
    }

    private int[] getIntervalOverlapArray(List<Interval> intervals) {
        // Find the time when the last interval ends
        ArrayList<LocalTime> endTimes = new ArrayList<>();
        ArrayList<LocalTime> startTime = new ArrayList<>();

        for(Interval interval : intervals) {
            endTimes.add(interval.getTimeEnd());
            startTime.add(interval.getTimeStart());
        }
        if(endTimes.isEmpty() || startTime.isEmpty()) {
            return new int[0];
        }
        LocalTime maxEndTime = endTimes.get(0);
        if(endTimes.size() >= 2) {
            for(int i = 1; i < endTimes.size(); ++i) {
                if(endTimes.get(i).isAfter(maxEndTime)) {
                    maxEndTime = endTimes.get(i);
                }
            }
        }

        int[] count = new int[maxEndTime.getHour()*60+maxEndTime.getMinute() + 2];
        // Fill the count array range count using the array index to store time
        for (int i = 0; i < endTimes.size(); i++) {
            LocalTime st = startTime.get(i);
            LocalTime end = endTimes.get(i);
            for (int j = st.getHour()*60 + st.getMinute(); j <= end.getHour()*60 + end.getMinute(); j++) {
                count[j]++;
            }
        }
        return count;
    }
    public void checkIntegrity() {
        System.out.println("Server: Integrity check...");
        /*
        LOCK ALL RESOURCES ? =>
        CLIENTS HAVE TO WAIT FOR INTEGRITY CHECK TO FINISH...
        */
    }

    private static class Interval {
        private LocalTime timeStart;
        private LocalTime timeEnd;

        public Interval(Appointment appointment, Treatment treatment) {
            if(appointment.getTreatmentId() != treatment.getId()) {
                return;
            }
            this.timeStart = appointment.getTime();
            this.timeEnd = this.timeStart.plusMinutes(treatment.getDuration());
        }
        public LocalTime getTimeStart() {
            return timeStart;
        }
        public LocalTime getTimeEnd() {
            return timeEnd;
        }
    }
}

