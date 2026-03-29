package com.hcmut.lms.coursemanagement.controller.internal;

import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.application.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses/internal/lectures")
@RequiredArgsConstructor
public class InternalLectureController {
    private final LectureService lectureService;

    @GetMapping("/{id}")
    public ResponseEntity<LectureResponse> getLectureById(@PathVariable UUID id) {
        return ResponseEntity.ok(lectureService.getLectureById(id));
    }
}
