package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.AssignServiceFunctionToRoleRequest;
import com.hcmut.lms.usermanagement.service.RoleServiceFunctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/role-service-functions")
@RequiredArgsConstructor
public class RoleServiceFunctionController {
    
    private final RoleServiceFunctionService roleServiceFunctionService;
    
    @PostMapping("/assign")
    public ResponseEntity<Void> assignServiceFunctionsToRole(@Valid @RequestBody AssignServiceFunctionToRoleRequest request) {
        roleServiceFunctionService.assignServiceFunctionsToRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @DeleteMapping("/role/{roleId}/service-function/{serviceFunctionId}")
    public ResponseEntity<Void> removeServiceFunctionFromRole(
            @PathVariable UUID roleId,
            @PathVariable UUID serviceFunctionId) {
        roleServiceFunctionService.removeServiceFunctionFromRole(roleId, serviceFunctionId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UUID>> getServiceFunctionIdsByRoleId(@PathVariable UUID roleId) {
        List<UUID> serviceFunctionIds = roleServiceFunctionService.getServiceFunctionIdsByRoleId(roleId);
        return ResponseEntity.ok(serviceFunctionIds);
    }
}

