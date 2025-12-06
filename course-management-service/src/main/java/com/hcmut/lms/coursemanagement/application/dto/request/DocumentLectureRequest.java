package com.hcmut.lms.coursemanagement.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentLectureRequest extends BaseLectureRequest {
    @Schema(hidden = true)
    private String fileUrl;
    @Schema(hidden = true)
    private Integer numPages;

    @Schema(hidden = true)
    private String fileFormat;

    @Schema(hidden = true)
    private MultipartFile file;
}
