package com.hcmut.lms.coursemanagement.application.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.TextLectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.TextLecture;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactory;
import org.springframework.stereotype.Component;

@Component
public class TextLectureFactory implements LectureFactory {
    @Override
    public Lecture createLecture(BaseLectureRequest request) {
        TextLectureRequest req = (TextLectureRequest) request;
        TextLecture lecture = new TextLecture(
                req.getTitle(),
                req.getContent(),
                req.getFormatType()
        );

        setCommonProperties(lecture, request);
        return lecture;
    }

    @Override
    public boolean supports(BaseLectureRequest request) {
        return request instanceof TextLectureRequest;
    }

    private void setCommonProperties(Lecture lecture, BaseLectureRequest request) {
        lecture.setDescription(request.getDescription());
        lecture.setIsMandatory(request.getIsMandatory());
        lecture.setAllowPreview(request.getAllowPreview());
        lecture.setIsDownloadable(request.getIsDownloadable());
        lecture.setOrderIndex(request.getOrderIndex());
    }
}
