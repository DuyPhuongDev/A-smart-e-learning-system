package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ServiceFunctionResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID systemServiceId;
    private String systemServiceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

