package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResultDto {
    private Integer totalEndpoints;
    private Integer newEndpoints;
    private Integer updatedEndpoints;
}

