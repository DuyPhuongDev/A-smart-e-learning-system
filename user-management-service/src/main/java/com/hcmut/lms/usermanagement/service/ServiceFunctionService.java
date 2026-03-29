package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.ServiceFunctionResponse;

import java.util.List;
import java.util.UUID;

public interface ServiceFunctionService {
    ServiceFunctionResponse create(CreateServiceFunctionRequest request);
    ServiceFunctionResponse getById(UUID id);
    List<ServiceFunctionResponse> getAll();
    List<ServiceFunctionResponse> getBySystemServiceId(UUID systemServiceId);
    ServiceFunctionResponse update(UUID id, UpdateServiceFunctionRequest request);
    void delete(UUID id);
}

