package com.hcmut.lms.personalization.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollmentWithSubjectResponse {
  private UUID id;
  private UUID studentId;
  private UUID classId;
  private UUID subjectId;
  private Double finalGrade;
  private Integer attemptNo;
  private Boolean isPassed;
  private String gradingType;
  private String enrolledAt;
  private String completionTime;
}
