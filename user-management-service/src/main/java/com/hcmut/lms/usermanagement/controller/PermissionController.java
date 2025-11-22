package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.UpdatePermissionsRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.EndpointDto;
import com.hcmut.lms.usermanagement.model.dto.response.PermissionDto;
import com.hcmut.lms.usermanagement.model.dto.response.ScanResultDto;
import com.hcmut.lms.usermanagement.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    
    @GetMapping("/endpoints")
    public List<EndpointDto> getAllEndpoints() {
        return permissionService.getAllEndpoints();
    }
    
    @PostMapping("/endpoints/scan")
    public ScanResultDto scanEndpoints() {
        return permissionService.scanEndpointsFromServices();
    }
    
    @PutMapping("/roles/{roleId}")
    public void updateRolePermissions(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdatePermissionsRequestDto dto) {
        permissionService.updateRolePermissions(roleId, dto);
    }
    
    @GetMapping("/roles/{roleId}")
    public List<PermissionDto> getRolePermissions(@PathVariable UUID roleId) {
        return permissionService.getRolePermissions(roleId);
    }
}

