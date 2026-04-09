package com.hcmut.lms.assessment.client.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ClassSectionReportMetadataResponse {
    private UUID classId;
    private UUID teacherId;
    private List<LectureReportMetadataResponse> lectures;
}

