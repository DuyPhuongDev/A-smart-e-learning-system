package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.StudentLearningProgressResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentSubjectDetailResponse;

import java.util.UUID;

public interface StudentProgressService {
    StudentLearningProgressResponse getStudentLearningProgress(UUID userId);
    StudentSubjectDetailResponse getStudentSubjectDetail(UUID subjectId, UUID userId);
}
