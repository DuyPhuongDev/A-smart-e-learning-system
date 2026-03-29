package com.hcmut.lms.assessment.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
    private UUID questionId;
    private String message;
    private List<String> hints;
    private String correctAnswer;
    private GradingStatus gradingStatus;
}
