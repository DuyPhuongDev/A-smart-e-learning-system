package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.dto.request.CreateLectureKnowledgeRequest;
import com.hcmut.lms.coachingchatbot.application.dto.request.UpdateLectureKnowledgeRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.LectureKnowledgeMapper;
import com.hcmut.lms.coachingchatbot.application.service.LectureKnowledgeService;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureKnowledgeServiceImpl implements LectureKnowledgeService {

    private final LectureKnowledgeRepository lectureKnowledgeRepository;
    private final LectureKnowledgeMapper lectureKnowledgeMapper;

    @Getter
    private static final String defaultEmbeddingModel = "gemini-text-embedding-004";

    @Override
    public LectureKnowledgeResponse createLectureKnowledge(CreateLectureKnowledgeRequest request) {
        // Check if already exists
        if (lectureKnowledgeRepository.existsByLectureKnowledgeId(request.lectureKnowledgeId())) {
            throw new RuntimeException("LectureKnowledge already exists for lecture: " + request.lectureKnowledgeId());
        }

        LectureKnowledge lectureKnowledge = LectureKnowledge.builder()
                .lectureKnowledgeId(request.lectureKnowledgeId())
                .syncStatus(SyncStatus.PENDING)
                .embeddingModel(request.embeddingModel() != null ? request.embeddingModel() : defaultEmbeddingModel)
                .build();

        LectureKnowledge saved = lectureKnowledgeRepository.save(lectureKnowledge);
        return lectureKnowledgeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LectureKnowledgeResponse getLectureKnowledgeById(UUID id) {
        LectureKnowledge lectureKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(id);
        if (lectureKnowledge == null) {
            throw new RuntimeException("LectureKnowledge not found with id: " + id);
        }
        return lectureKnowledgeMapper.toResponse(lectureKnowledge);
    }

    @Override
    @Transactional(readOnly = true)
    public LectureKnowledgeResponse getLectureKnowledgeByLectureId(UUID lectureId) {
        // Since lectureKnowledgeId = lectureId (1:1 relationship)
        return getLectureKnowledgeById(lectureId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LectureKnowledgeResponse> getAllLectureKnowledgeByLectureId(UUID lectureId) {
        // For 1:1 relationship, return single item as list
        LectureKnowledge lectureKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);
        if (lectureKnowledge == null) {
            return List.of();
        }
        return List.of(lectureKnowledgeMapper.toResponse(lectureKnowledge));
    }

    @Override
    public LectureKnowledgeResponse updateLectureKnowledge(UUID id, UpdateLectureKnowledgeRequest request) {
        LectureKnowledge lectureKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(id);
        if (lectureKnowledge == null) {
            throw new RuntimeException("LectureKnowledge not found with id: " + id);
        }

        // Update sync status if provided
        if (request.status() != null) {
            lectureKnowledge.setSyncStatus(SyncStatus.valueOf(request.status()));
        }

        // Mark as outdated if content needs reprocessing
        if (request.title() != null || request.description() != null) {
            lectureKnowledge.setSyncStatus(SyncStatus.OUTDATED);
        }

        LectureKnowledge updated = lectureKnowledgeRepository.save(lectureKnowledge);
        return lectureKnowledgeMapper.toResponse(updated);
    }

    @Override
    public void deleteLectureKnowledge(UUID id) {
        lectureKnowledgeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LectureKnowledgeResponse> getAllByStatus(String status) {
        SyncStatus syncStatus = SyncStatus.valueOf(status);
        List<LectureKnowledge> lectureKnowledges = lectureKnowledgeRepository.findBySyncStatus(syncStatus);
        return lectureKnowledges.stream()
                .map(lectureKnowledgeMapper::toResponse)
                .collect(Collectors.toList());
    }
}
