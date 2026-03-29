package com.hcmut.lms.assessment.dto.request.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SaveCodingAnswerRequest extends SaveAnswerRequest {
    private String code;
    private String language;
}
