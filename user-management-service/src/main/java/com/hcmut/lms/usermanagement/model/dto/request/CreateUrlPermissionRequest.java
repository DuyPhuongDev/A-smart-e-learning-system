package com.hcmut.lms.usermanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUrlPermissionRequest {
    @NotBlank(message = "URL pattern is required")
    private String urlPattern;
    
    @NotBlank(message = "HTTP method is required")
    private String httpMethod;
    
    @NotNull(message = "Service function ID is required")
    private UUID serviceFunctionId;
}

