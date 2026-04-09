package parkinglot;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StaticRatePaymentCalculatorTest {

    private static final Instant ENTRY = Instant.parse("2024-06-01T12:00:00Z");

    private StaticRatePaymentCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StaticRatePaymentCalculator();
    }

    @Test
    void freeWhenParkedLessThanTenMinutes() {
        TicketRecord ticket = ticket(ENTRY, ENTRY.plus(Duration.ofMinutes(9).plusSeconds(59)));
        assertEquals(0.0, calculator.computePrice(ticket), 1e-9);
    }

    @Test
    void freeWhenEntryEqualsExit() {
        TicketRecord ticket = ticket(ENTRY, ENTRY);
        assertEquals(0.0, calculator.computePrice(ticket), 1e-9);
    }

    @Test
    void zeroWhenExitBeforeEntry() {
        TicketRecord ticket = ticket(ENTRY, ENTRY.minusSeconds(1));
        assertEquals(0.0, calculator.computePrice(ticket), 1e-9);
    }

    @Test
    void exactlyTenMinutesStillFreeUnderCurrentRules() {
        TicketRecord ticket = ticket(ENTRY, ENTRY.plus(Duration.ofMinutes(10)));
        assertEquals(0.0, calculator.computePrice(ticket), 1e-9);
    }

    @Test
    void oneDollarJustAfterFreePeriodEnds() {
        TicketRecord ticket = ticket(ENTRY, ENTRY.plus(Duration.ofMinutes(10)).plusMillis(1));
        assertEquals(1.0, calculator.computePrice(ticket), 1e-9);
    }

    @Test
    void twentyMinutesTotalOneBillableBlock() {
        TicketRecord ticket = ticket(ENTRY, ENTRY.plus(Duration.ofMinutes(20)));
        assertEquals(1.0, calculator.computePrice(ticket), 1e-9);
    }

    @Test
    void twentyOneMinutesTotalTwoBlocks() {
        TicketRecord ticket = ticket(ENTRY, ENTRY.plus(Duration.ofMinutes(21)));
        assertEquals(2.0, calculator.computePrice(ticket), 1e-9);
    }

    @Test
    void priceCappedAtThirtyDollars() {
        Duration parked = Duration.ofMinutes(10).plus(Duration.ofMinutes(300)).plusMinutes(1);
        TicketRecord ticket = ticket(ENTRY, ENTRY.plus(parked));
        assertEquals(30.0, calculator.computePrice(ticket), 1e-9);
    }

    private static TicketRecord ticket(Instant entry, Instant exit) {
        TicketRecord record =
                new TicketRecord(entry, "S1", ParkingSpotType.COMPACT, "T1");
        record.setExitTime(exit);
        return record;
    }
}
