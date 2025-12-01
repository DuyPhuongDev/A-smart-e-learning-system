package com.hcmut.lms.usermanagement.model.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatarUrl;
    private Integer specializationId;
    private UUID roleId;
    
    // For student
    private String studentCode;
    
    // For teacher
    private String teacherCode;
    private String bio;
    
    // For admin
    private String adminCode;
}

