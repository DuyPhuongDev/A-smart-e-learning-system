package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubjectGradingRequest {

    @NotBlank(message = "Grading type is required")
    private String gradingType;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", message = "Weight must be >= 0")
    @DecimalMax(value = "100.0", message = "Weight must be <= 100")
    private Float weight;

    private String name;

    private String description;
}
