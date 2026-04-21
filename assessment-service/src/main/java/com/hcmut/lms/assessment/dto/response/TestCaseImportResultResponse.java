package com.hcmut.lms.assessment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseImportResultResponse {
    private int successCount;
    private int failedCount;
    private List<ValidationError> errors;
    private List<TestCaseResponse> testCases;

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
