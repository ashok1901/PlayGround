package cache;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SlidingWindowRateLimiterTest {

    @Test
    void testEvictionOfExpiredRequests() {

        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter();
        String user = "user1";

        long start = 1000;

        // Fill limit
        for (int i = 0; i < 100; i++) {
            assertTrue(limiter.allowRequest(user, start));
        }

        // Next request should fail
        assertFalse(limiter.allowRequest(user, start));

        // Move time forward beyond window
        long newTime = start + 60001;

        // Old requests should be evicted
        assertTrue(limiter.allowRequest(user, newTime));
    }

    @Test
    void testRequestsAllowedInNewWindow() {

        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter();
        String user = "user2";

        long start = 2000;

        for (int i = 0; i < 100; i++) {
            assertTrue(limiter.allowRequest(user, start));
        }

        assertFalse(limiter.allowRequest(user, start));

        // After window expires
        long nextWindow = start + 60000 + 1;

        for (int i = 0; i < 100; i++) {
            assertTrue(limiter.allowRequest(user, nextWindow));
        }
    }

    @Test
    void testWindowBoundaryCondition() {

        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter();
        String user = "user3";

        long start = 5000;

        // Fill limit
        for (int i = 0; i < 100; i++) {
            assertTrue(limiter.allowRequest(user, start));
        }

        // Exactly at boundary
        long boundary = start + 60000;

        // Old entries should now be expired
        assertTrue(limiter.allowRequest(user, boundary));
    }
}