package com.hcmut.lms.personalization.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompareLearningPathsRequest {

  @NotNull(message = "baselineLearningPathId is required")
  private UUID baselineLearningPathId;

  @NotNull(message = "targetLearningPathId is required")
  private UUID targetLearningPathId;
}

