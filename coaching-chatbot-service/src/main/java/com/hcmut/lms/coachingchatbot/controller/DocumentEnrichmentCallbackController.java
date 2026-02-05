package com.hcmut.lms.coachingchatbot.controller;

import com.hcmut.lms.coachingchatbot.application.dto.request.DocumentEnrichmentCallbackRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.DocumentEnrichmentCallbackResponse;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentCallbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public endpoint for Fargate worker to submit document/text enrichment results.
 * This is exposed via API Gateway (no /internal prefix).
 */
@RestController
@RequestMapping("${prefix-api}/document-enrichment-callback")
@RequiredArgsConstructor
@Slf4j
public class DocumentEnrichmentCallbackController {

    private final DocumentEnrichmentCallbackService documentEnrichmentCallbackService;

    /**
     * Receive enriched chunks from Fargate worker
     *
     * POST /api/coaching-chatbot/v1/document-enrichment-callback
     */
    @PostMapping
    public ResponseEntity<DocumentEnrichmentCallbackResponse> receiveEnrichmentCallback(
            @Valid @RequestBody DocumentEnrichmentCallbackRequest request) {
        log.info("Received document enrichment callback for lecture: {}, contentType: {}, chunks: {}",
                request.getLectureId(),
                request.getContentType(),
                request.getChunks() != null ? request.getChunks().size() : 0);

        DocumentEnrichmentCallbackResponse response = documentEnrichmentCallbackService.processEnrichmentCallback(request);

        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
