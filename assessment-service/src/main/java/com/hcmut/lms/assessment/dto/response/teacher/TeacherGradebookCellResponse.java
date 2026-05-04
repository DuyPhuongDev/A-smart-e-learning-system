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
public class TeacherGradebookCellResponse {
    private UUID assessmentId;
    private BigDecimal score;
    private String status;
    private List<TeacherGradebookAttemptResponse> attempts;
}
