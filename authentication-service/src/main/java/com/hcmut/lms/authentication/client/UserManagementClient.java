package com.hcmut.lms.authentication.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-management-service", fallback = UserManagementClientFallback.class)
public interface UserManagementClient {
    
    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable UUID id);
    
    @GetMapping("/api/users/email/{email}")
    Object getUserByEmail(@PathVariable String email);
}

