package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SpecializationRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SpecializationResponse;

import java.util.List;
import java.util.UUID;

public interface SpecializationService {
    SpecializationResponse createSpecialization(SpecializationRequest request);
    SpecializationResponse updateSpecialization(UUID id, SpecializationRequest request);
    SpecializationResponse getSpecializationById(UUID id);
    SpecializationResponse getSpecializationByCode(String code);
    List<SpecializationResponse> getAllSpecializations();
    PageResponse<SpecializationResponse> getAllSpecializations(int page, int size);
    List<SpecializationResponse> getSpecializationsByDepartmentId(UUID departmentId);
    PageResponse<SpecializationResponse> getSpecializationsByDepartmentId(UUID departmentId, int page, int size);
    void deleteSpecialization(UUID id);
}

