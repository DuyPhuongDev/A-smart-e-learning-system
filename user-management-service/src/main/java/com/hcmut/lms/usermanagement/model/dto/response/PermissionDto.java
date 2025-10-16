package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    private UUID id;
    private String endpoint;
    private String httpMethod;
    private String serviceName;
    private String description;
    private Boolean isAllowed;
}

