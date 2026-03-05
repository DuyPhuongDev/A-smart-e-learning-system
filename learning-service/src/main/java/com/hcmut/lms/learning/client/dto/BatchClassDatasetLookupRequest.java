package com.hcmut.lms.learning.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for the dataset metadata batch endpoint in course-management-service.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchClassDatasetLookupRequest {
    private List<UUID> classIds;
}
