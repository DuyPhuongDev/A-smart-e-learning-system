package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;

import java.util.List;
import java.util.UUID;

public interface SubjectService {
    SubjectResponse createSubject(SubjectRequest request);
    SubjectResponse updateSubject(UUID id, SubjectRequest request);
    SubjectResponse getSubjectById(UUID id);
    SubjectResponse getSubjectByCode(String code);
    List<SubjectResponse> getAllSubjects();
    PageResponse<SubjectResponse> getAllSubjects(int page, int size);
    void deleteSubject(UUID id);

    /**
     * Get prerequisite + recommendation mapping for all subjects.
     * Returns subjectId → list of related subjectIds (PREREQUISITE + RECOMMENDED).
     * Used by learning-service for grade prediction dataset computation.
     */
    List<SubjectPrerequisiteMapResponse> getPrerequisiteMapping();
}


