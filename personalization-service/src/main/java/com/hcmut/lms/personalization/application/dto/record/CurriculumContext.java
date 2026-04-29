package com.hcmut.lms.personalization.application.dto.record;

import com.hcmut.lms.personalization.client.dto.CurriculumResolutionResponse;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;

import java.util.UUID;

public record CurriculumContext(LearningGoal goal, UUID specializationId,
                                 StudentLearningProgressResponse progress, CurriculumResolutionResponse curriculum) {}
