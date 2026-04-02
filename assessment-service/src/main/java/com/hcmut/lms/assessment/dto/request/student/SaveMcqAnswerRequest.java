package com.hcmut.lms.assessment.dto.request.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SaveMcqAnswerRequest extends SaveAnswerRequest {
    private List<UUID> selectedOptionIds;
}
