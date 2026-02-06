package com.hcmut.lms.coachingchatbot.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for receiving chapter information from Course Management Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChapterResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer orderIndex;
    private String status;
    private UUID classSectionId;
    private Instant createdAt;
    private Instant updatedAt;
}
