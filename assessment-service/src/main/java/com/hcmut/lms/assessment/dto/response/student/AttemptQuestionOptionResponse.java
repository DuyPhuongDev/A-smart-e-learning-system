package com.hcmut.lms.assessment.dto.response.student;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptQuestionOptionResponse {
    private UUID id;
    private String content;
    private int orderIndex;
    private String explanation;
}
