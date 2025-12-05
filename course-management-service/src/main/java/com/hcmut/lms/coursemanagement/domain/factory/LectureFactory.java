package com.hcmut.lms.coursemanagement.domain.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;

public interface LectureFactory {
    Lecture createLecture(BaseLectureRequest request);

    boolean supports(BaseLectureRequest request);
}
