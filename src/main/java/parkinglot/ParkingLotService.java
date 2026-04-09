package parkinglot;

public interface ParkingLotService {

    /**
     * @return ticket id when a spot is assigned; {@code null} if no compatible spot is free
     */
    String assignSpot(ParkingSpotType vehicleType);

    /**
     * @return the spot for this ticket, or {@code null} if unknown or already released
     */
    ParkingSpot locateVehicle(String ticketId);

    /**
     * @return {@code true} if a vehicle was parked on this ticket and the spot was freed
     */
    boolean releaseSpot(String ticketId);
}
