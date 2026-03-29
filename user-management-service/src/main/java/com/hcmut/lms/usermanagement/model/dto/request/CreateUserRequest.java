package com.hcmut.lms.usermanagement.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String phone;
    
    private String avatarUrl;
    
    private UUID specializationId;
    
    @NotNull(message = "Role ID is required")
    private UUID roleId;
    
    // For student
    private String studentCode;
    
    // For teacher
    private String teacherCode;
    private String bio;
    
    // For admin
    private String adminCode;
}

