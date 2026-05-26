package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.application.dto.request.CreateLectureKnowledgeChunkRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeChunkResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.LectureKnowledgeChunkMapper;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeChunkRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LectureKnowledgeChunkServiceImplTest {

    @Mock private LectureKnowledgeChunkRepository chunkRepository;
    @Mock private LectureKnowledgeRepository lectureKnowledgeRepository;
    @Mock private LectureKnowledgeChunkMapper chunkMapper;

    @InjectMocks
    private LectureKnowledgeChunkServiceImpl lectureKnowledgeChunkService;

    private static final UUID lectureKnowledgeId = UUID.randomUUID();
    private static final UUID chunkId = UUID.randomUUID();

    @Test
    void createChunk_shouldReturnResponse_whenValidRequest() {
        CreateLectureKnowledgeChunkRequest request = CreateLectureKnowledgeChunkRequest.builder()
                .chunkIndex(0).chunkContent("Test content").tokenCount(100).build();
        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);
        lectureKnowledge.setTotalChunks(0);
        LectureKnowledgeChunk savedChunk = new LectureKnowledgeChunk();
        savedChunk.setId(chunkId);

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId)).thenReturn(lectureKnowledge);
        when(chunkRepository.save(any())).thenReturn(savedChunk);
        when(lectureKnowledgeRepository.save(any())).thenReturn(lectureKnowledge);
        when(chunkMapper.toResponse(savedChunk)).thenReturn(new LectureKnowledgeChunkResponse());

        LectureKnowledgeChunkResponse result = lectureKnowledgeChunkService.createChunk(lectureKnowledgeId, request);
        assertNotNull(result);
        assertEquals(1, lectureKnowledge.getTotalChunks());
    }

    @Test
    void createChunk_shouldThrowException_whenKnowledgeNotFound() {
        CreateLectureKnowledgeChunkRequest request = CreateLectureKnowledgeChunkRequest.builder()
                .chunkIndex(0).chunkContent("Content").build();
        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> lectureKnowledgeChunkService.createChunk(lectureKnowledgeId, request));
    }

    @Test
    void getChunkById_shouldReturnResponse_whenExists() {
        LectureKnowledgeChunk chunk = new LectureKnowledgeChunk();
        chunk.setId(chunkId);
        when(chunkRepository.findById(chunkId)).thenReturn(Optional.of(chunk));
        when(chunkMapper.toResponse(chunk)).thenReturn(new LectureKnowledgeChunkResponse());

        assertNotNull(lectureKnowledgeChunkService.getChunkById(chunkId));
    }

    @Test
    void getChunkById_shouldThrowException_whenNotFound() {
        when(chunkRepository.findById(chunkId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lectureKnowledgeChunkService.getChunkById(chunkId));
    }

    @Test
    void getChunksByLectureKnowledgeId_shouldReturnList_whenExist() {
        LectureKnowledgeChunk chunk = new LectureKnowledgeChunk();
        when(chunkRepository.findByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId)).thenReturn(List.of(chunk));
        when(chunkMapper.toResponse(chunk)).thenReturn(new LectureKnowledgeChunkResponse());

        List<LectureKnowledgeChunkResponse> result = lectureKnowledgeChunkService.getChunksByLectureKnowledgeId(lectureKnowledgeId);
        assertFalse(result.isEmpty());
    }

    @Test
    void getChunksByLectureKnowledgeId_shouldReturnEmptyList_whenNoChunks() {
        when(chunkRepository.findByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId)).thenReturn(List.of());

        List<LectureKnowledgeChunkResponse> result = lectureKnowledgeChunkService.getChunksByLectureKnowledgeId(lectureKnowledgeId);
        assertTrue(result.isEmpty());
    }

    @Test
    void getChunksByLectureKnowledgeIdOrderByIndex_shouldReturnOrderedList() {
        LectureKnowledgeChunk chunk = new LectureKnowledgeChunk();
        when(chunkRepository.findByLectureKnowledgeLectureKnowledgeIdOrderByChunkIndexAsc(lectureKnowledgeId))
                .thenReturn(List.of(chunk));
        when(chunkMapper.toResponse(chunk)).thenReturn(new LectureKnowledgeChunkResponse());

        List<LectureKnowledgeChunkResponse> result = lectureKnowledgeChunkService.getChunksByLectureKnowledgeIdOrderByIndex(lectureKnowledgeId);
        assertFalse(result.isEmpty());
    }

    @Test
    void updateChunk_shouldReturnUpdatedResponse_whenExists() {
        CreateLectureKnowledgeChunkRequest request = CreateLectureKnowledgeChunkRequest.builder()
                .chunkIndex(1).chunkContent("Updated content").tokenCount(50)
                .startTimeSeconds(10).endTimeSeconds(30).build();
        LectureKnowledgeChunk chunk = new LectureKnowledgeChunk();
        chunk.setId(chunkId);

        when(chunkRepository.findById(chunkId)).thenReturn(Optional.of(chunk));
        when(chunkRepository.save(chunk)).thenReturn(chunk);
        when(chunkMapper.toResponse(chunk)).thenReturn(new LectureKnowledgeChunkResponse());

        LectureKnowledgeChunkResponse result = lectureKnowledgeChunkService.updateChunk(chunkId, request);
        assertNotNull(result);
    }

    @Test
    void updateChunk_shouldThrowException_whenNotFound() {
        CreateLectureKnowledgeChunkRequest request = CreateLectureKnowledgeChunkRequest.builder()
                .chunkIndex(0).chunkContent("Content").build();
        when(chunkRepository.findById(chunkId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lectureKnowledgeChunkService.updateChunk(chunkId, request));
    }

    @Test
    void deleteChunk_shouldDeleteAndDecrementTotalChunks() {
        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);
        lectureKnowledge.setTotalChunks(5);
        LectureKnowledgeChunk chunk = new LectureKnowledgeChunk();
        chunk.setId(chunkId);
        chunk.setLectureKnowledge(lectureKnowledge);

        when(chunkRepository.findById(chunkId)).thenReturn(Optional.of(chunk));
        doNothing().when(chunkRepository).deleteById(chunkId);
        when(lectureKnowledgeRepository.save(any())).thenReturn(lectureKnowledge);

        assertDoesNotThrow(() -> lectureKnowledgeChunkService.deleteChunk(chunkId));
        assertEquals(4, lectureKnowledge.getTotalChunks());
    }

    @Test
    void deleteChunk_shouldNotGoNegative_whenTotalChunksIsZero() {
        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);
        lectureKnowledge.setTotalChunks(0);
        LectureKnowledgeChunk chunk = new LectureKnowledgeChunk();
        chunk.setId(chunkId);
        chunk.setLectureKnowledge(lectureKnowledge);

        when(chunkRepository.findById(chunkId)).thenReturn(Optional.of(chunk));
        doNothing().when(chunkRepository).deleteById(chunkId);
        when(lectureKnowledgeRepository.save(any())).thenReturn(lectureKnowledge);

        assertDoesNotThrow(() -> lectureKnowledgeChunkService.deleteChunk(chunkId));
        assertEquals(0, lectureKnowledge.getTotalChunks());
    }

    @Test
    void deleteChunk_shouldThrowException_whenNotFound() {
        when(chunkRepository.findById(chunkId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lectureKnowledgeChunkService.deleteChunk(chunkId));
    }

    @Test
    void deleteAllChunksByLectureKnowledgeId_shouldDeleteAllAndResetCount() {
        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);
        lectureKnowledge.setTotalChunks(10);

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId)).thenReturn(lectureKnowledge);
        doNothing().when(chunkRepository).deleteByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId);
        when(lectureKnowledgeRepository.save(any())).thenReturn(lectureKnowledge);

        assertDoesNotThrow(() -> lectureKnowledgeChunkService.deleteAllChunksByLectureKnowledgeId(lectureKnowledgeId));
        assertEquals(0, lectureKnowledge.getTotalChunks());
    }

    @Test
    void deleteAllChunksByLectureKnowledgeId_shouldThrowException_whenLectureKnowledgeNotFound() {
        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> lectureKnowledgeChunkService.deleteAllChunksByLectureKnowledgeId(lectureKnowledgeId));
    }
}
