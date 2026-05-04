package com.hcmut.lms.assessment.dto.request.student;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RunTestcaseRequest {
    private String languageCode;
    private String sourceCode;
}