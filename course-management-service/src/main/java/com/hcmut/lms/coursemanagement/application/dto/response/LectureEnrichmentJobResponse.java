package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Unified response DTO for lecture enrichment job queue operation
 * Contains results for VIDEO, DOCUMENT, and TEXT lecture processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureEnrichmentJobResponse {

    private int totalRequested;
    private int successfullyQueued;
    private int failed;

    // Breakdown by type
    private TypeBreakdown videoJobs;
    private TypeBreakdown documentJobs;
    private TypeBreakdown textJobs;

    private List<QueuedJob> jobs;
    private List<FailedJob> failures;

    /**
     * Breakdown statistics for each lecture type
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeBreakdown {
        private int requested;
        private int queued;
        private int failed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueuedJob {
        private UUID lectureId;
        private String messageId;
        private String lectureType;  // VIDEO, DOCUMENT, TEXT
        private String queueType;    // transcription, enrichment
        private String sourceType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedJob {
        private UUID lectureId;
        private String lectureType;
        private String reason;
    }
}
