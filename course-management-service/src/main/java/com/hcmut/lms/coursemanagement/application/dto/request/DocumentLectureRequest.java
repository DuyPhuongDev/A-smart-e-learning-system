package com.hcmut.lms.coursemanagement.application.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentLectureRequest extends BaseLectureRequest {
    private String fileUrl;
    private Integer numPages;
    private String fileFormat;
}
