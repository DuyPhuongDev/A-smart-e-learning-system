package com.hcmut.lms.coursemanagement.application.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.DocumentLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.DocumentLecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactory;
import org.springframework.stereotype.Component;

@Component
public class DocumentLectureFactory implements LectureFactory {

    final

    @Override
    public Lecture createLecture(BaseLectureRequest request) {
        // cast baselecture to DocumentLecture
        DocumentLectureRequest req = (DocumentLectureRequest) request;

        DocumentLecture documentLecture = new DocumentLecture(
                req.getTitle(),
                req.getFileUrl()
        );

        setCommonProperties(documentLecture, request);
        return documentLecture;
    }

    @Override
    public boolean supports(BaseLectureRequest request) {
        return request instanceof DocumentLectureRequest;
    }

    private void setCommonProperties(Lecture lecture, BaseLectureRequest request) {
        lecture.setDescription(request.getDescription());
        lecture.setIsMandatory(request.getIsMandatory());
        lecture.setAllowPreview(request.getAllowPreview());
        lecture.setIsDownloadable(request.getIsDownloadable());
        lecture.setOrderIndex(request.getOrderIndex());
    }
}
