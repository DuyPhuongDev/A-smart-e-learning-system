package com.hcmut.lms.assessment.dto.response.teacher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherQuestionOptionResponse {
    private UUID optionId;
    private String content;
    private Boolean correct;
}
