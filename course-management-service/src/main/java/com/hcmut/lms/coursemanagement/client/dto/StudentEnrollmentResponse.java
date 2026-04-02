package com.hcmut.lms.coursemanagement.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
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
