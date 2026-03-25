package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
    private String avatarUrl;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDateTime lastLogin;
    private UUID specializationId;
    private UUID roleId;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User type specific fields
    private String studentCode;
    private String teacherCode;
    private String bio;
    private String adminCode;
}

