package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CurriculumSectionRequest {
    @NotBlank(message = "Section name is required")
    private String name;
    
    private String notes;
    private Integer requiredCredits;
    private Integer displayOrder;
    private String description;
    
    @NotBlank(message = "Curriculum code is required")
    private String curriculumCode;
    
    @NotNull(message = "Curriculum specialization ID is required")
    private UUID curriculumSpecializationId;
    
    @NotNull(message = "Curriculum intake year ID is required")
    private UUID curriculumIntakeYearId;
}

