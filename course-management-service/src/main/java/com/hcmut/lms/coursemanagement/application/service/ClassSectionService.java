package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionDatasetResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassStatusResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseMenuResponse;

import java.util.List;
import java.util.UUID;

public interface ClassSectionService {
    ClassSectionResponse createClassSection(CurrentUserInfo currentUser, ClassSectionRequest request);

    ClassSectionResponse updateClassSection(UUID id, ClassSectionRequest request);

    ClassSectionResponse getClassSectionById(UUID id);

    PageResponse<ClassSectionResponse> getAllClassSections(int page, int size, String semester, UUID teacherId);

    PageResponse<ClassSectionResponse> getClassSectionsByTeacherId(UUID teacherId, int page, int size, String semester);

    void deleteClassSection(UUID id);

    ClassSectionResponse assignTeacherToClassSection(UUID id, UUID teacherId);

    CourseMenuResponse getCourseMenu(UUID classSectionId);

    ClassStatusResponse getClassStatus(UUID id);

    void openClass(UUID id);

    /**
     * Get class section info by batch IDs with optional filters
     * Used internally by learning-service for enrolled classes
     */
    List<ClassSectionResponse> getClassSectionsByIds(BatchClassLookupRequest request);

    /**
     * Increment current students count when a student enrolls
     */
    void incrementCurrentStudents(UUID classId);

    /**
     * Decrement current students count when a student unenrolls
     */
    void decrementCurrentStudents(UUID classId);

    /**
     * Count number of mandatory lecture in class
     */
    Integer countNumberLecturesByClassId(UUID classId);

    /**
     * Get class sections by semester ID and subject ID
     */
    List<ClassSectionResponse> getClassSectionsBySemesterAndSubject(UUID semesterId, UUID subjectId);

    /**
     * Batch fetch class section metadata enriched with subject credits and semester key.
     * Used by learning-service for grade prediction dataset computation.
     */
    List<ClassSectionDatasetResponse> getClassSectionsForDataset(List<UUID> classIds);

    /**
     * Fetch class sections for a subject within a semKey window (exclusive upper bound targetSemKey).
     * Used by learning-service to compute subject-level baselines.
     */
    List<ClassSectionDatasetResponse> getClassSectionsBySubjectWindow(UUID subjectId, Integer targetSemKey, Integer windowSpan);
}
