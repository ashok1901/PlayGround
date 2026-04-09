package parkinglot;

public class ParkingSpotLocation {

    private final int floor;
    private final int row;
    private final int column;

    public ParkingSpotLocation(int floor, int row, int column) {
        this.floor = floor;
        this.row = row;
        this.column = column;
    }

    public int getFloor() {
        return floor;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
