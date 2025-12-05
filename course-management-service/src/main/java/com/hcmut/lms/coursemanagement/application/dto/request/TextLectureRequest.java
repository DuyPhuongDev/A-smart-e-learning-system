package com.hcmut.lms.coursemanagement.application.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextLectureRequest extends  BaseLectureRequest{
    private String content;
    private String formatType;
}
