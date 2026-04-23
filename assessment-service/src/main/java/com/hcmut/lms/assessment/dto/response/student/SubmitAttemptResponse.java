package com.hcmut.lms.assessment.dto.response.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmitAttemptResponse {
    private UUID attemptId;
    private UUID assessmentId;
    private int attemptNo;
    private AssessmentSubmissionStatus status;
    private Instant submittedAt;
    private Integer takenTime;
    private BigDecimal score;
    private BigDecimal maxScore;
    private String gradingStatus;
    private List<SubmitQuestionResultResponse> questionResults;
}
