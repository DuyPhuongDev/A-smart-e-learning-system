package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.IntakeYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.IntakeYearResponse;

import java.util.List;
import java.util.UUID;

public interface IntakeYearService {
    IntakeYearResponse createIntakeYear(IntakeYearRequest request);
    IntakeYearResponse updateIntakeYear(UUID id, IntakeYearRequest request);
    IntakeYearResponse getIntakeYearById(UUID id);
    IntakeYearResponse getIntakeYearByStartYear(Integer startYear);
    List<IntakeYearResponse> getAllIntakeYears();
    PageResponse<IntakeYearResponse> getAllIntakeYears(int page, int size);
    void deleteIntakeYear(UUID id);
}

