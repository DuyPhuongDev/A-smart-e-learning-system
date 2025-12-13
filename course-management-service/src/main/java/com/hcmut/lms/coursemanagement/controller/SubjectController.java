package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;
import com.hcmut.lms.coursemanagement.application.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {
    
    private final SubjectService subjectService;
    
    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable UUID id,
            @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable UUID id) {
        SubjectResponse response = subjectService.getSubjectById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<SubjectResponse> getSubjectByCode(@PathVariable String code) {
        SubjectResponse response = subjectService.getSubjectByCode(code);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllSubjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<SubjectResponse> response = subjectService.getAllSubjects(page, size);
            return ResponseEntity.ok(response);
        }
        List<SubjectResponse> response = subjectService.getAllSubjects();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable UUID id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}


