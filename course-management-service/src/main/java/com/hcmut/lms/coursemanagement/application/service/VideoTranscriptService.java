package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoTranscriptService {
    VideoTranscriptResponse createTranscript(VideoTranscriptRequest request);
    List<VideoTranscriptResponse> createTranscripts(List<VideoTranscriptRequest> requests);
    VideoTranscriptResponse updateTranscript(UUID id, VideoTranscriptRequest request);
    Optional<VideoTranscriptResponse> getTranscriptByVideoLectureId(UUID videoLectureId);
    Optional<VideoTranscriptResponse> getTranscriptById(UUID id);
    void deleteTranscript(UUID id);
    void deleteTranscriptByVideoLectureId(UUID videoLectureId);
    boolean existsByVideoLectureId(UUID videoLectureId);
}
