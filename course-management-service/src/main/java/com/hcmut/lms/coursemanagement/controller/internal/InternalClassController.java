package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionReportMetadataResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassStatusResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/internal/class-sections")
@RequiredArgsConstructor
public class InternalClassController {
    private final ClassSectionService classSectionService;

    @GetMapping("/{id}/enroll-status")
    public ResponseEntity<ClassStatusResponse> getEnrollmentStatus(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(classSectionService.getClassStatus(id));
    }

    /**
     * Get class sections info by batch IDs with optional filters
     * Used by learning-service for enrolled classes display
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ClassSectionResponse>> getClassSectionsByIds(
            @RequestBody @Valid BatchClassLookupRequest request) {
        return ResponseEntity.ok(classSectionService.getClassSectionsByIds(request));
    }

    /**
     * Increment current students count when a student enrolls
     */
    @PostMapping("/{id}/increment-students")
    public ResponseEntity<Void> incrementCurrentStudents(@PathVariable("id") UUID id) {
        classSectionService.incrementCurrentStudents(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Decrement current students count when a student unenrolls
     */
    @PostMapping("/{id}/decrement-students")
    public ResponseEntity<Void> decrementCurrentStudents(@PathVariable("id") UUID id) {
        classSectionService.decrementCurrentStudents(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/count-lecture")
    public ResponseEntity<Integer> countNumberLecturesByClassId(@PathVariable UUID id){
        return ResponseEntity.ok(classSectionService.countNumberLecturesByClassId(id));
    }

    @GetMapping("/{id}/report-metadata")
    public ResponseEntity<ClassSectionReportMetadataResponse> getClassSectionReportMetadata(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(classSectionService.getClassSectionReportMetadata(id));
    }
}
