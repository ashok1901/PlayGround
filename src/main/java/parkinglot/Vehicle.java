package parkinglot;

public class Vehicle {

    private final String id;
    private final ParkingSpotType type;

    public Vehicle(String id, ParkingSpotType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public ParkingSpotType getType() {
        return type;
    }
}
