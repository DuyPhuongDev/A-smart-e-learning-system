package com.hcmut.lms.assessment.dto.request.questionbank;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String description;
}
