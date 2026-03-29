package com.hcmut.lms.assessment.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EssaySubmissionDto extends SubmissionDto {
    private String textContent;
    private String fileUrl;
}
