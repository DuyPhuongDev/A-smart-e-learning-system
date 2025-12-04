package com.hcmut.lms.coursemanagement.domain.factory;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.exception.UnsupportedLectureTypeException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LectureFactoryProvider {

    private final List<LectureFactory> factories;

    public LectureFactoryProvider(List<LectureFactory> factories) {
        this.factories = factories;
    }

    public Lecture createLecture(LectureRequest request){
        return factories.stream()
                .filter(factory -> factory.supports(request))
                .findFirst()
                .map(factory -> factory.createLecture(request))
                .orElseThrow(() -> new UnsupportedLectureTypeException(
                        "Unsupported Lecture type: " + request.getLectureType()
                ));
    }

}
