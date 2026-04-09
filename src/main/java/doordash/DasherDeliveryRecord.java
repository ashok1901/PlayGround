package doordash;

public class DasherDeliveryRecord {

    private final int dasherId;
    private final int deliveryId;
    private final long startTime;
    private final long endTime;
    private final DeliveryStatus status;

    public DasherDeliveryRecord(int dasherId, int deliveryId, long startTime, long endTime, DeliveryStatus status) {
        this.dasherId = dasherId;
        this.deliveryId = deliveryId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public int getDasherId() {
        return dasherId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public DeliveryStatus getStatus() {
        return status;
    }
}

