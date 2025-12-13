package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.AcademicYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.AcademicYearResponse;
import com.hcmut.lms.coursemanagement.application.service.AcademicYearService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {
    
    private final AcademicYearService academicYearService;
    
    @PostMapping
    public ResponseEntity<AcademicYearResponse> createAcademicYear(
            @Valid @RequestBody AcademicYearRequest request) {
        AcademicYearResponse response = academicYearService.createAcademicYear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AcademicYearResponse> updateAcademicYear(
            @PathVariable UUID id,
            @Valid @RequestBody AcademicYearRequest request) {
        AcademicYearResponse response = academicYearService.updateAcademicYear(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AcademicYearResponse> getAcademicYearById(@PathVariable UUID id) {
        AcademicYearResponse response = academicYearService.getAcademicYearById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllAcademicYears(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<AcademicYearResponse> response = academicYearService.getAllAcademicYears(page, size);
            return ResponseEntity.ok(response);
        }
        List<AcademicYearResponse> response = academicYearService.getAllAcademicYears();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAcademicYear(@PathVariable UUID id) {
        academicYearService.deleteAcademicYear(id);
        return ResponseEntity.noContent().build();
    }
}


