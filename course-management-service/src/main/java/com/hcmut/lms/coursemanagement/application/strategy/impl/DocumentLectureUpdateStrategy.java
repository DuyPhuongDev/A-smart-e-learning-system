package com.hcmut.lms.coursemanagement.application.strategy.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.DocumentLectureRequest;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategy;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.DocumentLecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Strategy implementation for updating DocumentLecture entities.
 */
@Component
@Slf4j
public class DocumentLectureUpdateStrategy implements LectureUpdateStrategy {
    
    @Override
    public void updateSpecificFields(Lecture lecture, BaseLectureRequest request) {
        if (!(lecture instanceof DocumentLecture)) {
            throw new IllegalArgumentException("Expected DocumentLecture but got: " + lecture.getClass().getSimpleName());
        }
        
        if (!(request instanceof DocumentLectureRequest)) {
            throw new IllegalArgumentException("Expected DocumentLectureRequest but got: " + request.getClass().getSimpleName());
        }
        
        DocumentLecture documentLecture = (DocumentLecture) lecture;
        DocumentLectureRequest documentRequest = (DocumentLectureRequest) request;
        
        log.debug("Updating DocumentLecture specific fields for lecture id: {}", lecture.getId());
        
        if (documentRequest.getFileUrl() != null) {
            documentLecture.setFileUrl(documentRequest.getFileUrl());
        }
    }
    
    @Override
    public LectureType getSupportedLectureType() {
        return LectureType.DOCUMENT;
    }
    
    @Override
    public void validate(BaseLectureRequest request) {
        if (!(request instanceof DocumentLectureRequest)) {
            return;
        }
        
        DocumentLectureRequest documentRequest = (DocumentLectureRequest) request;
    }
    
    @Override
    public void beforeDelete(Lecture lecture) {
        if (lecture instanceof DocumentLecture) {
            log.info("Performing pre-delete cleanup for DocumentLecture id: {}", lecture.getId());
            // Add specific cleanup logic for document lecture if needed
            // For example: delete document files from storage
            DocumentLecture documentLecture = (DocumentLecture) lecture;
            if (documentLecture.getFileUrl() != null) {
                log.debug("File URL to be cleaned up: {}", documentLecture.getFileUrl());
            }
        }
    }
    
    @Override
    public void afterDelete(UUID lectureId) {
        log.info("Performing post-delete operations for DocumentLecture id: {}", lectureId);
        // Add specific post-delete logic if needed
    }
}

