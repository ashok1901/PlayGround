package parkinglot;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ParkingLotSystem implements ParkingLotService {

    private final Map<String, ParkingSpot> spotsById = new LinkedHashMap<>();
    private final Map<String, String> occupiedSpotIdToTicketId = new HashMap<>();
    private final Map<String, TicketRecord> ticketIdToRecord = new HashMap<>();
    private final PaymentCalculator paymentCalculator;

    public ParkingLotSystem(Collection<ParkingSpot> spots, PaymentCalculator paymentCalculator) {
        this.paymentCalculator = Objects.requireNonNull(paymentCalculator, "paymentCalculator");
        for (ParkingSpot spot : spots) {
            if (spotsById.putIfAbsent(spot.getId(), spot) != null) {
                throw new IllegalArgumentException("Duplicate parking spot id: " + spot.getId());
            }
        }
    }

    /**
     * Fee for an active ticket (exit time unset uses {@link Instant#now()} in the calculator).
     *
     * @return computed price, or {@code 0} if the ticket id is unknown
     */
    public double computeParkingFee(String ticketId) {
        TicketRecord record = ticketIdToRecord.get(ticketId);
        if (record == null) {
            return 0;
        }
        return paymentCalculator.computePrice(record);
    }

    @Override
    public String assignSpot(ParkingSpotType vehicleType) {
        for (ParkingSpot spot : spotsById.values()) {
            if (occupiedSpotIdToTicketId.containsKey(spot.getId())) {
                continue;
            }
            if (!spotAcceptsVehicle(spot.getType(), vehicleType)) {
                continue;
            }
            String ticketId = UUID.randomUUID().toString();
            occupiedSpotIdToTicketId.put(spot.getId(), ticketId);
            ticketIdToRecord.put(
                    ticketId,
                    new TicketRecord(Instant.now(), spot.getId(), vehicleType, ticketId));
            return ticketId;
        }
        return null;
    }

    @Override
    public ParkingSpot locateVehicle(String ticketId) {
        TicketRecord record = ticketIdToRecord.get(ticketId);
        if (record == null) {
            return null;
        }
        return spotsById.get(record.getParkingSpotId());
    }

    @Override
    public boolean releaseSpot(String ticketId) {
        TicketRecord record = ticketIdToRecord.remove(ticketId);
        if (record == null) {
            return false;
        }
        record.setExitTime(Instant.now());
        occupiedSpotIdToTicketId.remove(record.getParkingSpotId());
        return true;
    }

    private static boolean spotAcceptsVehicle(ParkingSpotType spotType, ParkingSpotType vehicleType) {
        if (vehicleType == ParkingSpotType.HANDICAPPED) {
            return spotType == ParkingSpotType.HANDICAPPED;
        }
        if (spotType == ParkingSpotType.HANDICAPPED) {
            return false;
        }
        return sizeRank(spotType) >= sizeRank(vehicleType);
    }

    private static int sizeRank(ParkingSpotType type) {
        return switch (type) {
            case MOTORCYCLE -> 0;
            case COMPACT -> 1;
            case LARGE -> 2;
            case HANDICAPPED -> -1;
        };
    }
}
