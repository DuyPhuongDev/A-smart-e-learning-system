package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;

import java.util.List;

public interface LectureService {
    LectureResponse createLecture(LectureRequest request);

    List<LectureResponse> getLectures();
    LectureResponse getLectureById(String id);

    LectureResponse updateLecture(LectureRequest request);
    LectureResponse deleteLecture(String id);

}
