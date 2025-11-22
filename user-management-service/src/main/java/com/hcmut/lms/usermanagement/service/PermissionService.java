package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.UpdatePermissionsRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.EndpointDto;
import com.hcmut.lms.usermanagement.model.dto.response.PermissionDto;
import com.hcmut.lms.usermanagement.model.dto.response.ScanResultDto;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    
    List<EndpointDto> getAllEndpoints();
    
    ScanResultDto scanEndpointsFromServices();
    
    void updateRolePermissions(UUID roleId, UpdatePermissionsRequestDto dto);
    
    List<PermissionDto> getRolePermissions(UUID roleId);
    
    boolean checkUserPermission(UUID userId, String endpoint, String method);
}

