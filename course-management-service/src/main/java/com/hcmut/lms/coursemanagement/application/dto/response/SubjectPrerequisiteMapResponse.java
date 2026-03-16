package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Maps a subject to its related subjects (prerequisites + recommendations).
 * Used by learning-service for relative course feature computation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectPrerequisiteMapResponse {
    private UUID subjectId;
    private List<UUID> relatedSubjectIds;
}

