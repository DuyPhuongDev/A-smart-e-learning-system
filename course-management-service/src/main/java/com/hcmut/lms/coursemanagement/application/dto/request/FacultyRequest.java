package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacultyRequest {
    @NotBlank(message = "Faculty name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Faculty code is required")
    private String code;
}

