package service;

import domain.Center;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final Map<Integer, Center> centers;
    private static Controller instance = null;

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
}
