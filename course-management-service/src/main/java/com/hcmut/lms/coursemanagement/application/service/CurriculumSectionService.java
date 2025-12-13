package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumSectionResponse;

import java.util.List;
import java.util.UUID;

public interface CurriculumSectionService {
    CurriculumSectionResponse createCurriculumSection(CurriculumSectionRequest request);
    CurriculumSectionResponse updateCurriculumSection(UUID id, CurriculumSectionRequest request);
    CurriculumSectionResponse getCurriculumSectionById(UUID id);
    List<CurriculumSectionResponse> getAllCurriculumSections();
    List<CurriculumSectionResponse> getCurriculumSectionsByCurriculumId(String code, UUID specializationId, UUID intakeYearId);
    void deleteCurriculumSection(UUID id);
}

