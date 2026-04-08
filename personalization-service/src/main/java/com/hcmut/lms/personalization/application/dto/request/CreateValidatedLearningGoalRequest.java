package com.hcmut.lms.personalization.application.dto.request;

import com.hcmut.lms.personalization.application.dto.response.LearningGoalFeasibilityResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateValidatedLearningGoalRequest {

  @NotNull(message = "goal is required")
  @Valid
  private CreateLearningGoalRequest goal;

  @Builder.Default
  private List<@Valid CreatePreferredSummerSemesterRequest> summerSemesters = new ArrayList<>();

  @NotNull(message = "validationResult is required")
  @Valid
  private LearningGoalFeasibilityResponse validationResult;
}

