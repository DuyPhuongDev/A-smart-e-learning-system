package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurriculumSectionSubjectResponse {

  private UUID learningPathSubjectId;
  private UUID subjectId;
  private UUID learningPathSectionId;
  private String subjectCode;
  private String subjectName;
  private Integer credits;
  private Boolean isCompleted;
  private BigDecimal completionGrade;
  private BigDecimal predictedGrade;
  private String semesterLabel;
  private Boolean inLearningPath;
}
