package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponseDto;
import com.hcmut.lms.usermanagement.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    public List<RoleResponseDto> getAllRoles(
            @RequestParam(defaultValue = "true") boolean includeCustom) {
        return roleService.getAllRoles(includeCustom);
    }
    
    @GetMapping("/{id}")
    public RoleDetailResponseDto getRoleById(@PathVariable UUID id) {
        return roleService.getRoleById(id);
    }
    
    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody CreateRoleRequestDto dto) {
        RoleResponseDto role = roleService.createRole(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }
    
    @PutMapping("/{id}")
    public RoleResponseDto updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequestDto dto) {
        return roleService.updateRole(id, dto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
    }
}

