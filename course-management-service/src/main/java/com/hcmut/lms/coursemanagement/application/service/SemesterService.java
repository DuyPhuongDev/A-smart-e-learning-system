package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SemesterRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;

import java.util.List;
import java.util.UUID;

public interface SemesterService {
    SemesterResponse createSemester(SemesterRequest request);
    SemesterResponse updateSemester(UUID id, SemesterRequest request);
    SemesterResponse getSemesterById(UUID id);
    List<SemesterResponse> getAllSemesters();
    PageResponse<SemesterResponse> getAllSemesters(int page, int size);
    List<SemesterResponse> getSemestersByAcademicYearId(UUID academicYearId);
    PageResponse<SemesterResponse> getSemestersByAcademicYearId(UUID academicYearId, int page, int size);
    void deleteSemester(UUID id);
    SemesterResponse getCurrentSemester();
}


