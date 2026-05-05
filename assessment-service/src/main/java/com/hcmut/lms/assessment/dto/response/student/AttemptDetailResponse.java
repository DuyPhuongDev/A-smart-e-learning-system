package com.hcmut.lms.assessment.dto.response.student;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDetailResponse {
    private UUID attemptId;
    private UUID assessmentId;
    private String assessmentTitle;
    private AssessmentType assessmentType;
    private int attemptNo;
    private AssessmentSubmissionStatus status;
    private Instant startedAt;
    private Instant expiresAt;
    private Instant closedAt;
    private Instant submittedAt;
    private Integer takenTime;
    private Integer timeLimit;
    private List<AttemptQuestionResponse> questions;
}
