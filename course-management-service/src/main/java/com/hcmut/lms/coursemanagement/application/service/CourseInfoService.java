package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.CourseInfoRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseInfoResponse;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfoId;

import java.util.List;
import java.util.UUID;

public interface CourseInfoService {
    CourseInfoResponse createCourseInfo(CourseInfoRequest request);
    CourseInfoResponse updateCourseInfo(CourseInfoId id, CourseInfoRequest request);
    void deleteCourseInfo(CourseInfoId id);
    List<CourseInfoResponse> getCourseInfos();
    CourseInfoResponse getCourseInfoById(CourseInfoId id);
    List<CourseInfoResponse> getCourseInfosByClassId(UUID classId);

}
