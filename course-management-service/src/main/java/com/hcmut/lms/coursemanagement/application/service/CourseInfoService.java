package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.CourseInfoRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseInfoResponse;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfoId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CourseInfoService {
    CourseInfoResponse createCourseInfo(CourseInfoRequest request, MultipartFile thumbnail, MultipartFile introVideo);
    CourseInfoResponse updateCourseInfo(CourseInfoId id, CourseInfoRequest request,  MultipartFile thumbnail, MultipartFile introVideo);
    void deleteCourseInfo(CourseInfoId id);
    CourseInfoResponse getCourseInfoById(CourseInfoId id);
    CourseInfoResponse getCourseInfosByClassId(UUID classId);

}
