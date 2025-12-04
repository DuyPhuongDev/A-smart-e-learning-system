package com.hcmut.lms.coursemanagement.domain.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;

public interface LectureFactory {
    Lecture createLecture(LectureRequest request);

    boolean supports(LectureRequest request);
}
