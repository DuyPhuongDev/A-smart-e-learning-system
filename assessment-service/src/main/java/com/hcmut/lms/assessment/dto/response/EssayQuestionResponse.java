package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EssayQuestionResponse extends QuestionResponse {
    private String sampleAnswer;
    private int maxFileSize;
    private List<String> acceptedFileTypes;
}
