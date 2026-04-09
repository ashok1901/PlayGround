package parkinglot;

import java.time.Instant;

public class TicketRecord {

    private final Instant entryTime;
    private final String parkingSpotId;
    private final ParkingSpotType vehicleType;
    private final String ticketId;
    private Instant exitTime;

    public TicketRecord(
            Instant entryTime,
            String parkingSpotId,
            ParkingSpotType vehicleType,
            String ticketId) {
        this.entryTime = entryTime;
        this.parkingSpotId = parkingSpotId;
        this.vehicleType = vehicleType;
        this.ticketId = ticketId;
        this.exitTime = null;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public String getParkingSpotId() {
        return parkingSpotId;
    }

    public ParkingSpotType getVehicleType() {
        return vehicleType;
    }

    /**
     * @return exit instant when released, or {@code null} while still parked
     */
    public Instant getExitTime() {
        return exitTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setExitTime(Instant exitTime) {
        this.exitTime = exitTime;
    }
}
