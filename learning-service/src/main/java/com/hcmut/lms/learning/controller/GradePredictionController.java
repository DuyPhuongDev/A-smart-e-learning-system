package com.hcmut.lms.learning.controller;

import com.hcmut.lms.learning.dto.request.RawFeaturePredictionRequest;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.service.GradePredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api:}/prediction")
@RequiredArgsConstructor
@Slf4j
public class GradePredictionController {

    private final GradePredictionService gradePredictionService;

    @GetMapping("/grade")
    public ResponseEntity<GradePredictionResponse> predictGrade(
            @RequestParam UUID studentId,
            @RequestParam UUID subjectId,
            @RequestParam(required = false) Integer plannedSemesterCredits,
            @RequestParam(required = false) Double threshold) {
        log.info("Grade prediction request: studentId={}, subjectId={}", studentId, subjectId);
        return ResponseEntity.ok(gradePredictionService.predictGrade(
                studentId, subjectId, plannedSemesterCredits, threshold));
    }

    @PostMapping("/grade")
    public ResponseEntity<GradePredictionResponse> predictGradeFromRawFeatures(
            @Valid @RequestBody RawFeaturePredictionRequest request) {
        log.info("Raw feature grade prediction request received");
        return ResponseEntity.ok(gradePredictionService.predictGradeFromRawFeatures(request));
    }

    @GetMapping("/my-grade/{subjectId}")
    public ResponseEntity<GradePredictionResponse> predictMyGrade(
            @PathVariable UUID subjectId,
            @RequestParam(required = false) Integer plannedSemesterCredits,
            @RequestParam(required = false) Double threshold) {
        // TODO: Get current user from security context
        log.info("My grade prediction request: subjectId={}", subjectId);
        return ResponseEntity.status(501).build();
    }
}
