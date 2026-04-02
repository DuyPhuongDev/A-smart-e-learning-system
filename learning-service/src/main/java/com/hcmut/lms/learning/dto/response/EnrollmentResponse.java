package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentResponse {
  private UUID id;
  private UUID studentId;
  private UUID classId;
  private LocalDateTime enrolledAt;
  private Double finalGrade;
  private Boolean isPassed;
  private Integer attemptNo;
}
