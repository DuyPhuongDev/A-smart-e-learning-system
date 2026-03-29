package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumResponse;
import com.hcmut.lms.coursemanagement.application.service.CurriculumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/curriculums")
@RequiredArgsConstructor
public class CurriculumController {
    
    private final CurriculumService curriculumService;
    
    @PostMapping
    public ResponseEntity<CurriculumResponse> createCurriculum(
            @Valid @RequestBody CurriculumRequest request) {
        CurriculumResponse response = curriculumService.createCurriculum(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping
    public ResponseEntity<CurriculumResponse> updateCurriculum(
            @RequestParam String code,
            @RequestParam UUID specializationId,
            @RequestParam UUID intakeYearId,
            @Valid @RequestBody CurriculumRequest request) {
        CurriculumResponse response = curriculumService.updateCurriculum(code, specializationId, intakeYearId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/getById")
    public ResponseEntity<CurriculumResponse> getCurriculumById(
            @RequestParam String code,
            @RequestParam UUID specializationId,
            @RequestParam UUID intakeYearId) {
        CurriculumResponse response = curriculumService.getCurriculumById(code, specializationId, intakeYearId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllCurriculums(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<CurriculumResponse> response = curriculumService.getAllCurriculums(page, size);
            return ResponseEntity.ok(response);
        }
        List<CurriculumResponse> response = curriculumService.getAllCurriculums();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<?> getCurriculumsBySpecializationId(
            @PathVariable UUID specializationId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<CurriculumResponse> response = curriculumService.getCurriculumsBySpecializationId(specializationId, page, size);
            return ResponseEntity.ok(response);
        }
        List<CurriculumResponse> response = curriculumService.getCurriculumsBySpecializationId(specializationId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/intake-year/{intakeYearId}")
    public ResponseEntity<?> getCurriculumsByIntakeYearId(
            @PathVariable UUID intakeYearId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<CurriculumResponse> response = curriculumService.getCurriculumsByIntakeYearId(intakeYearId, page, size);
            return ResponseEntity.ok(response);
        }
        List<CurriculumResponse> response = curriculumService.getCurriculumsByIntakeYearId(intakeYearId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteCurriculum(
            @RequestParam String code,
            @RequestParam UUID specializationId,
            @RequestParam UUID intakeYearId) {
        curriculumService.deleteCurriculum(code, specializationId, intakeYearId);
        return ResponseEntity.noContent().build();
    }
}

