package parkinglot;

public class ParkingSpot {

    private final String id;
    private final ParkingSpotType type;
    private final String locationId;

    public ParkingSpot(String id, ParkingSpotType type, String locationId) {
        this.id = id;
        this.type = type;
        this.locationId = locationId;
    }

    public String getId() {
        return id;
    }

public ParkingSpotType getType() {
return type;
    }

    public String getLocationId() {
        return locationId;
    }
}
