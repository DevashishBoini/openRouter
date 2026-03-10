package gateway.service;

import gateway.exception.RateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiting service.
 * Tracks request counts per API key within a sliding time window.
 */
@Service
@Slf4j
public class RateLimitService {

    @Value("${gateway.ratelimit.requests-per-hour:1000}")
    private int requestsPerHour;

    @Value("${gateway.ratelimit.enabled:true}")
    private boolean enabled;

    // Stores: apiKeyId -> RateLimitBucket
    private final Map<UUID, RateLimitBucket> rateLimitMap = new ConcurrentHashMap<>();

    /**
     * Check if the API key has exceeded rate limit.
     * If not, increments the request count.
     *
     * @param apiKeyId the API key ID
     * @throws RateLimitExceededException if rate limit exceeded
     */
    public void checkRateLimit(UUID apiKeyId) {
        if (!enabled) {
            return; // Rate limiting disabled
        }

        long now = Instant.now().getEpochSecond();
        long windowStart = now - 3600; // 1 hour window

        RateLimitBucket bucket = rateLimitMap.compute(apiKeyId, (key, existing) -> {
            if (existing == null || existing.windowStart < windowStart) {
                // Create new bucket or reset expired bucket
                return new RateLimitBucket(now, 1);
            } else {
                // Increment existing bucket
                existing.requestCount++;
                return existing;
            }
        });

        if (bucket.requestCount > requestsPerHour) {
            int retryAfterSeconds = (int) (3600 - (now - bucket.windowStart));
            log.warn("Rate limit exceeded for API key: {}. Count: {}, Limit: {}, Retry after: {}s",
                    apiKeyId, bucket.requestCount, requestsPerHour, retryAfterSeconds);
            throw new RateLimitExceededException(requestsPerHour, retryAfterSeconds);
        }

        log.debug("Rate limit check passed for API key: {}. Count: {}/{}",
                apiKeyId, bucket.requestCount, requestsPerHour);
    }

    /**
     * Clean up expired buckets every 10 minutes.
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void cleanupExpiredBuckets() {
        long now = Instant.now().getEpochSecond();
        long windowStart = now - 3600;

        int initialSize = rateLimitMap.size();
        rateLimitMap.entrySet().removeIf(entry -> entry.getValue().windowStart < windowStart);
        int removed = initialSize - rateLimitMap.size();

        if (removed > 0) {
            log.info("Cleaned up {} expired rate limit buckets", removed);
        }
    }

    /**
     * Get current request count for an API key (for monitoring/debugging).
     */
    public int getCurrentCount(UUID apiKeyId) {
        RateLimitBucket bucket = rateLimitMap.get(apiKeyId);
        if (bucket == null) {
            return 0;
        }

        long now = Instant.now().getEpochSecond();
        long windowStart = now - 3600;

        if (bucket.windowStart < windowStart) {
            return 0; // Bucket expired
        }

        return bucket.requestCount;
    }

    /**
     * Inner class to hold rate limit data for each API key.
     */
    private static class RateLimitBucket {
        long windowStart; // Timestamp when the window started
        int requestCount;

        RateLimitBucket(long windowStart, int requestCount) {
            this.windowStart = windowStart;
            this.requestCount = requestCount;
        }
    }
}
