package com.hcmut.lms.assessment.dto.request.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SaveEssayAnswerRequest extends SaveAnswerRequest {
    private String textContent;
    private String fileUrl;
    private String fileFormat;
    private Integer numPages;
    private Integer wordCount;
}
