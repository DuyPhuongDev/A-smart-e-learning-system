package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.dto.request.CreateLectureKnowledgeChunkRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeChunkResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.LectureKnowledgeChunkMapper;
import com.hcmut.lms.coachingchatbot.application.service.LectureKnowledgeChunkService;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeChunkRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureKnowledgeChunkServiceImpl implements LectureKnowledgeChunkService {

    private final LectureKnowledgeChunkRepository chunkRepository;
    private final LectureKnowledgeRepository lectureKnowledgeRepository;
    private final LectureKnowledgeChunkMapper chunkMapper;

    @Override
    public LectureKnowledgeChunkResponse createChunk(UUID lectureKnowledgeId,
            CreateLectureKnowledgeChunkRequest request) {
        LectureKnowledge lectureKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId);
        if (lectureKnowledge == null) {
            throw new RuntimeException("LectureKnowledge not found with id: " + lectureKnowledgeId);
        }

        LectureKnowledgeChunk chunk = LectureKnowledgeChunk.builder()
                .id(UUID.randomUUID())
                .chunkIndex(request.getChunkIndex())
                .chunkContent(request.getChunkContent())
                .qdrantPointId(UUID.randomUUID())
                .startTimeSeconds(request.getStartTimeSeconds())
                .endTimeSeconds(request.getEndTimeSeconds())
                .pageNumber(request.getPageNumber())
                .tokenCount(request.getTokenCount())
                .lectureKnowledge(lectureKnowledge)
                .build();

        LectureKnowledgeChunk saved = chunkRepository.save(chunk);

        // Update totalChunks
        lectureKnowledge.setTotalChunks(lectureKnowledge.getTotalChunks() + 1);
        lectureKnowledgeRepository.save(lectureKnowledge);

        return chunkMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LectureKnowledgeChunkResponse getChunkById(UUID id) {
        LectureKnowledgeChunk chunk = chunkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LectureKnowledgeChunk not found with id: " + id));
        return chunkMapper.toResponse(chunk);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LectureKnowledgeChunkResponse> getChunksByLectureKnowledgeId(UUID lectureKnowledgeId) {
        List<LectureKnowledgeChunk> chunks = chunkRepository
                .findByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId);
        return chunks.stream()
                .map(chunkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LectureKnowledgeChunkResponse> getChunksByLectureKnowledgeIdOrderByIndex(UUID lectureKnowledgeId) {
        List<LectureKnowledgeChunk> chunks = chunkRepository
                .findByLectureKnowledgeLectureKnowledgeIdOrderByChunkIndexAsc(lectureKnowledgeId);
        return chunks.stream()
                .map(chunkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LectureKnowledgeChunkResponse updateChunk(UUID id, CreateLectureKnowledgeChunkRequest request) {
        LectureKnowledgeChunk chunk = chunkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LectureKnowledgeChunk not found with id: " + id));

        chunk.setChunkIndex(request.getChunkIndex());
        chunk.setChunkContent(request.getChunkContent());
        chunk.setStartTimeSeconds(request.getStartTimeSeconds());
        chunk.setEndTimeSeconds(request.getEndTimeSeconds());
        chunk.setPageNumber(request.getPageNumber());
        chunk.setTokenCount(request.getTokenCount());

        LectureKnowledgeChunk updated = chunkRepository.save(chunk);
        return chunkMapper.toResponse(updated);
    }

    @Override
    public void deleteChunk(UUID id) {
        LectureKnowledgeChunk chunk = chunkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LectureKnowledgeChunk not found with id: " + id));

        // Update totalChunks
        LectureKnowledge lectureKnowledge = chunk.getLectureKnowledge();
        lectureKnowledge.setTotalChunks(Math.max(0, lectureKnowledge.getTotalChunks() - 1));
        lectureKnowledgeRepository.save(lectureKnowledge);

        chunkRepository.deleteById(id);
    }

    @Override
    public void deleteAllChunksByLectureKnowledgeId(UUID lectureKnowledgeId) {
        LectureKnowledge lectureKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId);
        if (lectureKnowledge == null) {
            throw new RuntimeException("LectureKnowledge not found with id: " + lectureKnowledgeId);
        }

        chunkRepository.deleteByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId);

        // Reset totalChunks
        lectureKnowledge.setTotalChunks(0);
        lectureKnowledgeRepository.save(lectureKnowledge);
    }
}
