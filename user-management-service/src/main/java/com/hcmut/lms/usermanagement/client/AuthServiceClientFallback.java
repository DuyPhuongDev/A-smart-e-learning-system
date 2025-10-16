package com.hcmut.lms.usermanagement.client;

import com.hcmut.lms.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    
    @Override
    public ApiResponse<Void> createUserCredentials(UUID userId, String email, String temporaryPassword) {
        log.error("Authentication Service is unavailable. Failed to create credentials for user: {}", email);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Authentication service is temporarily unavailable")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    @Override
    public ApiResponse<Void> resetUserPassword(UUID userId, String email) {
        log.error("Authentication Service is unavailable. Failed to reset password for user: {}", email);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Authentication service is temporarily unavailable")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    @Override
    public ApiResponse<Void> lockUserAccount(UUID userId) {
        log.error("Authentication Service is unavailable. Failed to lock account for user: {}", userId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Authentication service is temporarily unavailable")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    @Override
    public ApiResponse<Void> unlockUserAccount(UUID userId) {
        log.error("Authentication Service is unavailable. Failed to unlock account for user: {}", userId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Authentication service is temporarily unavailable")
                .timestamp(LocalDateTime.now())
                .build();
    }
}

