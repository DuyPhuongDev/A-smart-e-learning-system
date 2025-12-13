package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SpecializationRequest {
    @NotBlank(message = "Specialization code is required")
    private String code;
    
    @NotBlank(message = "Specialization name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Department ID is required")
    private UUID departmentId;
}

