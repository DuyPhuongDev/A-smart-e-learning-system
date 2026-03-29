package com.hcmut.lms.coursemanagement.application.strategy;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;

/**
 * Strategy interface for updating lecture-specific fields.
 * This allows different update behaviors for different lecture types.
 * Extends LectureStrategy to inherit common behaviors.
 */
public interface LectureUpdateStrategy extends LectureStrategy {
    
    /**
     * Updates the specific fields of a lecture based on its type.
     * 
     * @param lecture the lecture entity to update
     * @param request the request DTO containing update data
     */
    void updateSpecificFields(Lecture lecture, BaseLectureRequest request);
}

