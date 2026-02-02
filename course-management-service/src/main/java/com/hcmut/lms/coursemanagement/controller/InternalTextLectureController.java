package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.response.TextLectureContentResponse;
import com.hcmut.lms.coursemanagement.application.service.TextLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal controller for text lecture service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("${prefix-api}/internal/text-lectures")
@RequiredArgsConstructor
public class InternalTextLectureController {

    private final TextLectureService textLectureService;

    /**
     * Get content for a text lecture
     * Used by learning-service to get text content for processing
     */
    @GetMapping("/{id}/content")
    public TextLectureContentResponse getContent(@PathVariable UUID id) {
        return textLectureService.getContent(id);
    }
}
