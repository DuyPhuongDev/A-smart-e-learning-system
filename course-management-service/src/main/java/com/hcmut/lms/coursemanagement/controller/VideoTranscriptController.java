package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;
import com.hcmut.lms.coursemanagement.application.service.VideoTranscriptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/video-transcripts")
@RequiredArgsConstructor
@Slf4j
public class VideoTranscriptController {

    private final VideoTranscriptService videoTranscriptService;

    @PostMapping
    public ResponseEntity<VideoTranscriptResponse> createTranscript(
            @Valid @RequestBody VideoTranscriptRequest request) {
        log.info("Creating transcript for video lecture: {}", request.getVideoLectureId());
        VideoTranscriptResponse response = videoTranscriptService.createTranscript(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoTranscriptResponse> updateTranscript(
            @PathVariable UUID id,
            @Valid @RequestBody VideoTranscriptRequest request) {
        log.info("Updating transcript with id: {}", id);
        VideoTranscriptResponse response = videoTranscriptService.updateTranscript(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/video-lecture/{videoLectureId}")
    public ResponseEntity<VideoTranscriptResponse> getTranscriptByVideoLectureId(
            @PathVariable UUID videoLectureId) {
        log.info("Getting transcript for video lecture: {}", videoLectureId);
        return videoTranscriptService.getTranscriptByVideoLectureId(videoLectureId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoTranscriptResponse> getTranscriptById(
            @PathVariable UUID id) {
        log.info("Getting transcript with id: {}", id);
        return videoTranscriptService.getTranscriptById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranscript(
            @PathVariable UUID id) {
        log.info("Deleting transcript with id: {}", id);
        videoTranscriptService.deleteTranscript(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/video-lecture/{videoLectureId}")
    public ResponseEntity<Void> deleteTranscriptByVideoLectureId(
            @PathVariable UUID videoLectureId) {
        log.info("Deleting transcript for video lecture: {}", videoLectureId);
        videoTranscriptService.deleteTranscriptByVideoLectureId(videoLectureId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/video-lecture/{videoLectureId}/exists")
    public ResponseEntity<Boolean> existsTranscript(
            @PathVariable UUID videoLectureId) {
        boolean exists = videoTranscriptService.existsByVideoLectureId(videoLectureId);
        return ResponseEntity.ok(exists);
    }
}
