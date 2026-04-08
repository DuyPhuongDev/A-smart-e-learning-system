package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.InternalGraduationRequirementResponse;

import java.util.List;
import java.util.UUID;

public interface GraduationRequirementService {

    List<InternalGraduationRequirementResponse> getActiveRequirementsByStudentId(UUID studentId);
}

