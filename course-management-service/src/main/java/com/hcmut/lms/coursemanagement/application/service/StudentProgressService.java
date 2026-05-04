package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.SemesterGpaResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentLearningProgressResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentSubjectDetailResponse;

import java.util.List;
import java.util.UUID;

public interface StudentProgressService {
    StudentLearningProgressResponse getStudentLearningProgress(UUID userId, UUID specializationId);
    StudentSubjectDetailResponse getStudentSubjectDetail(UUID subjectId, UUID userId);
    List<SemesterGpaResponse> getGpaTrend(UUID userId, UUID specializationId);
}
