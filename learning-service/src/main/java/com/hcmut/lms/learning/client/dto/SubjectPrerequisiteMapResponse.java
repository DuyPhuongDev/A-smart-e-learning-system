package com.hcmut.lms.learning.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Client-side DTO mirroring SubjectPrerequisiteMapResponse from course-management-service.
 * Maps a subject to its related subjects (prerequisites + recommendations).
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectPrerequisiteMapResponse {
    private UUID subjectId;
    private List<UUID> relatedSubjectIds;
}

