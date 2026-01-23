package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;
import com.hcmut.lms.coursemanagement.application.mapper.VideoTranscriptMapper;
import com.hcmut.lms.coursemanagement.application.service.VideoTranscriptService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoTranscript;
import com.hcmut.lms.coursemanagement.domain.repository.VideoTranscriptRepository;
import com.hcmut.lms.coursemanagement.domain.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for VideoTranscript
 * Handles operations related to video transcripts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoTranscriptServiceImpl implements VideoTranscriptService {

    private final VideoTranscriptRepository videoTranscriptRepository;
    private final VideoLectureRepository videoLectureRepository;
    private final VideoTranscriptMapper videoTranscriptMapper;

    @Override
    @Transactional
    public VideoTranscriptResponse createTranscript(VideoTranscriptRequest request) {
        log.info("Creating transcript for video lecture: {}", request.getVideoLectureId());

        VideoLecture videoLecture = videoLectureRepository.findById(request.getVideoLectureId())
                .orElseThrow(() -> new RuntimeException("Video lecture not found with id: " + request.getVideoLectureId()));

        if (videoTranscriptRepository.existsByVideoLectureId(request.getVideoLectureId())) {
            log.warn("Transcript already exists for video lecture: {}", request.getVideoLectureId());
            throw new RuntimeException("Transcript already exists for this video lecture");
        }

        VideoTranscript transcript = VideoTranscript.builder()
                .id(UUID.randomUUID())
                .videoLecture(videoLecture)
                .transcriptText(request.getTranscriptText())
                .languageCode(request.getLanguageCode() != null ? request.getLanguageCode() : "en")
                .audioDuration(request.getAudioDuration())
                .wordCount(request.getWordCount())
                .startTimeSeconds(request.getStartTimeSeconds() != null ? request.getStartTimeSeconds() : 0)
                .endTimeSeconds(request.getEndTimeSeconds())
                .segmentIndex(request.getSegmentIndex())
                .build();

        VideoTranscript saved = videoTranscriptRepository.save(transcript);
        log.info("Transcript created successfully with id: {}", saved.getId());
        return videoTranscriptMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public VideoTranscriptResponse updateTranscript(UUID id, VideoTranscriptRequest request) {
        log.info("Updating transcript with id: {}", id);

        VideoTranscript transcript = videoTranscriptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transcript not found with id: " + id));

        if (request.getTranscriptText() != null) {
            transcript.setTranscriptText(request.getTranscriptText());
        }
        if (request.getLanguageCode() != null) {
            transcript.setLanguageCode(request.getLanguageCode());
        }
        if (request.getAudioDuration() != null) {
            transcript.setAudioDuration(request.getAudioDuration());
        }
        if (request.getWordCount() != null) {
            transcript.setWordCount(request.getWordCount());
        }
        if (request.getStartTimeSeconds() != null) {
            transcript.setStartTimeSeconds(request.getStartTimeSeconds());
        }
        if (request.getEndTimeSeconds() != null) {
            transcript.setEndTimeSeconds(request.getEndTimeSeconds());
        }
        if (request.getSegmentIndex() != null) {
            transcript.setSegmentIndex(request.getSegmentIndex());
        }

        VideoTranscript updated = videoTranscriptRepository.save(transcript);
        log.info("Transcript updated successfully");
        return videoTranscriptMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VideoTranscriptResponse> getTranscriptByVideoLectureId(UUID videoLectureId) {
        log.info("Getting transcript for video lecture: {}", videoLectureId);
        return videoTranscriptRepository.findByVideoLectureId(videoLectureId)
                .map(videoTranscriptMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VideoTranscriptResponse> getTranscriptById(UUID id) {
        log.info("Getting transcript with id: {}", id);
        return videoTranscriptRepository.findById(id)
                .map(videoTranscriptMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteTranscript(UUID id) {
        log.info("Deleting transcript with id: {}", id);
        videoTranscriptRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteTranscriptByVideoLectureId(UUID videoLectureId) {
        log.info("Deleting transcript for video lecture: {}", videoLectureId);
        videoTranscriptRepository.findByVideoLectureId(videoLectureId)
                .ifPresent(videoTranscriptRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByVideoLectureId(UUID videoLectureId) {
        return videoTranscriptRepository.existsByVideoLectureId(videoLectureId);
    }
}


