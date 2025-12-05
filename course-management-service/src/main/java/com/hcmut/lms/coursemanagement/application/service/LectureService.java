package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderListRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;

import java.util.List;
import java.util.UUID;

public interface LectureService {
    LectureResponse createLecture(BaseLectureRequest request);
    LectureResponse updateLecture(UUID id, BaseLectureRequest request);
    LectureResponse getLectureById(UUID id);
    List<LectureResponse> getLecturesByChapterId(UUID chapterId);
    void deleteLecture(UUID id);
    List<LectureResponse> reorderLectures(ReorderListRequest request);
}
