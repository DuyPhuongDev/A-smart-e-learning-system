package com.hcmut.lms.assessment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic DTO for CSV import results.
 * Contains summary counts, per-row validation errors, and the created questions.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionImportResultResponse {
    private int successCount;
    private int failedCount;
    private List<ValidationError> errors;

    /**
     * List of successfully created questions for FE to display.
     */
    private List<QuestionResponse> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private int row;
        private String field;
        private String message;
    }
}

