package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SemesterRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;
import com.hcmut.lms.coursemanagement.application.service.SemesterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/semesters")
@RequiredArgsConstructor
public class SemesterController {
    
    private final SemesterService semesterService;
    
    @PostMapping
    public ResponseEntity<SemesterResponse> createSemester(
            @Valid @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.createSemester(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SemesterResponse> updateSemester(
            @PathVariable UUID id,
            @Valid @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.updateSemester(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SemesterResponse> getSemesterById(@PathVariable UUID id) {
        SemesterResponse response = semesterService.getSemesterById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllSemesters(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<SemesterResponse> response = semesterService.getAllSemesters(page, size);
            return ResponseEntity.ok(response);
        }
        List<SemesterResponse> response = semesterService.getAllSemesters();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/academic-year/{academicYearId}")
    public ResponseEntity<?> getSemestersByAcademicYearId(
            @PathVariable UUID academicYearId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<SemesterResponse> response = semesterService.getSemestersByAcademicYearId(academicYearId, page, size);
            return ResponseEntity.ok(response);
        }
        List<SemesterResponse> response = semesterService.getSemestersByAcademicYearId(academicYearId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable UUID id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.noContent().build();
    }
}


