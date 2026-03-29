package com.hcmut.lms.usermanagement.model.dto.request;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private String name;
    private String description;
    private Boolean isActive;
}

