package com.hcmut.lms.usermanagement.client;

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
    public void createUserCredentials(CreateCredentialsRequest request) {
        log.error("Authentication service is unavailable. Failed to create credentials for user: {}", 
                request.getEmail());
        throw new RuntimeException("Authentication service is temporarily unavailable");
    }
    
    @Override
    public void resetUserPassword(ResetPasswordRequest request) {
        log.error("Authentication service is unavailable. Failed to reset password for user: {}", 
                request.getEmail());
        throw new RuntimeException("Authentication service is temporarily unavailable");
    }
    
    @Override
    public void lockUserAccount(UserIdRequest request) {
        log.error("Authentication service is unavailable. Failed to lock account for user: {}", 
                request.getUserId());
        throw new RuntimeException("Authentication service is temporarily unavailable");
    }
    
    @Override
    public void unlockUserAccount(UserIdRequest request) {
        log.error("Authentication service is unavailable. Failed to unlock account for user: {}", 
                request.getUserId());
        throw new RuntimeException("Authentication service is temporarily unavailable");
    }
    
    @Override
    public void updateUserEmail(UpdateEmailRequest request) {
        log.error("Authentication service is unavailable. Failed to update email for user: {}", 
                request.getUserId());
        throw new RuntimeException("Authentication service is temporarily unavailable");
    }
}


