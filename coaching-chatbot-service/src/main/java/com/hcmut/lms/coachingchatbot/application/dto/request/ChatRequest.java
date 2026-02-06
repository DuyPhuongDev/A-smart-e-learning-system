package com.hcmut.lms.coachingchatbot.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for asking a question to the coaching chatbot
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Lecture ID is required")
    private UUID lectureId;

    @NotBlank(message = "Question cannot be blank")
    private String question;
}
