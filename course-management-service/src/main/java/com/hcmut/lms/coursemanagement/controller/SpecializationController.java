package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SpecializationRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SpecializationResponse;
import com.hcmut.lms.coursemanagement.application.service.SpecializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
public class SpecializationController {
    
    private final SpecializationService specializationService;
    
    @PostMapping
    public ResponseEntity<SpecializationResponse> createSpecialization(
            @Valid @RequestBody SpecializationRequest request) {
        SpecializationResponse response = specializationService.createSpecialization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SpecializationResponse> updateSpecialization(
            @PathVariable UUID id,
            @Valid @RequestBody SpecializationRequest request) {
        SpecializationResponse response = specializationService.updateSpecialization(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SpecializationResponse> getSpecializationById(@PathVariable UUID id) {
        SpecializationResponse response = specializationService.getSpecializationById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<SpecializationResponse> getSpecializationByCode(@PathVariable String code) {
        SpecializationResponse response = specializationService.getSpecializationByCode(code);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllSpecializations(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<SpecializationResponse> response = specializationService.getAllSpecializations(page, size);
            return ResponseEntity.ok(response);
        }
        List<SpecializationResponse> response = specializationService.getAllSpecializations();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getSpecializationsByDepartmentId(
            @PathVariable UUID departmentId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<SpecializationResponse> response = specializationService.getSpecializationsByDepartmentId(departmentId, page, size);
            return ResponseEntity.ok(response);
        }
        List<SpecializationResponse> response = specializationService.getSpecializationsByDepartmentId(departmentId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable UUID id) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.noContent().build();
    }
}

