package com.hcmut.lms.usermanagement.client;

import com.hcmut.lms.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for AuthServiceClient
 * Provides circuit breaker pattern when authentication-service is unavailable
 */
@Slf4j
@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    
    @Override
    public ApiResponse<Void> createUserCredentials(CreateCredentialsRequest request) {
        log.error("Authentication service is unavailable. Failed to create credentials for user: {}", 
                request.getEmail());
        return ApiResponse.<Void>builder()
                .status(503)
                .message("Authentication service is temporarily unavailable")
                .build();
    }
    
    @Override
    public ApiResponse<Void> resetUserPassword(ResetPasswordRequest request) {
        log.error("Authentication service is unavailable. Failed to reset password for user: {}", 
                request.getEmail());
        return ApiResponse.<Void>builder()
                .status(503)
                .message("Authentication service is temporarily unavailable")
                .build();
    }
    
    @Override
    public ApiResponse<Void> lockUserAccount(UserIdRequest request) {
        log.error("Authentication service is unavailable. Failed to lock account for user: {}", 
                request.getUserId());
        return ApiResponse.<Void>builder()
                .status(503)
                .message("Authentication service is temporarily unavailable")
                .build();
    }
    
    @Override
    public ApiResponse<Void> unlockUserAccount(UserIdRequest request) {
        log.error("Authentication service is unavailable. Failed to unlock account for user: {}", 
                request.getUserId());
        return ApiResponse.<Void>builder()
                .status(503)
                .message("Authentication service is temporarily unavailable")
                .build();
    }
}


