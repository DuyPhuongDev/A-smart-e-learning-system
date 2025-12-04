package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ClassSectionRequest {
    
    @NotBlank(message = "Section name is required")
    private String sectionName;
    
    private UUID subjectId;
    
    private UUID semesterId;

    private UUID teacherId;
}

