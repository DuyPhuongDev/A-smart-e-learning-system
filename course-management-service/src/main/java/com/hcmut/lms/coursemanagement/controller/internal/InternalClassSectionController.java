package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassDatasetLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectWindowDatasetLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionDatasetResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/dataset/batch")
    public ResponseEntity<List<ClassSectionDatasetResponse>> getClassSectionsForDataset(
            @RequestBody @Valid BatchClassDatasetLookupRequest request) {
        return ResponseEntity.ok(classSectionService.getClassSectionsForDataset(request.getClassIds()));
    }

    @PostMapping("/dataset/by-subject-window")
    public ResponseEntity<List<ClassSectionDatasetResponse>> getClassSectionsBySubjectWindow(
            @RequestBody @Valid SubjectWindowDatasetLookupRequest request) {
        return ResponseEntity.ok(classSectionService.getClassSectionsBySubjectWindow(
                request.getSubjectId(), request.getTargetSemKey(), request.getWindowSpan()));
    }
}
