package com.hcmut.lms.usermanagement.model.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateServiceFunctionRequest {
    private String name;
    private String description;
    private UUID systemServiceId;
}

