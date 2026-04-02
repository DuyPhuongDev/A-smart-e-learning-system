package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.response.InternalGraduationRequirementResponse;
import com.hcmut.lms.coursemanagement.application.service.GraduationRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/internal/graduation-requirements")
@RequiredArgsConstructor
public class InternalGraduationRequirementController {

    private final GraduationRequirementService graduationRequirementService;

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<InternalGraduationRequirementResponse>> getGraduationRequirementsByStudentId(
        @PathVariable UUID studentId) {
        return ResponseEntity.ok(graduationRequirementService.getActiveRequirementsByStudentId(studentId));
    }
}

