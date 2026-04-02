package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningPathSectionResponse {

  private UUID learningPathSectionId;
  private UUID learningPathId;
  private UUID academicYearId;
  private String academicYear;
  private Integer academicYearOrder;
  private UUID semesterId;
  private String semester;
  private Integer semesterOrder;
  private Integer totalCredits;
  private BigDecimal difficultyScore;
  private Instant createdAt;
  private Instant updatedAt;
}

