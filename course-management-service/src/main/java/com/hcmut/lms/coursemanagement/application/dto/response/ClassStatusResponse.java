package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassStatusResponse {
    private UUID id;
    private boolean exist;
    private String status;
    private boolean isOfficial;
    private int maxStudents;
    private int currentStudents;


}
