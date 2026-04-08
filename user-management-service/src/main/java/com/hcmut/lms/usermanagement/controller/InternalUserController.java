package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.InternalResolveUsersRequest;
import com.hcmut.lms.usermanagement.model.dto.response.InternalUserSummaryResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserRoleResponse;
import com.hcmut.lms.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
     * Get all teachers - for service-to-service communication
     */
    @GetMapping("/teachers")
    public List<UserResponse> getAllTeachers() {
        return userService.getAllTeachers();
    }

    /**
     * Get user's role by user ID
     * Used by authentication-service during login to include role in JWT
     */
    @GetMapping("/{userId}/role")
    public UserRoleResponse getUserRole(@PathVariable UUID userId) {
        return userService.getUserRole(userId);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @PostMapping("/resolve")
    public List<InternalUserSummaryResponse> resolveUsers(@RequestBody(required = false) InternalResolveUsersRequest request) {
        InternalResolveUsersRequest safeRequest = request == null ? new InternalResolveUsersRequest() : request;
        return userService.resolveUsers(safeRequest);
    }
}
