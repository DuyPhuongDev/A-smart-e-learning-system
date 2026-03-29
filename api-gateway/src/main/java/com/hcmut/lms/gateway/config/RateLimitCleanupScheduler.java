package com.hcmut.lms.gateway.config;

import com.hcmut.lms.gateway.filter.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler to clean up old rate limit buckets
 * Prevents memory leaks in in-memory rate limiting
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
public class RateLimitCleanupScheduler {

    private final RateLimitingFilter rateLimitingFilter;

    /**
     * Clean up old rate limit buckets every minute
     */
    @Scheduled(fixedRate = 60000)
    public void cleanupRateLimitBuckets() {
        rateLimitingFilter.cleanupOldBuckets();
    }
}

