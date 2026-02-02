package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.TextLectureContentResponse;

import java.util.UUID;

/**
 * Service for text lecture operations
 * Provides text content retrieval for text-based lectures
 */
public interface TextLectureService {

    /**
     * Get content for a text lecture
     *
     * @param lectureId The text lecture ID
     * @return TextLectureContentResponse containing the text content and metadata
     */
    TextLectureContentResponse getContent(UUID lectureId);
}
