package ratelimiting.tokenbucket;

import ratelimiting.ThrottleException;

public class TokenBucketImpl implements TokenBucketInterface {
    private TokenBucket tokenBucket;

    public TokenBucketImpl() {
        this.tokenBucket = new TokenBucket();
    }

    @Override
    public boolean isAllowed(String id) {
        fillIfNeeded(id);
        try {
            Token token = tokenBucket.getToken(id);
            token.consumeToken();
            return true;
        } catch (ThrottleException e) {
            // Log it here

        }
        return false;
    }

    private void fillIfNeeded(String id) {
        Token token = tokenBucket.getToken(id);
        if (token.isRefillDue()) {
            token.refillTokens();
        }
    }

    public static void main(String[] args) {
        TokenBucketInterface tokenBucket = new TokenBucketImpl();
        int count = 0;
        String userId = "1";
        while (count <= 100) {
            boolean isAllowed = tokenBucket.isAllowed(userId);
            System.out.println(String.format("Is user %s allowed %s", userId, isAllowed));
            count++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted......");
            }
        }
    }
}


