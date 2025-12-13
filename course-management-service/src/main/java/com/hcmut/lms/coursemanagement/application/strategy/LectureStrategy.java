package com.hcmut.lms.coursemanagement.application.strategy;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;

import java.util.UUID;

/**
 * Base strategy interface for lecture operations.
 * This interface can be extended for different behaviors (update, delete, validate, etc.)
 */
public interface LectureStrategy {
    
    /**
     * Returns the lecture type this strategy handles.
     * 
     * @return the lecture type
     */
    LectureType getSupportedLectureType();
    
    /**
     * Validates the request data for this lecture type.
     * 
     * @param request the request to validate
     * @throws IllegalArgumentException if validation fails
     */
    default void validate(BaseLectureRequest request) {
        // Default implementation does nothing
        // Subclasses can override to add specific validation
    }
    
    /**
     * Performs pre-delete operations specific to this lecture type.
     * This can be used to clean up related resources before deletion.
     * 
     * @param lecture the lecture to be deleted
     */
    default void beforeDelete(Lecture lecture) {
        // Default implementation does nothing
        // Subclasses can override to add specific cleanup logic
    }
    
    /**
     * Performs post-delete operations specific to this lecture type.
     * 
     * @param lectureId the ID of the deleted lecture
     */
    default void afterDelete(UUID lectureId) {
        // Default implementation does nothing
        // Subclasses can override to add specific post-delete logic
    }
}

