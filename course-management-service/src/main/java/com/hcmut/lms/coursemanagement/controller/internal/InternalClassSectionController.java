package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassDatasetLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionDatasetResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Internal controller providing class section metadata for service-to-service communication.
 * Not exposed through the API gateway.
 */
@RestController
@RequestMapping("/api/courses/internal/class-sections")
@RequiredArgsConstructor
public class InternalClassSectionController {

    private final ClassSectionService classSectionService;

    /**
     * Batch fetch class section metadata enriched with subject credits and semester key.
     * Used by learning-service to compute the grade prediction feature dataset.
     * POST /api/courses/internal/class-sections/dataset/batch
     */
    @PostMapping("/dataset/batch")
    public ResponseEntity<List<ClassSectionDatasetResponse>> getClassSectionsForDataset(
            @RequestBody @Valid BatchClassDatasetLookupRequest request) {
        return ResponseEntity.ok(classSectionService.getClassSectionsForDataset(request.getClassIds()));
    }
}
