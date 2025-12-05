package com.hcmut.lms.coursemanagement.application.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoLectureRequest extends BaseLectureRequest {
    private String videoUrl;
    private Integer duration;
}
