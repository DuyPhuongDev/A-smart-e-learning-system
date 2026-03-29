package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumSectionResponse;
import com.hcmut.lms.coursemanagement.application.service.CurriculumSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/curriculum-sections")
@RequiredArgsConstructor
public class CurriculumSectionController {
    
    private final CurriculumSectionService curriculumSectionService;
    
    @PostMapping
    public ResponseEntity<CurriculumSectionResponse> createCurriculumSection(
            @Valid @RequestBody CurriculumSectionRequest request) {
        CurriculumSectionResponse response = curriculumSectionService.createCurriculumSection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CurriculumSectionResponse> updateCurriculumSection(
            @PathVariable UUID id,
            @Valid @RequestBody CurriculumSectionRequest request) {
        CurriculumSectionResponse response = curriculumSectionService.updateCurriculumSection(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CurriculumSectionResponse> getCurriculumSectionById(@PathVariable UUID id) {
        CurriculumSectionResponse response = curriculumSectionService.getCurriculumSectionById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<CurriculumSectionResponse>> getAllCurriculumSections() {
        List<CurriculumSectionResponse> response = curriculumSectionService.getAllCurriculumSections();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/curriculum/{code}/{specializationId}/{intakeYearId}")
    public ResponseEntity<List<CurriculumSectionResponse>> getCurriculumSectionsByCurriculumId(
            @PathVariable String code,
            @PathVariable UUID specializationId,
            @PathVariable UUID intakeYearId) {
        List<CurriculumSectionResponse> response = curriculumSectionService.getCurriculumSectionsByCurriculumId(code, specializationId, intakeYearId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurriculumSection(@PathVariable UUID id) {
        curriculumSectionService.deleteCurriculumSection(id);
        return ResponseEntity.noContent().build();
    }
}

