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
public class LearningPathSubjectResponse {

  private UUID learningPathSubjectId;
  private UUID learningPathId;
  private UUID learningPathSectionId;
  private UUID curriculumSubjectId;
  private UUID subjectId;
  private String subjectCode;
  private String subjectName;
  private Integer credits;
  private String difficultyLevel;
  private BigDecimal avgPassRate;
  private BigDecimal avgGrade;
  private BigDecimal importanceScore;
  private Boolean isCompleted;
  private Integer studyOrder;
  private BigDecimal completionGrade;
  private BigDecimal predictedGrade;
  private Integer attemptNo;
  private Boolean isHighestResult;
  private List<PrerequisiteNodeResponse> prerequisitesGraph;
  private List<PrerequisiteNodeResponse> parallelsGraph;
  private List<PrerequisiteNodeResponse> recommendationsGraph;
  private Instant createdAt;
  private Instant updatedAt;
}

