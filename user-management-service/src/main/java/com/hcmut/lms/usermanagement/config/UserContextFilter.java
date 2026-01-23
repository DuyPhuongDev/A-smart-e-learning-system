package com.hcmut.lms.usermanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to extract user context from headers set by API Gateway
 * 
 * The API Gateway validates JWT tokens and adds user information to request headers:
 * - X-User-Id: The authenticated user's UUID
 * - X-User-Email: The authenticated user's email
 * - X-Role: The authenticated user's role
 * 
 * This filter extracts that information and makes it available via UserContextHolder
 */
@Component
@Order(1)
@Slf4j
public class UserContextFilter extends OncePerRequestFilter {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-Role";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String userIdStr = request.getHeader(USER_ID_HEADER);
            String email = request.getHeader(USER_EMAIL_HEADER);
            String role = request.getHeader(USER_ROLE_HEADER);

            if (userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    UUID userId = UUID.fromString(userIdStr);
                    
                    UserContextHolder.UserContext context = UserContextHolder.UserContext.builder()
                            .userId(userId)
                            .email(email)
                            .role(role)
                            .build();
                    
                    UserContextHolder.setContext(context);
                    
                    log.debug("User context set: userId={}, email={}, role={}", userId, email, role);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid user ID format in header: {}", userIdStr);
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            // Always clear the context after the request completes
            UserContextHolder.clear();
        }
    }
}

