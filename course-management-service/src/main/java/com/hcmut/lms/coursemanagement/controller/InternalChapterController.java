package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.response.ChapterResponse;
import com.hcmut.lms.coursemanagement.application.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal controller for service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("${prefix-api}/internal/chapters")
@RequiredArgsConstructor
public class InternalChapterController {

    private final ChapterService chapterService;

    /**
     * Get chapter by ID
     * Used by learning-service to get chapter information
     */
    @GetMapping("/{id}")
    public ChapterResponse getChapterById(@PathVariable UUID id) {
        return chapterService.getChapterById(id);
    }
}
