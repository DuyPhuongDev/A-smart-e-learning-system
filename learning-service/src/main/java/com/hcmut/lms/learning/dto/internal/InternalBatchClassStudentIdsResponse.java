package com.hcmut.lms.learning.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InternalBatchClassStudentIdsResponse {
    private List<InternalClassStudentIdsResponse> items;
}
