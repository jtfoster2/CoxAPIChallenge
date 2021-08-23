package cox.api.challenge.data_interfaces;

import java.util.ArrayList;
import java.util.List;

public class Dealers {
    private String dealerId;
    private String name;
    private List<Vehicles> vehicles;

    public void setVehicles(List<Vehicles> vehicles) {
        this.vehicles = vehicles;
    }

    public Dealers(String dealerId, String name, List<Vehicles> vehicles) {
        this.dealerId = dealerId;
        this.vehicles = vehicles;
        this.name = name;
    }
    public String getDealerId() {
        return dealerId;
    }

}
