package com.hcmut.lms.learning.application.strategy.impl;

import com.hcmut.lms.learning.application.dto.internal.ExtractedContent;
import com.hcmut.lms.learning.application.dto.internal.ProcessingContext;
import com.hcmut.lms.learning.application.service.AudioExtractionService;
import com.hcmut.lms.learning.application.service.TranscriptionService;
import com.hcmut.lms.learning.application.strategy.ContentProcessingException;
import com.hcmut.lms.learning.application.strategy.ContentProcessor;
import com.hcmut.lms.learning.domain.entity.lectureKnowledge.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for video lecture content (YouTube and S3 videos)
 * Extracts audio and transcribes using AssemblyAI
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VideoLectureProcessor implements ContentProcessor {

    private final AudioExtractionService audioExtractionService;
    private final TranscriptionService transcriptionService;

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

        try {
            // Step 1: Determine video source type
            boolean isYouTube = isYouTubeUrl(videoUrl);
            String sourceType = isYouTube ? "youtube" : "s3";
            log.info("[{}] Detected video source: {}", getProcessorName(), sourceType);

            // Step 2: Get audio URL or extract audio
            String audioUrl;
            if (isYouTube) {
                // For YouTube, we can directly use the video URL with AssemblyAI
                // AssemblyAI can fetch YouTube audio directly
                audioUrl = videoUrl;
                log.info("[{}] Using YouTube URL directly for transcription", getProcessorName());
            } else {
                // For S3 videos, we may need to extract audio or use direct URL
                audioUrl = audioExtractionService.getAudioUrl(videoUrl);
                log.info("[{}] Extracted/retrieved audio URL from S3 video", getProcessorName());
            }

            // Step 3: Transcribe audio using AssemblyAI
            log.info("[{}] Starting transcription...", getProcessorName());
            String transcript = transcriptionService.transcribe(audioUrl);
            log.info("[{}] Transcription completed, length: {} characters",
                    getProcessorName(), transcript.length());

            // Step 4: Build metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sourceType", sourceType);
            metadata.put("videoUrl", videoUrl);
            metadata.put("duration", context.getVideoDuration());
            metadata.put("transcriptionMethod", "assemblyai");

            // Step 5: Build and return extracted content
            return ExtractedContent.builder()
                    .lectureId(context.getLectureId())
                    .sourceType(sourceType)
                    .rawContent(transcript)
                    .metadata(metadata)
                    .build();

        } catch (Exception e) {
            log.error("[{}] Failed to process video content: {}", getProcessorName(), e.getMessage(), e);
            throw new ContentProcessingException(getProcessorName(), "extraction",
                    "Failed to extract content from video: " + e.getMessage(), e);
        }
    }

    /**
     * Check if the URL is a YouTube URL
     */
    private boolean isYouTubeUrl(String url) {
        if (url == null) return false;
        return url.contains("youtube.com") || url.contains("youtu.be");
    }
}
