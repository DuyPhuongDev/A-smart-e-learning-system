package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CurriculumRequest {
    @NotBlank(message = "Curriculum code is required")
    private String code;
    
    @NotNull(message = "Specialization ID is required")
    private UUID specializationId;
    
    @NotNull(message = "Intake year ID is required")
    private UUID intakeYearId;
    
    private String name;
    private Integer totalCredits;
    private String description;
    private String document;
}

