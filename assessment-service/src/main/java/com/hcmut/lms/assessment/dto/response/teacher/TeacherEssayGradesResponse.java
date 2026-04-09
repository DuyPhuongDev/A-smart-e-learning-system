package com.hcmut.lms.assessment.dto.response.teacher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherEssayGradesResponse {
    private UUID attemptId;
    private BigDecimal score;
    private BigDecimal maxScore;
    private String gradingStatus;
    private List<TeacherEssayGradeUpdatedQuestionResponse> updatedQuestions;
}
