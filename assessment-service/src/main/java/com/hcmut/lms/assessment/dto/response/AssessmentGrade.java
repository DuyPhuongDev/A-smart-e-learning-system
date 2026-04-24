package com.hcmut.lms.assessment.dto.response;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
public class AssessmentGrade {
    private UUID assessmentId;
    private String title;
    private BigDecimal weight;
    private AssessmentType assessmentType;

    public static AssessmentGrade toAssessmentGradeResponse(Assessment assessment) {
        return AssessmentGrade.builder()
                .assessmentId(assessment.getId())
                .title(assessment.getTitle())
                .assessmentType(assessment.getAssessmentType())
                .weight(assessment.getWeight())
                .build();
    }
}
