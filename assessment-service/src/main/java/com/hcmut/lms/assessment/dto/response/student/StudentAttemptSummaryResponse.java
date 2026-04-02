package com.hcmut.lms.assessment.dto.response.student;

import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttemptSummaryResponse {
    private UUID attemptId;
    private int attemptNo;
    private AssessmentSubmissionStatus status;
    private BigDecimal score;
    private Instant startedAt;
    private Instant submittedAt;
    private Integer takenTime;
    private String gradingStatus;
}
