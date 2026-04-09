package doordash;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DasherPayoutServiceTest {

    @Test
    void getPayoutReturnsZeroForBlankDasherData() {
        DasherPayoutService service = new DasherPayoutService(List.of());

        assertEquals(0.0, service.getPayout(1), 1e-9);
    }

    @Test
    void getPayoutReturnsZeroWhenNoDataForDasherId() {
        DasherPayoutService service = new DasherPayoutService(List.of(
                "1,1,2023-03-31 18:15:00,ACCEPTED",
                "1,1,2023-03-31 18:36:00,FULFILLED"));

        assertEquals(0.0, service.getPayout(99), 1e-9);
    }

    @Test
    void getPayoutIgnoresNotFulfilledDeliveriesAndComputesExpectedAmount() {
        DasherPayoutService service = new DasherPayoutService(List.of(
                "1,1,2023-03-31 18:15:00,ACCEPTED",
                "1,2,2023-03-31 18:18:00,ACCEPTED",
                "1,1,2023-03-31 18:36:00,FULFILLED",
                "1,2,2023-03-31 18:45:00,CANCELLED"));

        assertEquals(6.3, service.getPayout(1), 1e-9);
    }

    @Test
    void getPayoutWithNoFulfilledDeliveriesAndComputesExpectedAmount() {
        DasherPayoutService service = new DasherPayoutService(List.of(
                "5,10,2023-03-31 09:00:00,ACCEPTED", "5,10,2023-03-31 09:12:00,CANCELLED"));

        assertEquals(0.0, service.getPayout(1), 1e-9);
    }
}

