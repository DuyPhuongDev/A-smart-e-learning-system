package com.hcmut.lms.assessment.dto.response.student;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveAnswerResponse {
    private UUID attemptId;
    private UUID questionId;
    private Instant savedAt;
}
