package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.response.DocumentDownloadUrlResponse;
import com.hcmut.lms.coursemanagement.application.service.DocumentLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal controller for document lecture service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("${prefix-api}/internal/document-lectures")
@RequiredArgsConstructor
public class InternalDocumentLectureController {

    private final DocumentLectureService documentLectureService;

    /**
     * Get download URL for a document lecture
     * For S3 documents: returns pre-signed URL
     * Used by learning-service to download document for processing
     */
    @GetMapping("/{id}/download-url")
    public DocumentDownloadUrlResponse getDownloadUrl(@PathVariable UUID id) {
        return documentLectureService.getDownloadUrl(id);
    }
}
