package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubjectWindowDatasetLookupRequest {
    /** Target subject ID */
    @NotNull
    private UUID subjectId;

    /** Exclusive upper bound semKey (typically current semKey) */
    @NotNull
    private Integer targetSemKey;

    /** Window span (semKey units) to look back; default 30 (3-year window) */
    private Integer windowSpan;
}
