package com.hcmut.lms.coursemanagement.application.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.TextLecture;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactory;
import org.springframework.stereotype.Component;

@Component
public class TextLectureFactory implements LectureFactory {
    @Override
    public Lecture createLecture(LectureRequest request) {
        TextLecture lecture = new TextLecture(
                request.getTitle(),
                request.getContent(),
                request.getFormatType()
        );

        setCommonProperties(lecture, request);
        return lecture;
    }

    @Override
    public boolean supports(LectureRequest request) {
        return LectureType.TEXT.equals(request.getLectureType());
    }

    private void setCommonProperties(Lecture lecture, LectureRequest request) {
        lecture.setDescription(request.getDescription());
        lecture.setIsMandatory(request.getIsMandatory());
        lecture.setAllowPreview(request.getAllowPreview());
        lecture.setIsDownloadable(request.getIsDownloadable());
        lecture.setOrderIndex(request.getOrderIndex());
    }
}
