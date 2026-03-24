package ratelimiting;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SlidingWindowRateLimiter {

    private final int limit = 100;
    private final long windowSizeMillis = 60 * 1000;

    private final Map<String, Deque<Long>> requestMap = new HashMap<>();

    public synchronized boolean allowRequest(String userId, long timestamp) {
        Deque<Long> timestamps = requestMap.computeIfAbsent(userId, k -> new ArrayDeque<>());

        // Remove expired timestamps
        while (!timestamps.isEmpty() && timestamp - timestamps.peekFirst() >= windowSizeMillis) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < limit) {
            timestamps.addLast(timestamp);
            return true;
        }

        return false;
    }

    public static void main(String[] args) {

        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter();

        String user = "user1";
        long now = System.currentTimeMillis();

        long time = now;
        for (int i = 0; i < 110; i++) {
            boolean allowed = limiter.allowRequest(user, time);
            System.out.println("Request " + i + " allowed: " + allowed);
            if (i == 104) {
                // Just shift the time after letting few requests fail
                time = now + 60 * 1000;
            } else {
                time = now;
            }
        }
    }
}

