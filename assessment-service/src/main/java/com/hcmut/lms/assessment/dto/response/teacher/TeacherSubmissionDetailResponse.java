package com.hcmut.lms.assessment.dto.response.teacher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherSubmissionDetailResponse {
    private UUID attemptId;
    private UUID assessmentId;
    private String assessmentTitle;
    private UUID studentId;
    private String studentCode;
    private String studentName;
    private Integer attemptNo;
    private Instant submittedAt;
    private Integer takenTime;
    private BigDecimal score;
    private BigDecimal maxScore;
    private String gradingStatus;
    private List<TeacherQuestionReviewResponse> questions;
}
