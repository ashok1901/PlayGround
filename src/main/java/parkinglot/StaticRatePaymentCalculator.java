package parkinglot;

import java.time.Duration;
import java.time.Instant;

public class StaticRatePaymentCalculator implements PaymentCalculator {

    private static final Duration FREE_PERIOD = Duration.ofMinutes(10);
    private static final Duration BILLING_BLOCK = Duration.ofMinutes(10);
    private static final double PRICE_PER_BLOCK = 1.0;
    private static final double MAX_PRICE = 30.0;

    @Override
    public double computePrice(TicketRecord ticketRecord) {
        Instant end = ticketRecord.getExitTime() != null ? ticketRecord.getExitTime() : Instant.now();
        Duration parked = Duration.between(ticketRecord.getEntryTime(), end);
        if (parked.isNegative()) {
            return 0;
        }
        if (parked.compareTo(FREE_PERIOD) < 0) {
            return 0;
        }
        Duration billable = parked.minus(FREE_PERIOD);
        long billableMillis = billable.toMillis();
        long blockMillis = BILLING_BLOCK.toMillis();
        int blocks = (int) Math.ceil(billableMillis / (double) blockMillis);
        return Math.min(MAX_PRICE, blocks * PRICE_PER_BLOCK);
    }
}
