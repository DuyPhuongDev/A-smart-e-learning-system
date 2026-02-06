package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for import result response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultResponse {
    private int successCount;
    private int failedCount;
    private List<ValidationError> errors;

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
