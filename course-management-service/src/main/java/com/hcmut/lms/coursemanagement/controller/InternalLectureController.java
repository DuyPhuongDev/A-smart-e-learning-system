package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.application.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Internal controller for service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("${prefix-api}/internal/lectures")
@RequiredArgsConstructor
public class InternalLectureController {

    private final LectureService lectureService;

    /**
     * Get lecture by ID
     * Used by learning-service to get lecture information
     */
    @GetMapping("/{id}")
    public LectureResponse getLectureById(@PathVariable UUID id) {
        return lectureService.getLectureById(id);
    }

    /**
     * Get all lectures by chapter ID
     * Used by learning-service to get all lectures in a chapter
     */
    @GetMapping("/chapter/{chapterId}")
    public List<LectureResponse> getLecturesByChapterId(@PathVariable UUID chapterId) {
        return lectureService.getLecturesByChapterId(chapterId);
    }
}
