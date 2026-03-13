package ratelimiting.tokenbucket;

public class TokenRefillConfig {
    public static int INITIAL_TOKENS_SIZE = 10;
    // 1 Min refill time, time in millils wrt to epoch
    public static long REFILL_INTERVAL = 1 * 60 * 1000;
}


