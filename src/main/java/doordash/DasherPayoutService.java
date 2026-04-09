package doordash;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DasherPayoutService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final double PAYOUT_PER_MINUTE = 0.3;
    private static final double MILLIS_PER_MINUTE = 60_000.0;

    private final List<String> deliveryData;
    private Map<Integer, List<DasherDeliveryRecord>> recordsByDasherId;
    

    public DasherPayoutService(List<String> dashers) {
        this.deliveryData = dashers;
        parseDeliveryDataByDasherId();
    }

    public List<String> getDeliveryData() {
        return deliveryData;
    }

    protected DasherDeliveryRecord parseDeliveryRecord(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "Expected format: dasherId,deliveryId,timestamp,status");
        }

        int dasherId = Integer.parseInt(parts[0].trim());
        int deliveryId = Integer.parseInt(parts[1].trim());
        long timestamp = parseEpochTime(parts[2].trim());
        DeliveryStatus status = DeliveryStatus.valueOf(parts[3].trim().toUpperCase());

        long startTime = status == DeliveryStatus.ACCEPTED ? timestamp : 0L;
        long endTime = status != DeliveryStatus.ACCEPTED ? timestamp : 0L;
        return new DasherDeliveryRecord(
                dasherId,
                deliveryId,
                startTime,
                endTime,
                status);
    }

    protected long parseEpochTime(String dateTime) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime.trim(), DATE_TIME_FORMATTER);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Invalid time format. Expected yyyy-MM-dd HH:mm:ss", ex);
        }
    }

    public Map<Integer, List<DasherDeliveryRecord>> parseDeliveryDataByDasherId() {
        recordsByDasherId = new HashMap<>();
        Map<String, DasherDeliveryRecord> acceptedByKey = new HashMap<>();
        Map<String, DasherDeliveryRecord> fulfilledByKey = new HashMap<>();

        for (String line : deliveryData) {
            if (line == null || line.isBlank()) {
                continue;
            }
            DasherDeliveryRecord record = parseDeliveryRecord(line);
            String key = record.getDasherId() + ":" + record.getDeliveryId();
            if (record.getStatus() == DeliveryStatus.ACCEPTED) {
                acceptedByKey.put(key, record);
            } else if (record.getStatus() == DeliveryStatus.FULFILLED) {
                fulfilledByKey.put(key, record);
            }
        }

        for (Map.Entry<String, DasherDeliveryRecord> acceptedEntry : acceptedByKey.entrySet()) {
            DasherDeliveryRecord acceptedRecord = acceptedEntry.getValue();
            DasherDeliveryRecord fulfilledRecord = fulfilledByKey.get(acceptedEntry.getKey());
            if (fulfilledRecord == null) {
                continue;
            }
            DasherDeliveryRecord mergedRecord = new DasherDeliveryRecord(
                    acceptedRecord.getDasherId(),
                    acceptedRecord.getDeliveryId(),
                    acceptedRecord.getStartTime(),
                    fulfilledRecord.getEndTime(),
                    DeliveryStatus.FULFILLED);
            recordsByDasherId
                    .computeIfAbsent(mergedRecord.getDasherId(), ignored -> new ArrayList<>())
                    .add(mergedRecord);
        }
        return recordsByDasherId;
    }

    public double getPayout(int dasherId) {
        if (recordsByDasherId == null) {
            parseDeliveryDataByDasherId();
        }

        List<DasherDeliveryRecord> records =
                recordsByDasherId.getOrDefault(dasherId, Collections.emptyList());
        if (records.isEmpty()) {
            return 0.0;
        }

        List<TimeEvent> events = new ArrayList<>();
        for (DasherDeliveryRecord record : records) {
            long start = record.getStartTime();
            long end = record.getEndTime();
            if (end <= start) {
                continue;
            }
            events.add(new TimeEvent(start, 1));   // delivery becomes active
            events.add(new TimeEvent(end, -1));    // delivery is no longer active
        }
        if (events.isEmpty()) {
            return 0.0;
        }

        events.sort((a, b) -> {
            if (a.time != b.time) {
                return Long.compare(a.time, b.time);
            }
            // For equal timestamps, process end events before start events.
            return Integer.compare(a.delta, b.delta);
        });

        double payout = 0.0;
        int activeDeliveries = 0;
        long prevTime = events.get(0).time;

        for (TimeEvent event : events) {
            long currentTime = event.time;
            long deltaMillis = currentTime - prevTime;
            if (deltaMillis > 0 && activeDeliveries > 0) {
                double minutesInInterval = deltaMillis / MILLIS_PER_MINUTE;
                payout += minutesInInterval * PAYOUT_PER_MINUTE * activeDeliveries;
            }
            activeDeliveries += event.delta;
            prevTime = currentTime;
        }

        return payout;
    }

    private static final class TimeEvent {
        private final long time;
        private final int delta;

        private TimeEvent(long time, int delta) {
            this.time = time;
            this.delta = delta;
        }
    }

}
