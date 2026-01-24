package com.hcmut.lms.learning.application.strategy.content_process.impl;

import com.hcmut.lms.learning.application.dto.internal.ExtractedContent;
import com.hcmut.lms.learning.application.dto.internal.ProcessingContext;
import com.hcmut.lms.learning.application.service.AudioExtractionService;
import com.hcmut.lms.learning.application.service.TranscriptionService;
import com.hcmut.lms.learning.application.service.VideoDownloadService;
import com.hcmut.lms.learning.application.strategy.content_process.ContentProcessingException;
import com.hcmut.lms.learning.application.strategy.content_process.ContentProcessor;
import com.hcmut.lms.learning.application.util.AutoDeletingTempFile;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.VideoDownloadUrlResponse;
import com.hcmut.lms.learning.client.dto.VideoTranscriptRequest;
import com.hcmut.lms.learning.client.dto.VideoTranscriptResponse;
import com.hcmut.lms.learning.domain.entity.lectureKnowledge.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Processor for video lecture content (YouTube and S3 videos)
 * Downloads video, extracts audio using FFmpeg, transcribes using AssemblyAI,
 * and saves transcript to course-management-service
 *
 * Uses try-with-resources with AutoDeletingTempFile for automatic cleanup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VideoLectureProcessor implements ContentProcessor {

    private final VideoDownloadService videoDownloadService;
    private final AudioExtractionService audioExtractionService;
    private final TranscriptionService transcriptionService;
    private final CourseManagementClient courseManagementClient;

    @Override
    public ContentType[] getSupportedTypes() {
        return new ContentType[]{ContentType.VIDEO_YOUTUBE, ContentType.VIDEO_S3};
    }

    @Override
    public String getProcessorName() {
        return "VideoLectureProcessor";
    }

    @Override
    public ExtractedContent extractContent(ProcessingContext context) throws ContentProcessingException {
        log.info("[{}] Starting video content extraction for lecture: {}",
                getProcessorName(), context.getLectureId());

        String videoUrl = context.getVideoUrl();
        if (videoUrl == null || videoUrl.isBlank()) {
            throw new ContentProcessingException(getProcessorName(), "validation",
                    "Video URL is required for video lecture processing");
        }

        // Step 1: Get download URL from course-management-service
        log.info("[{}] Getting download URL for lecture: {}", getProcessorName(), context.getLectureId());
        VideoDownloadUrlResponse downloadUrlResponse = courseManagementClient.getVideoDownloadUrl(context.getLectureId());
        String downloadUrl = downloadUrlResponse.getDownloadUrl();
        String sourceType = downloadUrlResponse.getSourceType();
        log.info("[{}] Source type: {}, download URL obtained", getProcessorName(), sourceType);

        // Use try-with-resources for automatic cleanup of temp files
        // Both video and audio files will be automatically deleted when exiting the try block
        try (
            // Step 2: Download video to local temp directory (auto-cleanup on close)
            AutoDeletingTempFile videoTempFile = videoDownloadService.downloadVideo(
                    context.getLectureId(), downloadUrl, sourceType)
        ) {
            log.info("[{}] Video downloaded to: {}", getProcessorName(), videoTempFile.getPath());

            try (
                // Step 3: Extract audio using FFmpeg (auto-cleanup on close)
                AutoDeletingTempFile audioTempFile = audioExtractionService.extractAudio(videoTempFile.getPath())
            ) {
                log.info("[{}] Audio extracted to: {}", getProcessorName(), audioTempFile.getPath());

                // Step 4: Transcribe audio using AssemblyAI (with timestamps)
                log.info("[{}] Transcribing audio using AssemblyAI...", getProcessorName());
                TranscriptionService.TranscriptionResult transcriptionResult =
                        transcriptionService.transcribeWithTimestamps(audioTempFile.getPath());
                String transcript = transcriptionResult.fullText();
                log.info("[{}] Transcription completed, length: {} characters, words: {}, segments: {}",
                        getProcessorName(), transcript.length(), transcriptionResult.wordCount(),
                        transcriptionResult.utterances().size());

                // Step 5: Save transcript to course-management-service
                log.info("[{}] Saving transcript to course-management-service...", getProcessorName());
                VideoTranscriptResponse savedTranscript = saveTranscript(
                        context.getLectureId(),
                        transcript,
                        transcriptionResult.audioDurationSeconds(),
                        transcriptionResult.wordCount()
                );
                log.info("[{}] Transcript saved with ID: {}", getProcessorName(), savedTranscript.getId());

                // Step 6: Build metadata with segments
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("sourceType", sourceType);
                metadata.put("videoUrl", videoUrl);
                metadata.put("duration", context.getVideoDuration());
                metadata.put("transcriptionMethod", "assemblyai");
                metadata.put("transcriptId", savedTranscript.getId().toString());
                metadata.put("audioDuration", transcriptionResult.audioDurationSeconds());
                metadata.put("wordCount", transcriptionResult.wordCount());

                // Add transcript segments with timestamps
                List<Map<String, Object>> segments = transcriptionResult.utterances().stream()
                        .map(u -> {
                            Map<String, Object> seg = new HashMap<>();
                            seg.put("text", u.text());
                            seg.put("startMs", u.startMs());
                            seg.put("endMs", u.endMs());
                            seg.put("confidence", u.confidence());
                            return seg;
                        })
                        .toList();
                metadata.put("segments", segments);

                // Step 7: Build and return extracted content for knowledge processing pipeline
                ExtractedContent result = ExtractedContent.builder()
                        .lectureId(context.getLectureId())
                        .sourceType(sourceType)
                        .rawContent(transcript)
                        .metadata(metadata)
                        .build();

                log.info("[{}] Video processing completed successfully for lecture: {}",
                        getProcessorName(), context.getLectureId());

                return result;

            } // Audio file auto-deleted here
        } catch (ContentProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("[{}] Failed to process video content: {}", getProcessorName(), e.getMessage(), e);
            throw new ContentProcessingException(getProcessorName(), "extraction",
                    "Failed to extract content from video: " + e.getMessage(), e);
        }
        // Video file auto-deleted here (even if exception occurred)
    }

    /**
     * Async version of extractContent for background processing
     */
    @Async("taskExecutor")
    public CompletableFuture<ExtractedContent> extractContentAsync(ProcessingContext context) {
        try {
            ExtractedContent result = extractContent(context);
            return CompletableFuture.completedFuture(result);
        } catch (ContentProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Save transcript to course-management-service
     */
    private VideoTranscriptResponse saveTranscript(UUID lectureId, String transcriptText,
                                                    Integer audioDuration, Integer wordCount) {
        VideoTranscriptRequest request = VideoTranscriptRequest.builder()
                .videoLectureId(lectureId)
                .transcriptText(transcriptText)
                .languageCode("en") // Default to English, can be enhanced with language detection
                .audioDuration(audioDuration)
                .wordCount(wordCount)
                .startTimeSeconds(0)
                .endTimeSeconds(audioDuration)
                .segmentIndex(0) // Single segment for now
                .build();

        return courseManagementClient.createVideoTranscript(request);
    }
}
