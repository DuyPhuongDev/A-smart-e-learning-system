package com.hcmut.lms.learning.client.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for batch class section lookup
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchClassLookupRequest {
    private List<UUID> classIds;
    private String semesterCode;
    private String searchTerm;
}
