package com.hcmut.lms.gateway.filter;

import com.hcmut.lms.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Global JWT Authentication Filter for API Gateway
 * 
 * This filter:
 * - Validates JWT tokens for protected routes
 * - Extracts user information and adds to request headers for downstream services
 * - Blocks requests with invalid or missing tokens (except public endpoints)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    // Headers to pass user context to downstream services
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-Role";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        
        log.debug("Processing request: {} {}", method, path);

        // Skip authentication for public endpoints
        if (routeValidator.isPublicEndpoint(request)) {
            log.debug("Public endpoint, skipping authentication: {}", path);
            return chain.filter(exchange);
        }

        // Check for Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Missing Authorization header for: {} {}", method, path);
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Invalid Authorization header format for: {} {}", method, path);
            return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token for: {} {}", method, path);
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // Check if it's a refresh token (refresh tokens should only be used for /refresh-token endpoint)
        // Note: /api/auth/refresh-token is a public endpoint, so this check only applies to protected endpoints
        if (jwtUtil.isRefreshToken(token) && !path.equals("/api/auth/refresh-token")) {
            log.warn("Refresh token used for non-refresh endpoint: {} {}", method, path);
            return onError(exchange, "Refresh token cannot be used for this endpoint", HttpStatus.UNAUTHORIZED);
        }

        // Extract user information and add to headers for downstream services
        try {
            String userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // Create mutated request with user context headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(USER_ID_HEADER, userId)
                    .header(USER_EMAIL_HEADER, email)
                    .header(USER_ROLE_HEADER, role != null ? role : "UNKNOWN")
                    .build();

            log.debug("Authenticated user: {} ({}) [{}] for: {} {}", email, userId, role, method, path);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("Error extracting user info from token: {}", e.getMessage());
            return onError(exchange, "Error processing token", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public int getOrder() {
        // Run early in the filter chain, but after logging/tracing filters
        return -100;
    }

    /**
     * Handle authentication error
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String errorResponse = String.format(
                "{\"error\": \"%s\", \"message\": \"%s\", \"status\": %d}",
                status.getReasonPhrase(),
                message,
                status.value()
        );
        
        DataBuffer buffer = response.bufferFactory()
                .wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
}

