package com.hcmut.lms.coursemanagement.application.dto.request;

import com.hcmut.lms.coursemanagement.domain.entity.subject.GradingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GradingRequest {
    @NotBlank(message = "Grading name is mandatory")
    private String name;
    private String description;

    private GradingType gradingType;
}
