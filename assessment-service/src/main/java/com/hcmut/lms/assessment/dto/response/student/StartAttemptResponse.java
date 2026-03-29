package com.hcmut.lms.assessment.dto.response.student;

import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartAttemptResponse {
    private UUID attemptId;
    private UUID assessmentId;
    private int attemptNo;
    private AssessmentSubmissionStatus status;
    private Instant startedAt;
    private Instant expiresAt;
    private boolean resumed;
}
