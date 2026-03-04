package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.response.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.coursemanagement.application.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Internal controller providing subject metadata for service-to-service communication.
 * Not exposed through the API gateway.
 */
@RestController
@RequestMapping("/api/courses/internal/subjects")
@RequiredArgsConstructor
public class InternalSubjectController {

    private final SubjectService subjectService;

    /**
     * Get prerequisite + recommendation mapping for all subjects.
     * Used by learning-service for grade prediction dataset computation.
     * GET /api/courses/internal/subjects/prerequisite-mapping
     */
    @GetMapping("/prerequisite-mapping")
    public ResponseEntity<List<SubjectPrerequisiteMapResponse>> getPrerequisiteMapping() {
        return ResponseEntity.ok(subjectService.getPrerequisiteMapping());
    }
}

