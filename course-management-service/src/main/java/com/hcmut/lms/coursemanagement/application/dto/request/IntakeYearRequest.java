package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IntakeYearRequest {
    @NotNull(message = "Start year is required")
    private Integer startYear;
    
    private String name;
}

