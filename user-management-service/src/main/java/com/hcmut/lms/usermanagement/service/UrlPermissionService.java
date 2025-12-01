package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UrlPermissionResponse;

import java.util.List;
import java.util.UUID;

public interface UrlPermissionService {
    UrlPermissionResponse create(CreateUrlPermissionRequest request);
    UrlPermissionResponse getById(UUID id);
    List<UrlPermissionResponse> getAll();
    List<UrlPermissionResponse> getByServiceFunctionId(UUID serviceFunctionId);
    UrlPermissionResponse update(UUID id, UpdateUrlPermissionRequest request);
    void delete(UUID id);
}

