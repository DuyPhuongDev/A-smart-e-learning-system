package com.hcmut.lms.learning.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class LectureNoteRequest {
    private UUID studentId;
    private UUID lectureId;
    private String lectureType;
    private String content;
    private Integer contentPosition;
}
