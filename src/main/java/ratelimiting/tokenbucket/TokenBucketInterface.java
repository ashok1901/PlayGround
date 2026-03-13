package ratelimiting.tokenbucket;

public interface TokenBucketInterface {
    public boolean isAllowed(String id);
}


