package com.hcmut.lms.assessment.dto.request.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerOptionRequest {

    @NotBlank(message = "Option content must not be blank")
    private String content;

    @NotNull(message = "Correct flag must be specified")
    private Boolean correct;

    private int orderIndex;
    private String explanation;
}
