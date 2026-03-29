package com.hcmut.lms.usermanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateServiceFunctionRequest {
    @NotBlank(message = "Function name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "System service ID is required")
    private UUID systemServiceId;
}

