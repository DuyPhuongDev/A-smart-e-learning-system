package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class SubjectRequest {
    private String name;
    private String description;
    private String code;
    private Integer credits;

    @Valid
    private List<SubjectGradingRequest> gradings;
}
