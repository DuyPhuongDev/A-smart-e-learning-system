package com.hcmut.lms.coursemanagement.application.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactory;
import org.springframework.stereotype.Component;

@Component
public class VideoLectureFactory implements LectureFactory {

    @Override
    public Lecture createLecture(LectureRequest request) {
        VideoLecture lecture = new VideoLecture(
                request.getTitle(),
                request.getVideoUrl(),
                request.getDuration()
        );

        setCommonProperties(lecture, request);
        return lecture;
    }

    @Override
    public boolean supports(LectureRequest request) {
        return LectureType.VIDEO.equals(request.getLectureType());
    }

    private void setCommonProperties(Lecture lecture, LectureRequest request) {
        lecture.setDescription(request.getDescription());
        lecture.setIsMandatory(request.getIsMandatory());
        lecture.setAllowPreview(request.getAllowPreview());
        lecture.setIsDownloadable(request.getIsDownloadable());
        lecture.setOrderIndex(request.getOrderIndex());
    }
}

