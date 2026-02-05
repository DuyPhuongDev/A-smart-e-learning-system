package com.hcmut.lms.coachingchatbot.controller;

import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackBatchRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.TranscriptionCallbackResponse;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptionCallbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public endpoint for Fargate worker to submit transcription results.
 * This is exposed via API Gateway (no /internal prefix).
 */
@RestController
@RequestMapping("${prefix-api}/transcription-callback")
@RequiredArgsConstructor
@Slf4j
public class TranscriptionCallbackController {

    private final TranscriptionCallbackService transcriptionCallbackService;

    @PostMapping
    public ResponseEntity<TranscriptionCallbackResponse> receiveTranscriptionCallback(
            @Valid @RequestBody TranscriptionCallbackBatchRequest request) {
        log.info("Received transcription callback for lecture: {}, segments: {}",
                request.getVideoLectureId(),
                request.getSegments() != null ? request.getSegments().size() : 0);

        TranscriptionCallbackResponse response = transcriptionCallbackService.processTranscriptionCallbackBatch(request);
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.internalServerError().body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
