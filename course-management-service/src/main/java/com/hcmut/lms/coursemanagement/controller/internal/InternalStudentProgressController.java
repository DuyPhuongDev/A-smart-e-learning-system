package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentLearningProgressResponse;
import com.hcmut.lms.coursemanagement.application.service.SemesterService;
import com.hcmut.lms.coursemanagement.application.service.StudentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Internal controller for student progress operations
 * Used by other microservices (e.g., personalization-service) to fetch student progress data
 */
@RestController
@RequestMapping("/api/courses/internal")
@RequiredArgsConstructor
public class InternalStudentProgressController {

    private final StudentProgressService studentProgressService;
    private final SemesterService semesterService;

    /**
     * Get student learning progress with GPA, credits, and completed subjects
     * Used by personalization-service for learning goal validation and learning path generation.
     * If specializationId is not provided, falls back to the student's default specialization.
     */
    @GetMapping("/student-progress/internal/{studentId}")
    public ResponseEntity<StudentLearningProgressResponse> getStudentProgress(
            @PathVariable UUID studentId,
            @RequestParam(value = "specializationId", required = false) UUID specializationId) {
        return ResponseEntity.ok(studentProgressService.getStudentLearningProgress(studentId, specializationId));
    }

    /**
     * Get remaining semesters for a student based on intake year
     * Used by personalization-service for semester calculation
     */
    @GetMapping("/semesters/remaining/{studentId}")
    public ResponseEntity<List<SemesterResponse>> getRemainingSemesters(
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(semesterService.getRemainSemester(studentId));
    }
}
