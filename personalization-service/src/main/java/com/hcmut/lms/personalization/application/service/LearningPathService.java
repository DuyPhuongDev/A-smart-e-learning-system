package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.request.CompareLearningPathsRequest;
import com.hcmut.lms.personalization.application.dto.request.OptimizeLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface LearningPathService {

  LearningPathResponse getActiveLearningPath(UUID studentId);

  LearningPathResponse getLearningPathById(UUID studentId, UUID learningPathId);

  LearningPathResponse createLearningPath(UUID studentId);

  LearningPathResponse updateLearningPath(UUID studentId, UUID learningPathId, UpdateLearningPathRequest request);

  List<LearningPathSectionResponse> getSections(UUID studentId, UUID learningPathId);

  List<LearningPathSubjectResponse> getSubjects(UUID studentId, UUID learningPathId);

  List<LearningPathOptimizationCandidateResponse> optimize(
      UUID studentId, UUID learningPathId, OptimizeLearningPathRequest request);

  List<LearningPathValidationConflictResponse> validate(UUID studentId, UUID learningPathId);

  List<LearningPathChangeRecordResponse> getChanges(UUID studentId, UUID learningPathId);

  LearningPathGraphResponse getGraph(UUID studentId, UUID learningPathId);

  LearningPathComparisonResultResponse compare(UUID studentId, CompareLearningPathsRequest request);
}

