package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.FacultyRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.FacultyResponse;
import com.hcmut.lms.coursemanagement.application.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/faculties")
@RequiredArgsConstructor
public class FacultyController {
    
    private final FacultyService facultyService;
    
    @PostMapping
    public ResponseEntity<FacultyResponse> createFaculty(
            @Valid @RequestBody FacultyRequest request) {
        FacultyResponse response = facultyService.createFaculty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FacultyResponse> updateFaculty(
            @PathVariable UUID id,
            @Valid @RequestBody FacultyRequest request) {
        FacultyResponse response = facultyService.updateFaculty(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponse> getFacultyById(@PathVariable UUID id) {
        FacultyResponse response = facultyService.getFacultyById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<FacultyResponse> getFacultyByCode(@PathVariable String code) {
        FacultyResponse response = facultyService.getFacultyByCode(code);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllFaculties(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<FacultyResponse> response = facultyService.getAllFaculties(page, size);
            return ResponseEntity.ok(response);
        }
        List<FacultyResponse> response = facultyService.getAllFaculties();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}

