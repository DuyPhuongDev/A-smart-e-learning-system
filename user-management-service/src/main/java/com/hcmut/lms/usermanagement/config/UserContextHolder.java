package com.hcmut.lms.usermanagement.config;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Thread-local holder for user context passed from API Gateway
 * 
 * The API Gateway validates JWT tokens and passes user information
 * via HTTP headers. This class provides a convenient way to access
 * that information throughout the request lifecycle.
 */
public class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    /**
     * Set the user context for the current thread
     */
    public static void setContext(UserContext context) {
        CONTEXT.set(context);
    }

    /**
     * Get the user context for the current thread
     */
    public static UserContext getContext() {
        return CONTEXT.get();
    }

    /**
     * Clear the user context (should be called after request completes)
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * Get the current user's ID
     * @return User UUID or null if not authenticated
     */
    public static UUID getCurrentUserId() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getUserId() : null;
    }

    /**
     * Get the current user's email
     * @return User email or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getEmail() : null;
    }

    /**
     * Get the current user's role
     * @return User role or null if not authenticated
     */
    public static String getCurrentUserRole() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getRole() : null;
    }

    /**
     * Check if there is an authenticated user in the current context
     */
    public static boolean isAuthenticated() {
        return CONTEXT.get() != null && CONTEXT.get().getUserId() != null;
    }

    /**
     * Check if current user has the specified role
     */
    public static boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equalsIgnoreCase(role);
    }

    /**
     * User context data class
     */
    @Data
    @Builder
    public static class UserContext {
        private UUID userId;
        private String email;
        private String role;
    }
}

