package com.hcmut.lms.learning.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.EnrolledClassCardResponse;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;
import com.hcmut.lms.learning.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for Enrollment operations
 * Handles course enrollment, discovery, search, and ratings
 * Merged from enrollment-service
 */
@RestController
@RequestMapping("${prefix-api:}/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    /**
     * Enroll a student in a class
     */
    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(@RequestBody @Valid EnrollmentRequest enrollmentRequest) {
        return ResponseEntity.ok(enrollmentService.makeEnrollment(enrollmentRequest));
    }

    /**
     * Get enrolled classes for current user
     * For UI card display with filtering and searching
     * 
     * @param semesterCode Optional filter by semester code
     * @param searchTerm Optional search by class name or subject name
     * @param page Page number (default 0)
     * @param size Page size (default 10)
     */
    @GetMapping("/my-classes")
    public ResponseEntity<PageResponse<EnrolledClassCardResponse>> getMyEnrolledClasses(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestParam(required = false) String semesterCode,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(enrollmentService.getEnrolledClasses(
                currentUser.getId(), semesterCode, searchTerm, page, size));
    }

    @PutMapping("/change-class/{id}")
    public ResponseEntity<EnrollmentResponse> changeEnrolledClass(
            @PathVariable UUID id,
            @RequestBody EnrollmentRequest enrollmentRequest
    ){
        return ResponseEntity.ok(enrollmentService.changeClass(id, enrollmentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrolledClass(@PathVariable UUID id) {
        enrollmentService.unEnroll(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * Get enrolled classes for a specific student (admin/teacher use)
     */
    @GetMapping("/students/{studentId}/classes")
    public ResponseEntity<PageResponse<EnrolledClassCardResponse>> getStudentEnrolledClasses(
            @PathVariable UUID studentId,
            @RequestParam(required = false) String semesterCode,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(enrollmentService.getEnrolledClasses(
                studentId, semesterCode, searchTerm, page, size));
    }

    /**
     * Check if current user is enrolled in a class
     */
    @GetMapping("/check/{classId}")
    public ResponseEntity<Boolean> checkEnrollment(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID classId) {
        return ResponseEntity.ok(enrollmentService.isEnrolled(currentUser.getId(), classId));
    }

}
