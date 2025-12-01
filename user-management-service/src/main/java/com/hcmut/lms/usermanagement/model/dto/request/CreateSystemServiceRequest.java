package com.hcmut.lms.usermanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSystemServiceRequest {
    @NotBlank(message = "Service name is required")
    private String name;
    
    private String description;
}

