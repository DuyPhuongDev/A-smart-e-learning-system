package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.StudyTimeResponse;
import com.hcmut.lms.learning.dto.response.StudyTimeSummaryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StudyTimeService {

    /**
     * Record study time for current user
     */
    StudyTimeResponse recordStudyTime(UUID studentId, StudyTimeRequest request);

    /**
     * Get study time history for current user in a class
     */
    List<StudyTimeResponse> getStudyTimeHistory(UUID studentId, UUID classId, LocalDate startDate, LocalDate endDate);

    /**
     * Get study time summary for current user in a class
     */
    List<StudyTimeSummaryResponse> getStudyTimeSummary(UUID studentId, UUID classId, LocalDate startDate, LocalDate endDate);

    /**
     * Get total study time in seconds for current user in a class
     */
    Integer getTotalStudyTime(UUID studentId, UUID classId);
}
