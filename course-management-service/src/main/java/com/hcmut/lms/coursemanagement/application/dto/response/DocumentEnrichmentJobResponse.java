package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for document/text lecture enrichment job queue operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEnrichmentJobResponse {

    private int totalRequested;
    private int successfullyQueued;
    private int failed;
    private List<QueuedJob> jobs;
    private List<FailedJob> failures;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueuedJob {
        private UUID lectureId;
        private String messageId;
        private String lectureType;
        private String sourceType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedJob {
        private UUID lectureId;
        private String reason;
    }
}
