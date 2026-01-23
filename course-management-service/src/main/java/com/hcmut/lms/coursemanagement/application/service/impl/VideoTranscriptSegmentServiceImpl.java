package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;
import com.hcmut.lms.coursemanagement.application.service.VideoTranscriptSegmentService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoTranscript;
import com.hcmut.lms.coursemanagement.domain.repository.VideoTranscriptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoTranscriptSegmentServiceImpl implements VideoTranscriptSegmentService {

    private final VideoTranscriptRepository videoTranscriptRepository;

    // Default segment duration: 10 minutes (600 seconds)
    private static final Integer DEFAULT_SEGMENT_DURATION = 600;

    @Override
    public List<VideoTranscriptRequest> createTranscriptSegments(
            UUID videoLectureId,
            String fullTranscript,
            Integer segmentDurationSeconds,
            Integer totalVideoDurationSeconds) {

        log.info("Creating transcript segments for video lecture: {}", videoLectureId);

        if (fullTranscript == null || fullTranscript.isBlank()) {
            log.warn("Transcript is empty for video lecture: {}", videoLectureId);
            return new ArrayList<>();
        }

        // Use default if not specified
        Integer duration = segmentDurationSeconds != null ? segmentDurationSeconds : DEFAULT_SEGMENT_DURATION;

        List<VideoTranscriptRequest> segments = new ArrayList<>();
        String[] sentences = fullTranscript.split("\\. ");

        StringBuilder currentSegment = new StringBuilder();
        Integer currentStartTime = 0;
        int segmentIndex = 0;

        // Estimate: ~2-3 words per second
        double wordsPerSecond = 2.5;

        for (String sentence : sentences) {
            if (sentence.isBlank()) continue;

            currentSegment.append(sentence).append(". ");

            // Calculate estimated duration based on word count
            int currentWords = currentSegment.toString().split("\\s+").length;
            int estimatedDuration = (int) (currentWords / wordsPerSecond);

            // Create segment when duration is reached or at the end
            if (estimatedDuration >= duration || currentSegment.toString().equals(fullTranscript)) {
                Integer endTime = Math.min(currentStartTime + duration, totalVideoDurationSeconds);

                VideoTranscriptRequest segmentRequest = VideoTranscriptRequest.builder()
                        .videoLectureId(videoLectureId)
                        .transcriptText(currentSegment.toString().trim())
                        .languageCode("en")
                        .startTimeSeconds(currentStartTime)
                        .endTimeSeconds(endTime)
                        .segmentIndex(segmentIndex)
                        .build();

                segments.add(segmentRequest);

                currentStartTime = endTime;
                currentSegment = new StringBuilder();
                segmentIndex++;
            }
        }

        log.info("Created {} transcript segments for video lecture: {}", segments.size(), videoLectureId);
        return segments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoTranscriptResponse> getTranscriptSegments(UUID videoLectureId) {
        log.info("Getting transcript segments for video lecture: {}", videoLectureId);

        List<VideoTranscript> transcripts = videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId);

        return transcripts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoTranscriptResponse> getTranscriptSegmentsInRange(
            UUID videoLectureId,
            Integer startTimeSeconds,
            Integer endTimeSeconds) {

        log.info("Getting transcript segments in range [{}-{}] for video lecture: {}",
                startTimeSeconds, endTimeSeconds, videoLectureId);

        List<VideoTranscript> transcripts = videoTranscriptRepository.findTranscriptsInTimeRange(
                videoLectureId, startTimeSeconds, endTimeSeconds);

        return transcripts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String mergeTranscriptSegments(UUID videoLectureId) {
        log.info("Merging transcript segments for video lecture: {}", videoLectureId);

        List<VideoTranscript> segments = videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId);

        StringBuilder mergedTranscript = new StringBuilder();
        for (VideoTranscript segment : segments) {
            if (!mergedTranscript.isEmpty()) {
                mergedTranscript.append(" ");
            }
            mergedTranscript.append(segment.getTranscriptText());
        }

        log.info("Merged {} segments into complete transcript", segments.size());
        return mergedTranscript.toString();
    }

    @Override
    @Transactional
    public void deleteTranscriptSegments(UUID videoLectureId) {
        log.info("Deleting all transcript segments for video lecture: {}", videoLectureId);

        // Find all segments and delete them
        List<VideoTranscript> segments = videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId);
        for (VideoTranscript segment : segments) {
            videoTranscriptRepository.deleteById(segment.getId());
        }

        log.info("Deleted all transcript segments for video lecture: {}", videoLectureId);
    }

    /**
     * Convert VideoTranscript entity to VideoTranscriptResponse
     */
    private VideoTranscriptResponse mapToResponse(VideoTranscript transcript) {
        return VideoTranscriptResponse.builder()
                .id(transcript.getId())
                .videoLectureId(transcript.getVideoLecture().getId())
                .transcriptText(transcript.getTranscriptText())
                .languageCode(transcript.getLanguageCode())
                .audioDuration(transcript.getAudioDuration())
                .wordCount(transcript.getWordCount())
                .startTimeSeconds(transcript.getStartTimeSeconds())
                .endTimeSeconds(transcript.getEndTimeSeconds())
                .segmentIndex(transcript.getSegmentIndex())
                .createdAt(transcript.getCreatedAt())
                .updatedAt(transcript.getUpdatedAt())
                .build();
    }
}
