package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BatchClassDatasetLookupRequest {

    @NotEmpty(message = "Class IDs cannot be empty")
    private List<UUID> classIds;
}
