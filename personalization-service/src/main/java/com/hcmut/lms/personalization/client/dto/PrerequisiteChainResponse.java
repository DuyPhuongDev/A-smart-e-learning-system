package com.hcmut.lms.personalization.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrerequisiteChainResponse {

    private Integer longestChainLength;
}
