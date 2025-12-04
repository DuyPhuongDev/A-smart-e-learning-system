package com.hcmut.lms.coursemanagement.application.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.DocumentLecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactory;
import org.springframework.stereotype.Component;

@Component
public class DocumentLectureFactory implements LectureFactory {

    @Override
    public Lecture createLecture(LectureRequest request) {
        DocumentLecture documentLecture = new DocumentLecture(
                request.getTitle(),
                request.getFileUrl(),
                request.getNumPages(),
                request.getFileFormat()
        );

        setCommonProperties(documentLecture, request);
        return documentLecture;
    }

    @Override
    public boolean supports(LectureRequest request) {
        return LectureType.DOCUMENT.equals(request.getLectureType());
    }

    private void setCommonProperties(Lecture lecture, LectureRequest request) {
        lecture.setDescription(request.getDescription());
        lecture.setIsMandatory(request.getIsMandatory());
        lecture.setAllowPreview(request.getAllowPreview());
        lecture.setIsDownloadable(request.getIsDownloadable());
        lecture.setOrderIndex(request.getOrderIndex());
    }
}
