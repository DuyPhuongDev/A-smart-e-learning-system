package com.hcmut.lms.assessment.dto.response.teacher;

import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherQuestionDifficultyResponse {
    private UUID questionId;
    private int orderIndex;
    private QuestionType questionType;
    private String content;
    private int incorrectCount;
    private int totalSubmitted;
    private BigDecimal incorrectPercent;
}
