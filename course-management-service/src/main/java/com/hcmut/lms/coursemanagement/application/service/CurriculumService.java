package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumFullResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumResponse;

import java.util.List;
import java.util.UUID;

public interface CurriculumService {
    CurriculumResponse createCurriculum(CurriculumRequest request);
    CurriculumResponse updateCurriculum(String code, UUID specializationId, UUID intakeYearId, CurriculumRequest request);
    CurriculumResponse getCurriculumById(String code, UUID specializationId, UUID intakeYearId);
    List<CurriculumResponse> getAllCurriculums();
    PageResponse<CurriculumResponse> getAllCurriculums(int page, int size);
    List<CurriculumResponse> getCurriculumsBySpecializationId(UUID specializationId);
    PageResponse<CurriculumResponse> getCurriculumsBySpecializationId(UUID specializationId, int page, int size);
    List<CurriculumResponse> getCurriculumsByIntakeYearId(UUID intakeYearId);
    PageResponse<CurriculumResponse> getCurriculumsByIntakeYearId(UUID intakeYearId, int page, int size);
    CurriculumResponse resolveCurriculumBySpecializationAndIntakeYear(UUID specializationId, Integer intakeYear);
    void deleteCurriculum(String code, UUID specializationId, UUID intakeYearId);
    CurriculumFullResponse getCurriculumFull(String curriculumCode);
}

