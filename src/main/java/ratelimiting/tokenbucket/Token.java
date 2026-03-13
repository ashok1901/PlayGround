package ratelimiting.tokenbucket;

import ratelimiting.ThrottleException;

public class Token {
    private int tokensCount;
    private long lastFillTime;
    private long nextFillTime;

    public Token() {
        this.tokensCount = TokenRefillConfig.INITIAL_TOKENS_SIZE;
        this.lastFillTime = System.currentTimeMillis();
        this.nextFillTime = System.currentTimeMillis() + TokenRefillConfig.REFILL_INTERVAL;
    }

    public void refillTokens() {
        this.tokensCount = TokenRefillConfig.INITIAL_TOKENS_SIZE;
        this.lastFillTime = System.currentTimeMillis();
        this.nextFillTime = System.currentTimeMillis() + TokenRefillConfig.REFILL_INTERVAL;
    }

    public boolean isRefillDue() {
        long currentTime = System.currentTimeMillis();
        return currentTime >= nextFillTime;
    }

    public void consumeToken() {
        if (this.tokensCount == 0) {
            throw new ThrottleException("Rate limit exceeded.");
        }
        this.tokensCount--;
    }
}


