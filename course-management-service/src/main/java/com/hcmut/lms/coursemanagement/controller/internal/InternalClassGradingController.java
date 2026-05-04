package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingWeightResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassGradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/internal/class-sections")
@RequiredArgsConstructor
public class InternalClassGradingController {

    private final ClassGradingService classGradingService;

    @GetMapping("/{classId}/gradings")
    public ResponseEntity<List<ClassGradingResponse>> getGradings(@PathVariable UUID classId) {
        return ResponseEntity.ok(classGradingService.getGradingsForClass(classId));
    }

    @GetMapping("/{classId}/grading-weights")
    public ResponseEntity<List<ClassGradingWeightResponse>> getGradingWeights(@PathVariable UUID classId) {
        return ResponseEntity.ok(classGradingService.getGradingWeightsForClass(classId));
    }
}
