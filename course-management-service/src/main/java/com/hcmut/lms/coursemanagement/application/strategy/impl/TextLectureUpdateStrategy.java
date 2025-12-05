package com.hcmut.lms.coursemanagement.application.strategy.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.TextLectureRequest;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategy;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.TextLecture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Strategy implementation for updating TextLecture entities.
 */
@Component
@Slf4j
public class TextLectureUpdateStrategy implements LectureUpdateStrategy {
    
    @Override
    public void updateSpecificFields(Lecture lecture, BaseLectureRequest request) {
        if (!(lecture instanceof TextLecture)) {
            throw new IllegalArgumentException("Expected TextLecture but got: " + lecture.getClass().getSimpleName());
        }
        
        if (!(request instanceof TextLectureRequest)) {
            throw new IllegalArgumentException("Expected TextLectureRequest but got: " + request.getClass().getSimpleName());
        }
        
        TextLecture textLecture = (TextLecture) lecture;
        TextLectureRequest textRequest = (TextLectureRequest) request;
        
        log.debug("Updating TextLecture specific fields for lecture id: {}", lecture.getId());
        
        if (textRequest.getContent() != null) {
            textLecture.setContent(textRequest.getContent());
            // Auto-calculate word count when content is updated
            textLecture.setWordCount(textRequest.getContent().split("\\s+").length);
        }
        
        if (textRequest.getFormatType() != null) {
            textLecture.setFormatType(textRequest.getFormatType());
        }
    }
    
    @Override
    public LectureType getSupportedLectureType() {
        return LectureType.TEXT;
    }
    
    @Override
    public void validate(BaseLectureRequest request) {
        if (!(request instanceof TextLectureRequest)) {
            return;
        }
        
        TextLectureRequest textRequest = (TextLectureRequest) request;
        
        // Add specific validation for text lecture if needed
        if (textRequest.getContent() != null && textRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
    
    @Override
    public void beforeDelete(Lecture lecture) {
        if (lecture instanceof TextLecture) {
            log.info("Performing pre-delete cleanup for TextLecture id: {}", lecture.getId());
            // Add specific cleanup logic for text lecture if needed
        }
    }
    
    @Override
    public void afterDelete(UUID lectureId) {
        log.info("Performing post-delete operations for TextLecture id: {}", lectureId);
        // Add specific post-delete logic if needed
    }
}

