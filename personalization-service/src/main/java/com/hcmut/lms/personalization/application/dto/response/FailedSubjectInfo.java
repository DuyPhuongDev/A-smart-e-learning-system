package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedSubjectInfo {
  private UUID subjectId;
  private String subjectCode;
  private String subjectName;
  private Integer semesterOrder;
  private Double completionGrade;
  private Integer attemptNo;
}
