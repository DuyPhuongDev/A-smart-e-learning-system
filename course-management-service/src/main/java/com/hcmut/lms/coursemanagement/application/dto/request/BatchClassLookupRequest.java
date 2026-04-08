package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for batch class section lookup
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BatchClassLookupRequest {
    @NotEmpty(message = "Class IDs cannot be empty")
    private List<UUID> classIds;
    
    private String semesterCode;
    private String searchTerm;

    public BatchClassLookupRequest(List<UUID> classIds) {
        this.classIds = classIds;
    }
}
