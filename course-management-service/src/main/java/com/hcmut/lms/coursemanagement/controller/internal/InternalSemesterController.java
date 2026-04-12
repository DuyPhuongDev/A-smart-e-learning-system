package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;
import com.hcmut.lms.coursemanagement.application.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Internal controller providing semester metadata for service-to-service communication.
 * Not exposed through the API gateway.
 */
@RestController
@RequestMapping("/api/courses/internal/semesters")
@RequiredArgsConstructor
public class InternalSemesterController {

    private final SemesterService semesterService;

    /**
     * Get all semesters.
     * Used by learning-service to compute SubjectSemesterMetrics for all (subject, semester) pairs.
     * GET /api/courses/internal/semesters
     */
    @GetMapping
    public ResponseEntity<List<SemesterResponse>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.getAllSemesters());
    }

    /**
     * Get the currently active semester based on the current date.
     * Used by learning-service to resolve the target semester for grade prediction.
     * GET /api/courses/internal/semesters/current
     */
    @GetMapping("/current")
    public ResponseEntity<SemesterResponse> getCurrentSemester() {
        return ResponseEntity.ok(semesterService.getCurrentSemester());
    }
}
