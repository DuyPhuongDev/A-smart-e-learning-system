package com.hcmut.lms.learning.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCountResponse {
    private UUID classId;
    private Integer submittedCount;
    private Integer count;
}
