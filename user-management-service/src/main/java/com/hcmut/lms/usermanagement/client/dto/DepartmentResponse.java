package com.hcmut.lms.usermanagement.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID facultyId;
    private String facultyName;
    private String createdAt;
    private String updatedAt;
}

