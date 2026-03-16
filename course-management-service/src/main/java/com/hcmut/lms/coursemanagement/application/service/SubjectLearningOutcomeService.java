package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.SubjectLearningOutcomeResponse;

import java.util.List;
import java.util.UUID;

public interface SubjectLearningOutcomeService {
    List<SubjectLearningOutcomeResponse> getBySubjectId(UUID subjectId);
}
