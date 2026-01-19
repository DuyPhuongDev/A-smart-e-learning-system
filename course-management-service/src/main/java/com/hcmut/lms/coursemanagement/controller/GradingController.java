package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.GradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.GradingResponse;
import com.hcmut.lms.coursemanagement.application.service.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/gradings")
@RequiredArgsConstructor
public class GradingController {
    
    private final GradingService gradingService;
    
    @PostMapping
    public ResponseEntity<GradingResponse> createGrading(
            @Valid @RequestBody GradingRequest request) {
        GradingResponse response = gradingService.createGrading(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GradingResponse> updateGrading(
            @PathVariable UUID id,
            @Valid @RequestBody GradingRequest request) {
        GradingResponse response = gradingService.updateGrading(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GradingResponse> getGradingById(@PathVariable UUID id) {
        GradingResponse response = gradingService.getGradingById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<GradingResponse>> getAllGradings() {
        List<GradingResponse> response = gradingService.getAllGradings();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrading(@PathVariable UUID id) {
        gradingService.deleteGrading(id);
        return ResponseEntity.noContent().build();
    }
}


