package ratelimiting;

public class ThrottleException extends RuntimeException {
    public ThrottleException(String msg) {
        super(msg);
    }
}


