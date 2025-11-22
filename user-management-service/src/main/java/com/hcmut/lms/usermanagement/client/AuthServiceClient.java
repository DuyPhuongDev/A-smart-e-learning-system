package com.hcmut.lms.usermanagement.client;

import com.hcmut.lms.common.dto.ApiResponse;
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
    fallback = AuthServiceClientFallback.class
)
public interface AuthServiceClient {
    
    /**
     * Create user credentials in authentication service
     */
    @PostMapping("/api/auth/internal/create-credentials")
    ApiResponse<Void> createUserCredentials(@RequestBody CreateCredentialsRequest request);
    
    /**
     * Reset user password
     */
    @PostMapping("/api/auth/internal/reset-password")
    ApiResponse<Void> resetUserPassword(@RequestBody ResetPasswordRequest request);
    
    /**
     * Lock user account
     */
    @PostMapping("/api/auth/internal/lock-account")
    ApiResponse<Void> lockUserAccount(@RequestBody UserIdRequest request);
    
    /**
     * Unlock user account
     */
    @PostMapping("/api/auth/internal/unlock-account")
    ApiResponse<Void> unlockUserAccount(@RequestBody UserIdRequest request);
    
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
}


