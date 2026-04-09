package com.hcmut.lms.learning.client.fallback;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.BatchClassDatasetLookupRequest;
import com.hcmut.lms.learning.client.dto.BatchClassLookupRequest;
import com.hcmut.lms.learning.client.dto.ClassEnrollStatus;
import com.hcmut.lms.learning.client.dto.ClassResponse;
import com.hcmut.lms.learning.client.dto.ClassSectionReportMetadataResponse;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.client.dto.SemesterResponse;
import com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse;
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

    @Override
    public Integer countNumberLecturesByClassId(UUID classId) {
        log.error("CourseManagement service unavailable. Unable to get number of lectures by class ID");
        throw new ServiceUnavailableException("Course management service is unavailable");
    }

    @Override
    public LectureResponse getLectureById(UUID id) {
        log.error("CourseManagement service unavailable. Unable to get lecture by ID");
        throw new ServiceUnavailableException("Course management service is unavailable");
    }

    @Override
    public ClassSectionReportMetadataResponse getClassSectionReportMetadata(UUID id) {
        log.error("CourseManagement service unavailable. Unable to get class section report metadata");
        throw new ServiceUnavailableException("Course management service is unavailable");
    }

    @Override
    public List<ClassSectionDatasetResponse> getClassSectionsForDataset(BatchClassDatasetLookupRequest request) {
        log.error("CourseManagement service unavailable. Unable to fetch class sections for dataset computation.");
        throw new ServiceUnavailableException("Course management service is unavailable");
    }

    @Override
    public List<ClassSectionDatasetResponse> getClassSectionsBySubjectWindow(com.hcmut.lms.learning.client.dto.SubjectWindowDatasetLookupRequest request) {
        log.error("CourseManagement service unavailable. Unable to fetch class sections for subject window.");
        throw new ServiceUnavailableException("Course management service is unavailable");
    }

    @Override
    public List<SubjectPrerequisiteMapResponse> getPrerequisiteMapping() {
        log.warn("CourseManagement service unavailable. Returning empty prerequisite mapping.");
        return List.of();
    }

    @Override
    public SemesterResponse getCurrentSemester() {
        log.error("CourseManagement service unavailable. Unable to get current semester.");
        throw new ServiceUnavailableException("Course management service is unavailable");
    }
}
