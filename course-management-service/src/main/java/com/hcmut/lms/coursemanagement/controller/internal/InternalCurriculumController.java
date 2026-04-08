package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.request.PrerequisiteChainRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumFullResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.PrerequisiteChainResponse;
import com.hcmut.lms.coursemanagement.application.service.CurriculumService;
import com.hcmut.lms.coursemanagement.application.service.PrerequisiteChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Internal controller for prerequisite chain calculations
 * Used by personalization-service for learning goal validation
 */
@RestController
@RequestMapping("/api/courses/internal/curriculums")
@RequiredArgsConstructor
public class InternalCurriculumController {

    private final PrerequisiteChainService prerequisiteChainService;
    private final CurriculumService curriculumService;

    /**
     * Calculate the longest prerequisite chain for remaining subjects
     * POST /api/courses/internal/curriculums/prerequisite-chains
     */
    @PostMapping("/prerequisite-chains")
    public ResponseEntity<PrerequisiteChainResponse> calculatePrerequisiteChain(
            @RequestBody PrerequisiteChainRequest request) {
        return ResponseEntity.ok(prerequisiteChainService.calculatePrerequisiteChain(request));
    }

    /**
     * Get full curriculum data with sections, subjects, prerequisites, recommendations, and priorities
     * GET /api/courses/internal/curriculums/{curriculumCode}/full
     */
    @GetMapping("/{curriculumCode}/full")
    public ResponseEntity<CurriculumFullResponse> getCurriculumFull(
            @PathVariable String curriculumCode) {
        return ResponseEntity.ok(curriculumService.getCurriculumFull(curriculumCode));
    }

    @GetMapping("/resolve")
    public ResponseEntity<CurriculumResponse> resolveCurriculum(
            @RequestParam UUID specializationId,
            @RequestParam Integer intakeYear) {
        return ResponseEntity.ok(
            curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specializationId, intakeYear));
    }
}
