package com.hcmut.lms.learning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestEnrollmentRequest {
  private UUID studentId;
  private UUID classId;
  private UUID subjectId;
  private Double finalGrade;
  private Boolean isPassed;
  private Integer attemptNo;
  private String gradingType;
}
