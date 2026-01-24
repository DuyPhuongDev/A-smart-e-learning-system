package com.hcmut.lms.learning.controller;

import com.hcmut.lms.learning.application.dto.request.ProcessLectureRequest;
import com.hcmut.lms.learning.application.dto.response.ProcessingStatusResponse;
import com.hcmut.lms.learning.application.service.DocumentProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for document processing operations
 */
@RestController
@RequestMapping("${prefix-api:}/document-processing")
@RequiredArgsConstructor
@Slf4j
public class DocumentProcessingController {

    private final DocumentProcessingService documentProcessingService;

    /**
     * Process a single lecture
     */
    @PostMapping("/process")
    public ResponseEntity<ProcessingStatusResponse> processLecture(
            @Valid @RequestBody ProcessLectureRequest request) {
        log.info("Processing lecture request: {}", request.getLectureId());
        ProcessingStatusResponse response = documentProcessingService.processLecture(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Process multiple lectures
     */
    @PostMapping("/process/batch")
    public ResponseEntity<List<ProcessingStatusResponse>> processLectures(
            @RequestBody List<UUID> lectureIds) {
        log.info("Batch processing {} lectures", lectureIds.size());
        List<ProcessingStatusResponse> responses = documentProcessingService.processLectures(lectureIds);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responses);
    }

    /**
     * Process all lectures in a chapter
     */
    @PostMapping("/process/chapter/{chapterId}")
    public ResponseEntity<List<ProcessingStatusResponse>> processChapterLectures(
            @PathVariable UUID chapterId) {
        log.info("Processing all lectures in chapter: {}", chapterId);
        List<ProcessingStatusResponse> responses = documentProcessingService.processChapterLectures(chapterId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responses);
    }

    /**
     * Get processing status for a lecture
     */
    @GetMapping("/status/lecture/{lectureId}")
    public ResponseEntity<ProcessingStatusResponse> getProcessingStatus(
            @PathVariable UUID lectureId) {
        ProcessingStatusResponse response = documentProcessingService.getProcessingStatus(lectureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get processing status by lecture knowledge ID
     */
    @GetMapping("/status/knowledge/{lectureKnowledgeId}")
    public ResponseEntity<ProcessingStatusResponse> getProcessingStatusByKnowledgeId(
            @PathVariable UUID lectureKnowledgeId) {
        ProcessingStatusResponse response = documentProcessingService.getProcessingStatusByKnowledgeId(lectureKnowledgeId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retry failed processing
     */
    @PostMapping("/retry/{lectureId}")
    public ResponseEntity<ProcessingStatusResponse> retryProcessing(
            @PathVariable UUID lectureId) {
        log.info("Retrying processing for lecture: {}", lectureId);
        ProcessingStatusResponse response = documentProcessingService.retryProcessing(lectureId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Cancel ongoing processing
     */
    @DeleteMapping("/cancel/{lectureId}")
    public ResponseEntity<Void> cancelProcessing(
            @PathVariable UUID lectureId) {
        log.info("Cancelling processing for lecture: {}", lectureId);
        documentProcessingService.cancelProcessing(lectureId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reprocess a lecture (delete existing and process again)
     */
    @PostMapping("/reprocess/{lectureId}")
    public ResponseEntity<ProcessingStatusResponse> reprocessLecture(
            @PathVariable UUID lectureId) {
        log.info("Reprocessing lecture: {}", lectureId);
        ProcessingStatusResponse response = documentProcessingService.reprocessLecture(lectureId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
