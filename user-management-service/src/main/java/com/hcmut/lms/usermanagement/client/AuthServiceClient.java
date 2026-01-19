package com.hcmut.lms.usermanagement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Feign Client for Authentication Service
 * Handles communication with authentication-service for credential management
 */
@FeignClient(
    name = "authentication-service",
    fallback = AuthServiceClientFallback.class,
        url = "http://localhost:8081"
)
public interface AuthServiceClient {
    
    /**
     * Create user credentials in authentication service
     */
    @PostMapping("/api/auth/internal/create-credentials")
    void createUserCredentials(@RequestBody CreateCredentialsRequest request);
    
    /**
     * Reset user password
     */
    @PostMapping("/api/auth/internal/reset-password")
    void resetUserPassword(@RequestBody ResetPasswordRequest request);
    
    /**
     * Lock user account
     */
    @PostMapping("/api/auth/internal/lock-account")
    void lockUserAccount(@RequestBody UserIdRequest request);
    
    /**
     * Unlock user account
     */
    @PostMapping("/api/auth/internal/unlock-account")
    void unlockUserAccount(@RequestBody UserIdRequest request);
    
    /**
     * Update user email (sync from user-management to authentication)
     */
    @PostMapping("/api/auth/internal/update-email")
    void updateUserEmail(@RequestBody UpdateEmailRequest request);
    
    // DTO Classes
    class CreateCredentialsRequest {
        private UUID userId;
        private String email;
        private String temporaryPassword;
        
        public CreateCredentialsRequest() {}
        
        public CreateCredentialsRequest(UUID userId, String email, String temporaryPassword) {
            this.userId = userId;
            this.email = email;
            this.temporaryPassword = temporaryPassword;
        }
        
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getTemporaryPassword() { return temporaryPassword; }
        public void setTemporaryPassword(String temporaryPassword) { this.temporaryPassword = temporaryPassword; }
    }
    
    class ResetPasswordRequest {
        private UUID userId;
        private String email;
        
        public ResetPasswordRequest() {}
        
        public ResetPasswordRequest(UUID userId, String email) {
            this.userId = userId;
            this.email = email;
        }
        
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    class UserIdRequest {
        private UUID userId;
        
        public UserIdRequest() {}
        
        public UserIdRequest(UUID userId) {
            this.userId = userId;
        }
        
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }
    
    class UpdateEmailRequest {
        private UUID userId;
        private String oldEmail;
        private String newEmail;
        
        public UpdateEmailRequest() {}
        
        public UpdateEmailRequest(UUID userId, String oldEmail, String newEmail) {
            this.userId = userId;
            this.oldEmail = oldEmail;
            this.newEmail = newEmail;
        }
        
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        
        public String getOldEmail() { return oldEmail; }
        public void setOldEmail(String oldEmail) { this.oldEmail = oldEmail; }
        
        public String getNewEmail() { return newEmail; }
        public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
    }
}


