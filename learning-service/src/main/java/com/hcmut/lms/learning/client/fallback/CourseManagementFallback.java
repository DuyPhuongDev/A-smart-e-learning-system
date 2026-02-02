package com.hcmut.lms.learning.client.fallback;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.BatchClassLookupRequest;
import com.hcmut.lms.learning.client.dto.ClassEnrollStatus;
import com.hcmut.lms.learning.client.dto.ClassResponse;
import com.hcmut.lms.learning.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class CourseManagementFallback implements CourseManagementClient {
    @Override
    public ClassEnrollStatus getEnrollmentStatus(UUID id) {
        log.error("CourseManagement service unavailable. classId={}", id);
        return ClassEnrollStatus.unavailable(id);
    }

    @Override
    public List<ClassResponse> getClassSectionsByIds(BatchClassLookupRequest request) {
        log.error("CourseManagement service unavailable. Unable to get class sections by IDs");
        throw new ServiceUnavailableException("Course management service is unavailable");
    }

    @Override
    public void incrementCurrentStudents(UUID id) {
        log.error("CourseManagement service unavailable. Unable to increment students for classId={}", id);
        throw new ServiceUnavailableException("Course management service is unavailable");
    }

    @Override
    public void decrementCurrentStudents(UUID id) {
        log.error("CourseManagement service unavailable. Unable to decrement students for classId={}", id);
        throw new ServiceUnavailableException("Course management service is unavailable");
    }
}
