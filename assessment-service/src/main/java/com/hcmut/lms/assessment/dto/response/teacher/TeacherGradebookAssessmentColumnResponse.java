package com.hcmut.lms.assessment.dto.response.teacher;

import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherGradebookAssessmentColumnResponse {
    private UUID assessmentId;
    private String title;
    private GradingRule gradingRule;
}
