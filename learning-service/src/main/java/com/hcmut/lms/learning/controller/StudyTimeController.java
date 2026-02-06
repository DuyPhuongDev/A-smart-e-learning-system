package com.hcmut.lms.learning.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.StudyTimeResponse;
import com.hcmut.lms.learning.dto.response.StudyTimeSummaryResponse;
import com.hcmut.lms.learning.service.StudyTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller for Study Time operations
 * Handles recording and querying study time for students
 */
@RestController
@RequestMapping("${prefix-api:}/study-time")
@RequiredArgsConstructor
public class StudyTimeController {
    private final StudyTimeService studyTimeService;

    /**
     * Record study time for current user
     * 
     * @param request Study time request with classId, lectureId (optional), durationSeconds, startedAt
     */
    @PostMapping("/record")
    public ResponseEntity<StudyTimeResponse> recordStudyTime(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestBody @Valid StudyTimeRequest request) {
        return ResponseEntity.ok(studyTimeService.recordStudyTime(currentUser.getId(), request));
    }

    /**
     * Get study time history for current user in a class
     * 
     * @param classId Class ID
     * @param startDate Start date (optional, default: 30 days ago)
     * @param endDate End date (optional, default: today)
     */
    @GetMapping("/my-classes/{classId}/history")
    public ResponseEntity<List<StudyTimeResponse>> getMyStudyTimeHistory(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(studyTimeService.getStudyTimeHistory(
                currentUser.getId(), classId, startDate, endDate));
    }

    /**
     * Get study time history for a specific student in a class (admin/teacher use)
     */
    @GetMapping("/students/{studentId}/classes/{classId}/history")
    public ResponseEntity<List<StudyTimeResponse>> getStudentStudyTimeHistory(
            @PathVariable UUID studentId,
            @PathVariable UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(studyTimeService.getStudyTimeHistory(
                studentId, classId, startDate, endDate));
    }

    /**
     * Get study time summary (daily aggregated) for current user in a class
     * 
     * @param classId Class ID
     * @param startDate Start date (optional, default: 30 days ago)
     * @param endDate End date (optional, default: today)
     */
    @GetMapping("/my-classes/{classId}/summary")
    public ResponseEntity<List<StudyTimeSummaryResponse>> getMyStudyTimeSummary(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(studyTimeService.getStudyTimeSummary(
                currentUser.getId(), classId, startDate, endDate));
    }

    /**
     * Get study time summary for a specific student in a class (admin/teacher use)
     */
    @GetMapping("/students/{studentId}/classes/{classId}/summary")
    public ResponseEntity<List<StudyTimeSummaryResponse>> getStudentStudyTimeSummary(
            @PathVariable UUID studentId,
            @PathVariable UUID classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(studyTimeService.getStudyTimeSummary(
                studentId, classId, startDate, endDate));
    }

    /**
     * Get total study time in seconds for current user in a class
     */
    @GetMapping("/my-classes/{classId}/total")
    public ResponseEntity<Integer> getMyTotalStudyTime(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID classId) {
        return ResponseEntity.ok(studyTimeService.getTotalStudyTime(currentUser.getId(), classId));
    }

    /**
     * Get total study time in seconds for a specific student in a class
     */
    @GetMapping("/students/{studentId}/classes/{classId}/total")
    public ResponseEntity<Integer> getStudentTotalStudyTime(
            @PathVariable UUID studentId,
            @PathVariable UUID classId) {
        return ResponseEntity.ok(studyTimeService.getTotalStudyTime(studentId, classId));
    }
}
