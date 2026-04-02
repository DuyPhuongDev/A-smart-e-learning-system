package com.hcmut.lms.personalization.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrerequisiteChainRequest {

    private String specializationId;

    private List<UUID> completedSubjectIds;

    private List<UUID> remainingSubjectIds;
}
