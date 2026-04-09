package com.hcmut.lms.assessment.dto.request.teacher;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TeacherEssayGradeItemRequest {

    @NotNull(message = "questionId is required")
    private UUID questionId;

    @NotNull(message = "score is required")
    private BigDecimal score;

    private String feedback;
}
