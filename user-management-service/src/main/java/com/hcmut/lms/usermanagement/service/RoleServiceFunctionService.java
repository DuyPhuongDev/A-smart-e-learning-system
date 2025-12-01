package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.AssignServiceFunctionToRoleRequest;

import java.util.List;
import java.util.UUID;

public interface RoleServiceFunctionService {
    void assignServiceFunctionsToRole(AssignServiceFunctionToRoleRequest request);
    void removeServiceFunctionFromRole(UUID roleId, UUID serviceFunctionId);
    List<UUID> getServiceFunctionIdsByRoleId(UUID roleId);
}

