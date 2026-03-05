package com.hcmut.lms.learning.client;

import com.hcmut.lms.learning.client.dto.BatchClassDatasetLookupRequest;
import com.hcmut.lms.learning.client.dto.BatchClassLookupRequest;
import com.hcmut.lms.learning.client.dto.ClassEnrollStatus;
import com.hcmut.lms.learning.client.dto.ClassResponse;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.learning.client.fallback.CourseManagementFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "course-management-service",
        path = "/api/courses/internal",
        fallback = CourseManagementFallback.class
)
public interface CourseManagementClient {
    @GetMapping("/class-sections/{id}/enroll-status")
    ClassEnrollStatus getEnrollmentStatus(@PathVariable("id") UUID id);

    /**
     * Get class sections info by batch IDs with optional filters
     */
    @PostMapping("/class-sections/batch")
    List<ClassResponse> getClassSectionsByIds(@RequestBody BatchClassLookupRequest request);

    /**
     * Increment current students count when a student enrolls
     */
    @PostMapping("/class-sections/{id}/increment-students")
    void incrementCurrentStudents(@PathVariable("id") UUID id);

    /**
     * Decrement current students count when a student unenrolls
     */
    @PostMapping("/class-sections/{id}/decrement-students")
    void decrementCurrentStudents(@PathVariable("id") UUID id);

    @GetMapping("/class-sections/{id}/count-lecture")
    Integer countNumberLecturesByClassId(@PathVariable UUID id);

    @GetMapping("/lectures/{id}")
    LectureResponse getLectureById(@PathVariable UUID id);

    /**
     * Batch fetch class section metadata with credits and semester key for dataset computation.
     * Calls: POST /api/courses/internal/class-sections/dataset/batch
     */
    @PostMapping("/class-sections/dataset/batch")
    List<ClassSectionDatasetResponse> getClassSectionsForDataset(
            @RequestBody BatchClassDatasetLookupRequest request);

    /**
     * Get prerequisite + recommendation mapping for all subjects.
     * Calls: GET /api/courses/internal/subjects/prerequisite-mapping
     */
    @GetMapping("/subjects/prerequisite-mapping")
    List<SubjectPrerequisiteMapResponse> getPrerequisiteMapping();
}
