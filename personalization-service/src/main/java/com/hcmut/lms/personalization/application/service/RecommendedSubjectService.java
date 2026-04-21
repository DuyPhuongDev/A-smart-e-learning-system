package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.response.RecommendedSubjectResponse;

import java.util.List;
import java.util.UUID;

public interface RecommendedSubjectService {
  List<RecommendedSubjectResponse> getRecommendedSubjects(UUID studentId, UUID learningPathId);
}
