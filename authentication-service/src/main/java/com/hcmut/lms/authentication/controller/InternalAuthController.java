package com.hcmut.lms.authentication.controller;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Internal Authentication Controller
 * Handles internal service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("/api/auth/internal")
public class InternalAuthController {

    @PostMapping("/create-credentials")
    public String createCredentials(@RequestBody CreateCredentialsRequest request) {
        // TODO: Implement credential creation
        // 1. Hash password with BCrypt
        // 2. Store in user_credentials table
        // 3. Optionally send welcome email with temporary password
        return "Credentials created for user: " + request.getEmail();
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        // TODO: Implement password reset
        // 1. Generate secure reset token
        // 2. Store token with expiration
        // 3. Send reset email to user
        return "Password reset initiated for user: " + request.getEmail();
    }

    @PostMapping("/lock-account")
    public String lockAccount(@RequestBody UserIdRequest request) {
        // TODO: Implement account lock
        // 1. Revoke all active refresh tokens
        // 2. Add all active access tokens to blacklist
        return "Account locked for user: " + request.getUserId();
    }

    @PostMapping("/unlock-account")
    public String unlockAccount(@RequestBody UserIdRequest request) {
        // TODO: Implement account unlock
        // 1. Remove user from any lockout tracking
        // 2. Clear failed login attempts
        return "Account unlocked for user: " + request.getUserId();
    }

    // DTO Classes
    public static class CreateCredentialsRequest {
        private UUID userId;
        private String email;
        private String temporaryPassword;

        public CreateCredentialsRequest() {}

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getTemporaryPassword() { return temporaryPassword; }
        public void setTemporaryPassword(String temporaryPassword) { 
            this.temporaryPassword = temporaryPassword; 
        }
    }

    public static class ResetPasswordRequest {
        private UUID userId;
        private String email;

        public ResetPasswordRequest() {}

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class UserIdRequest {
        private UUID userId;

        public UserIdRequest() {}

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }
}


