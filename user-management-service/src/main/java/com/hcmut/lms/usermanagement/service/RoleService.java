package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponse;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleResponse create(CreateRoleRequest request);
    RoleResponse getById(UUID id);
    List<RoleResponse> getAll();
    RoleResponse update(UUID id, UpdateRoleRequest request);
    void delete(UUID id);
}
