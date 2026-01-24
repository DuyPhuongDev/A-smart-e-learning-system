package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.AudioExtractionService;
import com.hcmut.lms.learning.application.service.TranscriptionService;
import com.hcmut.lms.learning.application.service.VideoDownloadService;
import com.hcmut.lms.learning.application.service.VideoProcessingService;
import com.hcmut.lms.learning.application.util.AutoDeletingTempFile;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.client.dto.VideoDownloadUrlResponse;
import com.hcmut.lms.learning.client.dto.VideoTranscriptRequest;
import com.hcmut.lms.learning.client.dto.VideoTranscriptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * Implementation of VideoProcessingService
 * Handles complete video processing pipeline:
 * 1. Fetch lecture info from course-management-service
 * 2. Get download URL (pre-signed for S3, original for YouTube)
 * 3. Download video to temp directory
 * 4. Extract audio using FFmpeg
 * 5. Transcribe audio using AssemblyAI
 * 6. Save transcript to course-management-service
 * 7. Cleanup temp files automatically
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoProcessingServiceImpl implements VideoProcessingService {

    private final CourseManagementClient courseManagementClient;
    private final VideoDownloadService videoDownloadService;
    private final AudioExtractionService audioExtractionService;
    private final TranscriptionService transcriptionService;

    @Override
    public VideoProcessingResult processVideo(UUID lectureId) {
        log.info("Starting video processing for lecture: {}", lectureId);
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Validate lecture exists and is a video lecture
            log.info("[Step 1/6] Fetching lecture info for: {}", lectureId);
            LectureResponse lecture = courseManagementClient.getLectureById(lectureId);

            if (lecture == null) {
                throw new IllegalArgumentException("Lecture not found: " + lectureId);
            }

            if (!"VIDEO".equalsIgnoreCase(lecture.getLectureType())) {
                throw new IllegalArgumentException("Lecture is not a video lecture: " + lecture.getLectureType());
            }

            String videoUrl = lecture.getVideoUrl();
            if (videoUrl == null || videoUrl.isBlank()) {
                throw new IllegalArgumentException("Video URL is empty for lecture: " + lectureId);
            }

            log.info("[Step 1/6] Lecture validated: title='{}', videoUrl='{}'",
                    lecture.getTitle(), videoUrl);

            // Step 2: Get download URL
            log.info("[Step 2/6] Getting download URL...");
            VideoDownloadUrlResponse downloadUrlResponse = courseManagementClient.getVideoDownloadUrl(lectureId);
            String downloadUrl = downloadUrlResponse.getDownloadUrl();
            String sourceType = downloadUrlResponse.getSourceType();
            log.info("[Step 2/6] Source type: {}, download URL obtained", sourceType);

            // Step 3-6: Process video with auto-cleanup using try-with-resources
            return processVideoWithCleanup(lectureId, downloadUrl, sourceType, startTime);

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Video processing failed for lecture {}: {}", lectureId, e.getMessage(), e);
            return VideoProcessingResult.failure(lectureId, e.getMessage(), processingTime);
        }
    }

    /**
     * Process video with automatic cleanup of temp files
     */
    private VideoProcessingResult processVideoWithCleanup(
            UUID lectureId, String downloadUrl, String sourceType, long startTime) {

        // Use try-with-resources for automatic cleanup
        try (
            // Step 3: Download video
            AutoDeletingTempFile videoFile = downloadVideo(lectureId, downloadUrl, sourceType)
        ) {
            try (
                // Step 4: Extract audio
                AutoDeletingTempFile audioFile = extractAudio(videoFile)
            ) {
                // Step 5: Transcribe audio
                TranscriptionService.TranscriptionResult transcriptionResult = transcribeAudio(audioFile);

                // Step 6: Save transcript segments
                List<VideoTranscriptResponse> savedTranscripts = saveTranscriptSegments(
                        lectureId,
                        transcriptionResult
                );

                // Map utterances to segments for response
                List<TranscriptSegment> segments = transcriptionResult.utterances().stream()
                        .map(u -> new TranscriptSegment(u.text(), u.startMs(), u.endMs(), u.confidence()))
                        .toList();

                long processingTime = System.currentTimeMillis() - startTime;
                log.info("Video processing completed successfully for lecture: {} in {}ms, {} segments saved",
                        lectureId, processingTime, savedTranscripts.size());

                // Return first transcript ID (or null if empty)
                UUID firstTranscriptId = savedTranscripts.isEmpty() ? null : savedTranscripts.get(0).getId();

                return VideoProcessingResult.success(
                        lectureId,
                        firstTranscriptId,
                        transcriptionResult.fullText(),
                        segments,
                        sourceType,
                        transcriptionResult.audioDurationSeconds(),
                        transcriptionResult.wordCount(),
                        processingTime
                );
            }
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Video processing failed: {}", e.getMessage(), e);
            return VideoProcessingResult.failure(lectureId, e.getMessage(), processingTime);
        }
        // Temp files are automatically deleted here
    }

    /**
     * Step 3: Download video
     */
    private AutoDeletingTempFile downloadVideo(UUID lectureId, String downloadUrl, String sourceType) {
        log.info("[Step 3/6] Downloading video (source: {})...", sourceType);
        AutoDeletingTempFile videoFile = videoDownloadService.downloadVideo(lectureId, downloadUrl, sourceType);
        try {
            log.info("[Step 3/6] Video downloaded: {} ({} bytes)",
                    videoFile.getPath(), videoFile.size());
        } catch (Exception e) {
            log.info("[Step 3/6] Video downloaded: {}", videoFile.getPath());
        }
        return videoFile;
    }

    /**
     * Step 4: Extract audio using FFmpeg
     */
    private AutoDeletingTempFile extractAudio(AutoDeletingTempFile videoFile) {
        log.info("[Step 4/6] Extracting audio using FFmpeg...");
        AutoDeletingTempFile audioFile = audioExtractionService.extractAudio(videoFile.getPath());
        try {
            log.info("[Step 4/6] Audio extracted: {} ({} bytes)",
                    audioFile.getPath(), audioFile.size());
        } catch (Exception e) {
            log.info("[Step 4/6] Audio extracted: {}", audioFile.getPath());
        }
        return audioFile;
    }

    /**
     * Step 5: Transcribe audio using AssemblyAI
     */
    private TranscriptionService.TranscriptionResult transcribeAudio(AutoDeletingTempFile audioFile) {
        log.info("[Step 5/6] Transcribing audio using AssemblyAI...");
        TranscriptionService.TranscriptionResult result =
                transcriptionService.transcribeWithTimestamps(audioFile.getPath());
        log.info("[Step 5/6] Transcription completed: {} characters, {} words, {}s duration",
                result.fullText().length(), result.wordCount(), result.audioDurationSeconds());
        return result;
    }

    /**
     * Step 6: Save transcript segments to course-management-service
     * Each segment is saved with its own start/end time and index
     */
    private List<VideoTranscriptResponse> saveTranscriptSegments(
            UUID lectureId, TranscriptionService.TranscriptionResult transcriptionResult) {
        log.info("[Step 6/6] Saving {} transcript segments to database...",
                transcriptionResult.utterances().size());

        List<VideoTranscriptRequest> requests = new ArrayList<>();
        var utterances = transcriptionResult.utterances();

        for (int i = 0; i < utterances.size(); i++) {
            var utterance = utterances.get(i);
            int wordCount = utterance.text().isEmpty() ? 0 : utterance.text().split("\\s+").length;

            VideoTranscriptRequest request = VideoTranscriptRequest.builder()
                    .videoLectureId(lectureId)
                    .transcriptText(utterance.text())
                    .languageCode("auto") // Auto-detected
                    .audioDuration((int) ((utterance.endMs() - utterance.startMs()) / 1000))
                    .wordCount(wordCount)
                    .startTimeSeconds((int) (utterance.startMs() / 1000))
                    .endTimeSeconds((int) (utterance.endMs() / 1000))
                    .segmentIndex(i)
                    .build();

            requests.add(request);
        }

        List<VideoTranscriptResponse> responses = courseManagementClient.createVideoTranscripts(requests);
        log.info("[Step 6/6] {} transcript segments saved successfully", responses.size());
        return responses;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<VideoProcessingResult> processVideoAsync(UUID lectureId) {
        log.info("Starting async video processing for lecture: {}", lectureId);
        try {
            VideoProcessingResult result = processVideo(lectureId);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async video processing failed: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
