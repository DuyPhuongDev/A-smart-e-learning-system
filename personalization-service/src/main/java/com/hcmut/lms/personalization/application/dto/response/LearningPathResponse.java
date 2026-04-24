package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningPathResponse {

  private UUID learningPathId;
  private UUID studentId;
  private UUID learningGoalId;
  private String curriculumCode;
  private Integer totalCredits;
  private Integer earnedCredits;
  private Integer requiredCredits;
  private Integer estimatedDurationSemesters;
  private BigDecimal predictedGpa;
  private BigDecimal completionRate;
  private String riskLevel;
  private Boolean isActive;
  private List<LearningPathSectionResponse> sections;
  private List<LearningPathSubjectResponse> subjects;
  private LearningPathGraphResponse graph;
  private List<LearningPathValidationConflictResponse> validationConflicts;
  private Instant createdAt;
  private Instant updatedAt;
}
