package com.hcmut.lms.coursemanagement.application.dto.request;

import lombok.Data;

@Data
public class SubjectRequest {
    private String name;
    private String description;
    private String code;
    private Integer credits;
}
