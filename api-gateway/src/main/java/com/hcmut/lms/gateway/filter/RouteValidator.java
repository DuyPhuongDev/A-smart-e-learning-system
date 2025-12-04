package com.hcmut.lms.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Route Validator to determine which endpoints are public (no auth required)
 */
@Component
public class RouteValidator {

    /**
     * List of public endpoints that don't require authentication
     * These patterns are matched against the request path
     */
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            // Authentication endpoints
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            
            // Health checks and actuator endpoints
            "/actuator",
            "/actuator/health",
            "/actuator/info",
            
            // Swagger/OpenAPI documentation (if you add it later)
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources"
    );

    /**
     * Patterns for more flexible matching
     * Example: /api/public/** would match any path starting with /api/public/
     */
    private static final List<Pattern> PUBLIC_PATTERNS = List.of(
            Pattern.compile("^/actuator.*"),
            Pattern.compile("^/swagger-ui.*"),
            Pattern.compile("^/v3/api-docs.*")
    );

    /**
     * Check if the request is to a public endpoint
     * @param request The incoming server request
     * @return true if the endpoint is public, false otherwise
     */
    public boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        
        // Check exact matches
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::equals)) {
            return true;
        }
        
        // Check pattern matches
        return PUBLIC_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(path).matches());
    }

    /**
     * Predicate for filtering in reactive chains
     */
    public Predicate<ServerHttpRequest> isSecured() {
        return request -> !isPublicEndpoint(request);
    }
}

