package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.dto.request.LearningProgressRequest;
import com.hcmut.lms.learning.dto.response.ClassProgressResponse;
import com.hcmut.lms.learning.dto.response.LearningProgressResponse;
import com.hcmut.lms.learning.entity.progress.LearningProgress;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LearningProgressService {

    /**
     * Update or create learning progress for current user
     */
    LearningProgressResponse trackingProgress(UUID studentId, LearningProgressRequest request);
    /**
     * Get learning progress for a specific student in a lecture
     */
    LearningProgressResponse getProgressForStudent(UUID studentId, UUID lectureId);

    /**
     * Get all learning progress for a specific student in a class
     */
    List<LearningProgressResponse> getProgressesByClassForStudent(UUID studentId, UUID classId);

    /**
     * Get class-level progress summary for a specific student
     */
    ClassProgressResponse getClassProgressSummaryForStudent(UUID studentId, UUID classId);

    BigDecimal calcProgress(LearningProgress progress);
}
