package com.hcmut.lms.learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollmentResponse {
  private UUID id;
  private UUID studentId;
  private UUID classId;
  private String enrolledAt;
  private String completionTime;
  private Double finalGrade;
  private Integer attemptNo;
  private Double progressPercentage;
  private Boolean isPassed;
}
