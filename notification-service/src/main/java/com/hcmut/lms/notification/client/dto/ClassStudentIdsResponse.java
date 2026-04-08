package com.hcmut.lms.notification.client.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ClassStudentIdsResponse {
    private UUID classId;
    private List<UUID> studentIds;
}
