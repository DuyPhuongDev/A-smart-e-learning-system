package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.ClassGradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.UpdateGradingWeightRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassGradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/class-sections/{classId}/gradings")
@RequiredArgsConstructor
public class ClassGradingController {

    private final ClassGradingService classGradingService;

    @GetMapping
    public ResponseEntity<List<ClassGradingResponse>> getGradingsForClass(@PathVariable UUID classId) {
        return ResponseEntity.ok(classGradingService.getGradingsForClass(classId));
    }

    @PostMapping
    public ResponseEntity<ClassGradingResponse> addGradingToClass(
            @PathVariable UUID classId,
            @Valid @RequestBody ClassGradingRequest request) {
        ClassGradingResponse response = classGradingService.addGradingToClass(classId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{gradingId}")
    public ResponseEntity<ClassGradingResponse> updateGradingWeight(
            @PathVariable UUID classId,
            @PathVariable UUID gradingId,
            @Valid @RequestBody UpdateGradingWeightRequest request) {
        return ResponseEntity.ok(classGradingService.updateGradingWeight(classId, gradingId, request));
    }

    @DeleteMapping("/{gradingId}")
    public ResponseEntity<Void> removeGradingFromClass(
            @PathVariable UUID classId,
            @PathVariable UUID gradingId) {
        classGradingService.removeGradingFromClass(classId, gradingId);
        return ResponseEntity.noContent().build();
    }
}
