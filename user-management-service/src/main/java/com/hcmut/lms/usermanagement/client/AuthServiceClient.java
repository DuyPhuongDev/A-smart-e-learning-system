package com.hcmut.lms.usermanagement.client;

import com.hcmut.lms.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "authentication-service", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {
    
    @PostMapping("/api/auth/internal/create-credentials")
    ApiResponse<Void> createUserCredentials(
            @RequestParam UUID userId, 
            @RequestParam String email, 
            @RequestParam String temporaryPassword);
    
    @PostMapping("/api/auth/internal/reset-password")
    ApiResponse<Void> resetUserPassword(
            @RequestParam UUID userId, 
            @RequestParam String email);
    
    @PostMapping("/api/auth/internal/lock-account")
    ApiResponse<Void> lockUserAccount(@RequestParam UUID userId);
    
    @PostMapping("/api/auth/internal/unlock-account")
    ApiResponse<Void> unlockUserAccount(@RequestParam UUID userId);
}

