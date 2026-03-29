package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UrlPermissionResponse {
    private UUID id;
    private String urlPattern;
    private String httpMethod;
    private UUID serviceFunctionId;
    private String serviceFunctionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

