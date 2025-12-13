package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.DepartmentRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DepartmentResponse;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(UUID id, DepartmentRequest request);
    DepartmentResponse getDepartmentById(UUID id);
    List<DepartmentResponse> getAllDepartments();
    PageResponse<DepartmentResponse> getAllDepartments(int page, int size);
    List<DepartmentResponse> getDepartmentsByFacultyId(UUID facultyId);
    PageResponse<DepartmentResponse> getDepartmentsByFacultyId(UUID facultyId, int page, int size);
    void deleteDepartment(UUID id);
}

