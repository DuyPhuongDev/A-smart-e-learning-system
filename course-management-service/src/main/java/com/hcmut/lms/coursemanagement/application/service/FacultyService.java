package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.FacultyRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.FacultyResponse;

import java.util.List;
import java.util.UUID;

public interface FacultyService {
    FacultyResponse createFaculty(FacultyRequest request);
    FacultyResponse updateFaculty(UUID id, FacultyRequest request);
    FacultyResponse getFacultyById(UUID id);
    FacultyResponse getFacultyByCode(String code);
    List<FacultyResponse> getAllFaculties();
    PageResponse<FacultyResponse> getAllFaculties(int page, int size);
    void deleteFaculty(UUID id);
}

