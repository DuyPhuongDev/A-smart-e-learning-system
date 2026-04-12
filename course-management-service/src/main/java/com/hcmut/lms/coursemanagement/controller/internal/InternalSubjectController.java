package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.response.SubjectLearningOutcomeResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;
import com.hcmut.lms.coursemanagement.application.service.SubjectLearningOutcomeService;
import com.hcmut.lms.coursemanagement.application.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Internal controller providing subject metadata for service-to-service communication.
 * Not exposed through the API gateway.
 */
@RestController
@RequestMapping("/api/courses/internal/subjects")
@RequiredArgsConstructor
public class InternalSubjectController {

    private final SubjectService subjectService;
    private final SubjectLearningOutcomeService subjectLearningOutcomeService;

    /**
     * Get prerequisite + recommendation mapping for all subjects.
     * Used by learning-service for grade prediction dataset computation.
     * GET /api/courses/internal/subjects/prerequisite-mapping
     */
    @GetMapping("/prerequisite-mapping")
    public ResponseEntity<List<SubjectPrerequisiteMapResponse>> getPrerequisiteMapping() {
        return ResponseEntity.ok(subjectService.getPrerequisiteMapping());
    }

    @GetMapping("/{subjectId}/learning-outcomes")
    public ResponseEntity<List<SubjectLearningOutcomeResponse>> getLearningOutcomes(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(subjectLearningOutcomeService.getBySubjectId(subjectId));
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(subjectService.getSubjectById(subjectId));
    }

    /**
     * Get all subject IDs in the database.
     * Used by learning-service to pre-compute SubjectSemesterMetrics for all subjects.
     * GET /api/courses/internal/subjects/ids
     */
    @GetMapping("/ids")
    public ResponseEntity<List<UUID>> getAllSubjectIds() {
        return ResponseEntity.ok(subjectService.getAllSubjectIds());
    }
}

