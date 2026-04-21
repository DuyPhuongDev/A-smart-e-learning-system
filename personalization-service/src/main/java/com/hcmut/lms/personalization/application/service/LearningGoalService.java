package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.request.CreateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;
import com.hcmut.lms.personalization.application.dto.response.PreferredSummerSemesterResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LearningGoalService {

  Optional<LearningGoalResponse> getCurrentLearningGoal(UUID studentId);

  LearningGoalResponse getLearningGoalById(UUID studentId, UUID learningGoalId);

  LearningGoalResponse createLearningGoal(UUID studentId, CreateLearningGoalRequest request);

  LearningGoalResponse updateLearningGoal(UUID studentId, UUID learningGoalId, UpdateLearningGoalRequest request);

  void deleteLearningGoal(UUID studentId, UUID learningGoalId);

  List<PreferredSummerSemesterResponse> getPreferredSummerSemesters(UUID studentId, UUID learningGoalId);

  PreferredSummerSemesterResponse createPreferredSummerSemester(
      UUID studentId, UUID learningGoalId,
      CreatePreferredSummerSemesterRequest request);

  List<PreferredSummerSemesterResponse> createPreferredSummerSemesters(
      UUID studentId, UUID learningGoalId,
      List<CreatePreferredSummerSemesterRequest> requests);
}


