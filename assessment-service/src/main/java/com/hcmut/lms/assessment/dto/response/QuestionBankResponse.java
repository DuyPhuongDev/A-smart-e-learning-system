package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionBankResponse {
    private UUID id;
    private String name;
    private String description;
    private boolean isPublic;
    private UUID ownerId;
    private int questionCount;
    private Instant createdAt;
    private Instant updatedAt;
}
