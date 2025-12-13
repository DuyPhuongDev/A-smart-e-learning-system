package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.AcademicYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.AcademicYearResponse;

import java.util.List;
import java.util.UUID;

public interface AcademicYearService {
    AcademicYearResponse createAcademicYear(AcademicYearRequest request);
    AcademicYearResponse updateAcademicYear(UUID id, AcademicYearRequest request);
    AcademicYearResponse getAcademicYearById(UUID id);
    List<AcademicYearResponse> getAllAcademicYears();
    PageResponse<AcademicYearResponse> getAllAcademicYears(int page, int size);
    void deleteAcademicYear(UUID id);
}
