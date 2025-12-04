package com.hcmut.lms.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Rate Limiting Filter using in-memory storage
 * For production, consider using Redis-based rate limiting
 * 
 * This filter limits requests per IP address within a time window
 */
@Component
@Slf4j
public class RateLimitingFilter implements GlobalFilter, Ordered {

    @Value("${rate-limit.requests-per-second:100}")
    private int requestsPerSecond;

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    // In-memory storage for rate limiting (use Redis in production)
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!rateLimitEnabled) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String clientId = getClientId(request);
        
        if (!isAllowed(clientId)) {
            log.warn("Rate limit exceeded for client: {}", clientId);
            return onRateLimitExceeded(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Run before authentication filter
        return -200;
    }

    /**
     * Get client identifier (IP address or user ID if authenticated)
     */
    private String getClientId(ServerHttpRequest request) {
        // Try to get user ID from header (set by JWT filter for authenticated requests)
        String userId = request.getHeaders().getFirst("X-User-Id");
        if (userId != null) {
            return "user:" + userId;
        }

        // Fall back to IP address
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return "ip:" + xForwardedFor.split(",")[0].trim();
        }

        if (request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null) {
            return "ip:" + request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "ip:unknown";
    }

    /**
     * Check if the request is allowed based on rate limit
     */
    private boolean isAllowed(String clientId) {
        long currentSecond = Instant.now().getEpochSecond();
        
        RateLimitBucket bucket = buckets.compute(clientId, (key, existingBucket) -> {
            if (existingBucket == null || existingBucket.timestamp != currentSecond) {
                return new RateLimitBucket(currentSecond, new AtomicInteger(0));
            }
            return existingBucket;
        });

        int currentCount = bucket.counter.incrementAndGet();
        return currentCount <= requestsPerSecond;
    }

    /**
     * Clean up old buckets periodically (to prevent memory leaks)
     * In production, this would be handled by Redis TTL
     */
    public void cleanupOldBuckets() {
        long currentSecond = Instant.now().getEpochSecond();
        buckets.entrySet().removeIf(entry -> 
            currentSecond - entry.getValue().timestamp > 60
        );
    }

    /**
     * Handle rate limit exceeded
     */
    private Mono<Void> onRateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().add("Retry-After", "1");

        String errorResponse = String.format(
                "{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Please try again later.\", \"status\": %d}",
                HttpStatus.TOO_MANY_REQUESTS.value()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(errorResponse.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Simple bucket for rate limiting
     */
    private static class RateLimitBucket {
        final long timestamp;
        final AtomicInteger counter;

        RateLimitBucket(long timestamp, AtomicInteger counter) {
            this.timestamp = timestamp;
            this.counter = counter;
        }
    }
}

