package com.hcmut.lms.coursemanagement.application.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.VideoLectureRequest;
import com.hcmut.lms.coursemanagement.application.service.FileService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoLectureFactory implements LectureFactory {

    private final FileService fileService;

    @Override
    public Lecture createLecture(BaseLectureRequest request) {
        VideoLectureRequest req = (VideoLectureRequest) request;
        VideoLecture lecture = new VideoLecture(
                req.getTitle(),
                req.getVideoUrl(),
                req.getDuration()
        );

        setCommonProperties(lecture, request);
        return lecture;
    }

    @Override
    public boolean supports(BaseLectureRequest request) {
        return request instanceof VideoLectureRequest;
    }

    private void setCommonProperties(Lecture lecture, BaseLectureRequest request) {
        lecture.setDescription(request.getDescription());
        lecture.setIsMandatory(request.getIsMandatory());
        lecture.setAllowPreview(request.getAllowPreview());
        lecture.setIsDownloadable(request.getIsDownloadable());
        lecture.setOrderIndex(request.getOrderIndex());
    }
}

