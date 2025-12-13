package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DepartmentRequest {
    @NotBlank(message = "Department name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Faculty ID is required")
    private UUID facultyId;
}

