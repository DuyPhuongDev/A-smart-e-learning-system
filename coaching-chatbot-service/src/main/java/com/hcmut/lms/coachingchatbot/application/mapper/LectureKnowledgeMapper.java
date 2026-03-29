package com.hcmut.lms.coachingchatbot.application.mapper;

import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeResponse;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LectureKnowledgeMapper {

    public LectureKnowledgeResponse toResponse(LectureKnowledge entity) {
        if (entity == null) {
            return null;
        }

        return new LectureKnowledgeResponse(
                entity.getLectureKnowledgeId(),
                entity.getSyncStatus().name(),
                entity.getLastSyncedAt(),
                entity.getErrorMessage(),
                entity.getEmbeddingModel(),
                entity.getContentType(),
                entity.getTotalChunks(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public LectureKnowledge toEntity(UUID lectureKnowledgeId, String embeddingModel) {
        return LectureKnowledge.builder()
                .lectureKnowledgeId(lectureKnowledgeId)
                .syncStatus(SyncStatus.PENDING)
                .embeddingModel(embeddingModel)
                .build();
    }
}
