package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
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
}

