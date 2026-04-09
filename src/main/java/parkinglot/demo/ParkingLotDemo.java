package parkinglot.demo;

import java.util.List;

import parkinglot.ParkingLotSystem;
import parkinglot.ParkingSpot;
import parkinglot.ParkingSpotType;
import parkinglot.StaticRatePaymentCalculator;

public final class ParkingLotDemo {

    private ParkingLotDemo() {}

    public static void main(String[] args) {
        List<ParkingSpot> spots = List.of(
                new ParkingSpot("M1", ParkingSpotType.MOTORCYCLE, "L1-M1"),
                new ParkingSpot("C1", ParkingSpotType.COMPACT, "L1-C1"),
                new ParkingSpot("C2", ParkingSpotType.COMPACT, "L1-C2"),
                new ParkingSpot("L1", ParkingSpotType.LARGE, "L1-L1"));

        ParkingLotSystem lot = new ParkingLotSystem(spots, new StaticRatePaymentCalculator());

        String ticket = lot.assignSpot(ParkingSpotType.COMPACT);
        System.out.println("Assigned COMPACT, ticket: " + ticket);

        var spot = lot.locateVehicle(ticket);
        System.out.println("Locate vehicle: " + describeSpot(spot));

        double feeWhileParked = lot.computeParkingFee(ticket);
        System.out.printf("Fee while parked (uses 'now' if no exit): $%.2f%n", feeWhileParked);

        boolean released = lot.releaseSpot(ticket);
        System.out.println("Released spot: " + released);

        System.out.println("Locate after release: " + describeSpot(lot.locateVehicle(ticket)));

        String ticket2 = lot.assignSpot(ParkingSpotType.LARGE);
        System.out.println("Assigned LARGE, ticket: " + ticket2);
        System.out.println("Locate: " + describeSpot(lot.locateVehicle(ticket2)));

        String noRoom = lot.assignSpot(ParkingSpotType.HANDICAPPED);
        System.out.println("Assign HANDICAPPED (no such spot): " + noRoom);
    }

    private static String describeSpot(ParkingSpot s) {
        if (s == null) {
            return "null";
        }
        return s.getId() + " @ " + s.getLocationId() + " (" + s.getType() + ")";
    }
}
