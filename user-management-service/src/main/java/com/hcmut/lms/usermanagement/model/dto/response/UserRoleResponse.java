package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for user role information
 * Used by authentication-service to get role during login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse {
    private UUID userId;
    private String roleName;
    private UUID roleId;
}

