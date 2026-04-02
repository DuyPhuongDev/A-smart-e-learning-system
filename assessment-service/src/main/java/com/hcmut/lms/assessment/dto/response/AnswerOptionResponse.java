package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerOptionResponse {
    private UUID id;
    private String content;
    private boolean correct;
    private int orderIndex;
    private String explanation;
}
