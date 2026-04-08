package com.hcmut.lms.notification.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchClassStudentIdsResponse {
    private List<ClassStudentIdsResponse> items;
}
