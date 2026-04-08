package com.hcmut.lms.personalization.application.mapper;

import com.hcmut.lms.personalization.application.dto.response.GoalValidationResultResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;
import com.hcmut.lms.personalization.application.dto.response.PreferredSummerSemesterResponse;
import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GoalValidationResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LearningGoalMapper {

  @Mapping(target = "expectedCompletedSemesterId", source = "expectedCompletedSemester")
  @Mapping(target = "prefMainSemLearnIntensity", source = "prefMainSemLearnIntensity")
  @Mapping(target = "goalValidationResult", ignore = true)
  LearningGoalResponse toResponse(LearningGoal entity);

  GoalValidationResultResponse toGoalValidationResultResponse(GoalValidationResult entity);

  @Mapping(target = "learningGoalId", source = "learningGoal.learningGoalId")
  @Mapping(target = "learnIntensity", source = "learningIntensity")
  PreferredSummerSemesterResponse toPreferredSummerSemesterResponse(PreferredSummerSemester entity);

  default String map(LearningIntensity value) {
    return value != null ? value.name() : null;
  }

  default String map(SummerLearningIntensity value) {
    return value != null ? value.name() : null;
  }
}
