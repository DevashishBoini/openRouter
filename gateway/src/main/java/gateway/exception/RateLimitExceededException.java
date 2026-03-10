package gateway.exception;

public class RateLimitExceededException extends RuntimeException {
    private final int limit;
    private final int retryAfterSeconds;

    public RateLimitExceededException(int limit, int retryAfterSeconds) {
        super(String.format("Rate limit exceeded. Limit: %d requests/hour. Try again in %d seconds.",
                limit, retryAfterSeconds));
        this.limit = limit;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getLimit() {
        return limit;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
