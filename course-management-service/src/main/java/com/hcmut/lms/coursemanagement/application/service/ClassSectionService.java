package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;

import java.util.List;
import java.util.UUID;

public interface ClassSectionService {
    ClassSectionResponse createClassSection(CurrentUserInfo currentUser, ClassSectionRequest request);
    ClassSectionResponse updateClassSection(UUID id, ClassSectionRequest request);
    ClassSectionResponse getClassSectionById(UUID id);
    List<ClassSectionResponse> getAllClassSections();
    PageResponse<ClassSectionResponse> getAllClassSections(int page, int size);
    List<ClassSectionResponse> getClassSectionsBySubjectId(UUID subjectId);
    PageResponse<ClassSectionResponse> getClassSectionsBySubjectId(UUID subjectId, int page, int size);
    List<ClassSectionResponse> getClassSectionsBySemesterId(UUID semesterId);
    PageResponse<ClassSectionResponse> getClassSectionsBySemesterId(UUID semesterId, int page, int size);
    List<ClassSectionResponse> getClassSectionsByTeacherId(UUID teacherId);
    PageResponse<ClassSectionResponse> getClassSectionsByTeacherId(UUID teacherId, int page, int size);
    void deleteClassSection(UUID id);
    ClassSectionResponse assignTeacherToClassSection(UUID id, ClassSectionRequest request);
}

