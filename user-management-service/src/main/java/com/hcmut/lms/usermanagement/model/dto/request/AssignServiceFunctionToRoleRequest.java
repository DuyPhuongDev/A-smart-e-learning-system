package com.hcmut.lms.usermanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignServiceFunctionToRoleRequest {
    @NotNull(message = "Role ID is required")
    private UUID roleId;
    
    @NotNull(message = "Service function IDs are required")
    private List<UUID> serviceFunctionIds;
}

