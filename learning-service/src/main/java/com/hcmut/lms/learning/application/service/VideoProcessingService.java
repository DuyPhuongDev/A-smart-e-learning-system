package com.hcmut.lms.learning.application.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface VideoProcessingService {

    VideoProcessingResult processVideo(UUID lectureId);

    CompletableFuture<VideoProcessingResult> processVideoAsync(UUID lectureId);

    record VideoProcessingResult(
            UUID lectureId,
            UUID transcriptId,
            String transcript,
            List<TranscriptSegment> segments,
            String sourceType,
            Integer audioDurationSeconds,
            Integer wordCount,
            String status,
            String errorMessage,
            long processingTimeMs
    ) {
        public static VideoProcessingResult success(
                UUID lectureId, UUID transcriptId, String transcript, List<TranscriptSegment> segments,
                String sourceType, Integer audioDuration, Integer wordCount, long processingTimeMs) {
            return new VideoProcessingResult(
                    lectureId, transcriptId, transcript, segments, sourceType,
                    audioDuration, wordCount, "SUCCESS", null, processingTimeMs);
        }

        public static VideoProcessingResult failure(UUID lectureId, String errorMessage, long processingTimeMs) {
            return new VideoProcessingResult(
                    lectureId, null, null, null, null,
                    null, null, "FAILED", errorMessage, processingTimeMs);
        }
    }

    record TranscriptSegment(
            String text,
            long startMs,
            long endMs,
            double confidence
    ) {}
}
