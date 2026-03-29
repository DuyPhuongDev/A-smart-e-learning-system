package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.response.SystemServiceResponse;

import java.util.List;
import java.util.UUID;

public interface SystemServiceService {
    SystemServiceResponse create(CreateSystemServiceRequest request);
    SystemServiceResponse getById(UUID id);
    List<SystemServiceResponse> getAll();
    SystemServiceResponse update(UUID id, UpdateSystemServiceRequest request);
    void delete(UUID id);
}

