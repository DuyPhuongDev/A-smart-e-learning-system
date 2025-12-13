package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.response.UserRoleResponse;
import com.hcmut.lms.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal controller for service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    /**
     * Get user's role by user ID
     * Used by authentication-service during login to include role in JWT
     */
    @GetMapping("/{userId}/role")
    public UserRoleResponse getUserRole(@PathVariable UUID userId) {
        return userService.getUserRole(userId);
    }
}

