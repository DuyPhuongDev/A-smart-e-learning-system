package com.hcmut.lms.coursemanagement.client.dto;

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
    private Integer specializationId;
    private UUID roleId;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User type specific fields
    private String studentCode;
    private String teacherCode;
    private String bio;
    private String adminCode;
    
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}

