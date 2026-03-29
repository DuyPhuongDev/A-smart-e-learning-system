package com.hcmut.lms.assessment.dto.request.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionRequest {

    @NotNull(message = "questionId is required")
    private UUID questionId;

    private int orderIndex;
}
