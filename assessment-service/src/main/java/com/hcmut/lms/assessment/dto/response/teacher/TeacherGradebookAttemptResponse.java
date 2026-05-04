package com.hcmut.lms.assessment.dto.response.teacher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherGradebookAttemptResponse {
    private UUID attemptId;
    private Integer attemptNo;
    private Instant submittedAt;
    private BigDecimal score;
    private String status;
    private Integer takenTime;
    private Integer correctCount;
    private Integer incorrectCount;
    private Integer skippedCount;
    private Integer totalQuestions;
}
