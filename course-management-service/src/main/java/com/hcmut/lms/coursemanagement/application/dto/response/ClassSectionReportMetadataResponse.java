package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ClassSectionReportMetadataResponse {
    private UUID classId;
    private UUID teacherId;
    private List<LectureReportMetadataResponse> lectures;
}

