package domain;

import java.io.Serializable;

public class Treatment implements Serializable {
    private final int id, cost, duration;

    public Treatment(int id, int cost, int duration) {
        this.id = id;
        this.cost = cost;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "id=" + id +
                ", cost=" + cost +
                ", duration=" + duration +
                '}';
    }
}
