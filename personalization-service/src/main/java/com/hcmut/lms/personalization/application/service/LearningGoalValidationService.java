package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.request.CreateValidatedLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.ValidateLearningGoalFeasibilityRequest;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalFeasibilityResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;

import java.util.UUID;

public interface LearningGoalValidationService {

  LearningGoalResponse confirmValidatedLearningGoal(UUID studentId, CreateValidatedLearningGoalRequest request);

  LearningGoalFeasibilityResponse validateLearningGoalFeasibility(
      UUID studentId, ValidateLearningGoalFeasibilityRequest request);
}
