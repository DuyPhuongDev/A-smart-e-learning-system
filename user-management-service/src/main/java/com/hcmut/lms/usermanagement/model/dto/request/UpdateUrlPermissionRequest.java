package com.hcmut.lms.usermanagement.model.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUrlPermissionRequest {
    private String urlPattern;
    private String httpMethod;
    private UUID serviceFunctionId;
}

