package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StudentResponse {
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

    private String studentCode;
    private UUID intakeYear;
}

