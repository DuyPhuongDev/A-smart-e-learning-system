package com.hcmut.lms.gateway.filter;

import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.regex.Pattern;

/**
 * Filter to block internal service-to-service endpoints from external access
 * 
 * Internal endpoints should only be accessed by other services within the cluster,
 * not through the API Gateway by external clients
 */
@Component
@Slf4j
public class InternalEndpointBlockFilter implements GlobalFilter, Ordered {

    /**
     * Patterns for internal endpoints that should be blocked
     */
    private static final List<Pattern> INTERNAL_PATTERNS = List.of(
            Pattern.compile("^/api/.*/internal/.*"),
            Pattern.compile("^/internal/.*")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Check if path matches any internal pattern
        boolean isInternal = INTERNAL_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(path).matches());

        if (isInternal) {
            log.warn("Blocked access to internal endpoint: {} {}", 
                    request.getMethod(), path);
            return onForbidden(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Run first, before any other processing
        return -300;
    }

    /**
     * Return 403 Forbidden response
     */
    private Mono<Void> onForbidden(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = String.format(
                "{\"error\": \"Forbidden\", \"message\": \"This endpoint is not accessible\", \"status\": %d}",
                HttpStatus.FORBIDDEN.value()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(errorResponse.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}

