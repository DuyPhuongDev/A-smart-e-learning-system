package com.hcmut.lms.learning.client.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SubjectWindowDatasetLookupRequest {
    private UUID subjectId;
    private Integer targetSemKey;
    private Integer windowSpan;
}
