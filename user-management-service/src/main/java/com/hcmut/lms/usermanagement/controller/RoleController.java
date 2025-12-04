package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponse;
import com.hcmut.lms.usermanagement.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@Valid @RequestBody CreateRoleRequest request) {
        return roleService.create(request);
    }
    
    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleService.getById(id);
    }
    
    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.getAll();
    }
    
    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        return roleService.update(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }
}
