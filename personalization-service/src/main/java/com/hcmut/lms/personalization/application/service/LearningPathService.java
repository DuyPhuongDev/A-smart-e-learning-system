package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.request.SubjectChangeDto;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface LearningPathService {

  LearningPathResponse getActiveLearningPath(UUID studentId);

  LearningPathResponse getLearningPathById(UUID studentId, UUID learningPathId);

  LearningPathResponse createLearningPath(UUID studentId);

  LearningPathResponse updateLearningPath(UUID studentId, UUID learningPathId, UpdateLearningPathRequest request);

  LearningPathResponse updateLearningPathSubjects(UUID studentId, UUID learningPathId, List<SubjectChangeDto> changes);

  List<LearningPathSectionResponse> getSections(UUID studentId, UUID learningPathId);

  List<LearningPathSubjectResponse> getSubjects(UUID studentId, UUID learningPathId);

  LearningPathGraphResponse getGraph(UUID studentId, UUID learningPathId);

  List<LearningPathSubjectResponse> searchSubjects(UUID studentId, UUID learningPathId, String keyword);

  LearningPathSyncProgressResponse syncLearningPathProgress(UUID studentId, UUID learningPathId);

  LearningPathCurriculumProgressResponse getCurriculumProgress(UUID studentId, UUID learningPathId);
}