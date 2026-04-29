package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateGradingWeightRequest {

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", message = "Weight must be >= 0")
    @DecimalMax(value = "100.0", message = "Weight must be <= 100")
    private Float weight;
}
