package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.IntakeYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.IntakeYearResponse;
import com.hcmut.lms.coursemanagement.application.service.IntakeYearService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/intake-years")
@RequiredArgsConstructor
public class IntakeYearController {
    
    private final IntakeYearService intakeYearService;
    
    @PostMapping
    public ResponseEntity<IntakeYearResponse> createIntakeYear(
            @Valid @RequestBody IntakeYearRequest request) {
        IntakeYearResponse response = intakeYearService.createIntakeYear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<IntakeYearResponse> updateIntakeYear(
            @PathVariable UUID id,
            @Valid @RequestBody IntakeYearRequest request) {
        IntakeYearResponse response = intakeYearService.updateIntakeYear(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<IntakeYearResponse> getIntakeYearById(@PathVariable UUID id) {
        IntakeYearResponse response = intakeYearService.getIntakeYearById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/start-year/{startYear}")
    public ResponseEntity<IntakeYearResponse> getIntakeYearByStartYear(@PathVariable Integer startYear) {
        IntakeYearResponse response = intakeYearService.getIntakeYearByStartYear(startYear);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllIntakeYears(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<IntakeYearResponse> response = intakeYearService.getAllIntakeYears(page, size);
            return ResponseEntity.ok(response);
        }
        List<IntakeYearResponse> response = intakeYearService.getAllIntakeYears();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntakeYear(@PathVariable UUID id) {
        intakeYearService.deleteIntakeYear(id);
        return ResponseEntity.noContent().build();
    }
}

