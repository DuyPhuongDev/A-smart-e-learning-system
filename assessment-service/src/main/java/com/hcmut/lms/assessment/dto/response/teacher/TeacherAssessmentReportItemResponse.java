package com.hcmut.lms.assessment.dto.response.teacher;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherAssessmentReportItemResponse {
    private UUID assessmentId;
    private String title;
    private AssessmentType assessmentType;
    private GradingRule gradingRule;
    private LocalDateTime closeTime;
    private int totalStudents;
    private int submittedCount;
    private int onTimeCount;
    private int lateCount;
    private int missingCount;
    private BigDecimal averageScore;
    private BigDecimal maxScore;
    private BigDecimal difficultyPercent;
    private List<TeacherQuestionDifficultyResponse> questions;
}
