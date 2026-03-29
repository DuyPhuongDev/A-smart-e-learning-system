package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectCategory;
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
public class SubjectResponse {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private Integer credits;
    private String category;
    private String createdAt;
    private String updatedAt;
}


