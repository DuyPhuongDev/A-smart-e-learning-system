package com.hcmut.lms.learning.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.EnrolledClassCardResponse;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;

import java.util.UUID;

public interface EnrollmentService {

    /**
     * Enroll a student in a class
     */
    EnrollmentResponse makeEnrollment(EnrollmentRequest request);

    /**
     * Get enrolled classes for a student with optional filters
     * @param studentId Student ID
     * @param semesterCode Optional semester filter
     * @param searchTerm Optional search term (by class name or subject name)
     * @param page Page number
     * @param size Page size
     * @return Paginated list of enrolled class cards
     */
    PageResponse<EnrolledClassCardResponse> getEnrolledClasses(UUID studentId, String semesterCode, 
                                                               String searchTerm, int page, int size);

    /**
     * Check if student is already enrolled in a class
     */
    boolean isEnrolled(UUID studentId, UUID classId);
}
