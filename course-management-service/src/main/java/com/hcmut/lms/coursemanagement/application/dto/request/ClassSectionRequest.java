package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ClassSectionRequest {
    
    @NotBlank(message = "Section name is required")
    private String sectionName;

    private String code;

    private UUID subjectId;
    
    private UUID semesterId;

    private String description;

    private String level;

    private Integer durationHours;

    private String language;

    private String topic;

    private String objective;

    private String thumbnail;

    private String introVideo;

    private Integer maxStudents;
}

