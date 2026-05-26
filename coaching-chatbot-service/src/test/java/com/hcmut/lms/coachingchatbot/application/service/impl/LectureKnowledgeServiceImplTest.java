package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.application.dto.request.CreateLectureKnowledgeRequest;
import com.hcmut.lms.coachingchatbot.application.dto.request.UpdateLectureKnowledgeRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.LectureKnowledgeMapper;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LectureKnowledgeServiceImplTest {

    @Mock private LectureKnowledgeRepository lectureKnowledgeRepository;
    @Mock private LectureKnowledgeMapper lectureKnowledgeMapper;

    @InjectMocks
    private LectureKnowledgeServiceImpl lectureKnowledgeService;

    private static final UUID lectureId = UUID.randomUUID();

    // === createLectureKnowledge ===

    @Test
    void createLectureKnowledge_shouldReturnResponse_whenValidRequest() {
        CreateLectureKnowledgeRequest request = new CreateLectureKnowledgeRequest(lectureId, null);
        LectureKnowledge entity = new LectureKnowledge();
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                lectureId, "PENDING", null, null, "gemini-text-embedding-004", null, 0, null, null);

        when(lectureKnowledgeRepository.existsByLectureKnowledgeId(lectureId)).thenReturn(false);
        when(lectureKnowledgeRepository.save(any())).thenReturn(entity);
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        LectureKnowledgeResponse result = lectureKnowledgeService.createLectureKnowledge(request);
        assertNotNull(result);
        assertEquals("PENDING", result.syncStatus());
    }

    @Test
    void createLectureKnowledge_shouldThrowException_whenAlreadyExists() {
        CreateLectureKnowledgeRequest request = new CreateLectureKnowledgeRequest(lectureId, null);
        when(lectureKnowledgeRepository.existsByLectureKnowledgeId(lectureId)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> lectureKnowledgeService.createLectureKnowledge(request));
    }

    @Test
    void createLectureKnowledge_shouldUseCustomEmbeddingModel_whenProvided() {
        UUID id = UUID.randomUUID();
        CreateLectureKnowledgeRequest request = new CreateLectureKnowledgeRequest(id, "custom-model");
        LectureKnowledge entity = new LectureKnowledge();
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                id, "PENDING", null, null, "custom-model", null, 0, null, null);

        when(lectureKnowledgeRepository.existsByLectureKnowledgeId(id)).thenReturn(false);
        when(lectureKnowledgeRepository.save(any())).thenReturn(entity);
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        LectureKnowledgeResponse result = lectureKnowledgeService.createLectureKnowledge(request);
        assertNotNull(result);
    }

    // === getLectureKnowledgeById ===

    @Test
    void getLectureKnowledgeById_shouldReturnResponse_whenExists() {
        LectureKnowledge entity = new LectureKnowledge();
        entity.setLectureKnowledgeId(lectureId);
        entity.setSyncStatus(SyncStatus.COMPLETED);
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                lectureId, "COMPLETED", null, null, null, null, 10, Instant.now(), Instant.now());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(entity);
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        LectureKnowledgeResponse result = lectureKnowledgeService.getLectureKnowledgeById(lectureId);
        assertNotNull(result);
        assertEquals("COMPLETED", result.syncStatus());
    }

    @Test
    void getLectureKnowledgeById_shouldThrowException_whenNotFound() {
        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> lectureKnowledgeService.getLectureKnowledgeById(lectureId));
    }

    // === getLectureKnowledgeByLectureId ===

    @Test
    void getLectureKnowledgeByLectureId_shouldReturnResponse_whenExists() {
        LectureKnowledge entity = new LectureKnowledge();
        entity.setLectureKnowledgeId(lectureId);
        entity.setSyncStatus(SyncStatus.PENDING);
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                lectureId, "PENDING", null, null, null, null, 0, Instant.now(), Instant.now());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(entity);
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        LectureKnowledgeResponse result = lectureKnowledgeService.getLectureKnowledgeByLectureId(lectureId);
        assertNotNull(result);
    }

    // === getAllLectureKnowledgeByLectureId ===

    @Test
    void getAllLectureKnowledgeByLectureId_shouldReturnList_whenExist() {
        LectureKnowledge entity = new LectureKnowledge();
        entity.setLectureKnowledgeId(lectureId);
        entity.setSyncStatus(SyncStatus.COMPLETED);
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                lectureId, "COMPLETED", null, null, null, null, 5, Instant.now(), Instant.now());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(entity);
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        List<LectureKnowledgeResponse> result = lectureKnowledgeService.getAllLectureKnowledgeByLectureId(lectureId);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllLectureKnowledgeByLectureId_shouldReturnEmptyList_whenNotFound() {
        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(null);

        List<LectureKnowledgeResponse> result = lectureKnowledgeService.getAllLectureKnowledgeByLectureId(lectureId);
        assertTrue(result.isEmpty());
    }

    // === updateLectureKnowledge ===

    @Test
    void updateLectureKnowledge_shouldUpdateStatus_whenStatusProvided() {
        UpdateLectureKnowledgeRequest request = new UpdateLectureKnowledgeRequest(null, null, "COMPLETED", null);
        LectureKnowledge entity = new LectureKnowledge();
        entity.setLectureKnowledgeId(lectureId);
        entity.setSyncStatus(SyncStatus.PROCESSING);
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                lectureId, "COMPLETED", null, null, null, null, 0, Instant.now(), Instant.now());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(entity);
        when(lectureKnowledgeRepository.save(entity)).thenReturn(entity);
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        LectureKnowledgeResponse result = lectureKnowledgeService.updateLectureKnowledge(lectureId, request);
        assertNotNull(result);
        assertEquals("COMPLETED", result.syncStatus());
    }

    @Test
    void updateLectureKnowledge_shouldMarkOutdated_whenTitleOrDescriptionChanged() {
        UpdateLectureKnowledgeRequest request = new UpdateLectureKnowledgeRequest("New Title", null, null, null);
        LectureKnowledge entity = new LectureKnowledge();
        entity.setLectureKnowledgeId(lectureId);
        entity.setSyncStatus(SyncStatus.COMPLETED);
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                lectureId, "OUTDATED", null, null, null, null, 0, Instant.now(), Instant.now());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(entity);
        when(lectureKnowledgeRepository.save(entity)).thenReturn(entity);
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        LectureKnowledgeResponse result = lectureKnowledgeService.updateLectureKnowledge(lectureId, request);
        assertNotNull(result);
        assertEquals(SyncStatus.OUTDATED, entity.getSyncStatus());
    }

    @Test
    void updateLectureKnowledge_shouldThrowException_whenNotFound() {
        UpdateLectureKnowledgeRequest request = new UpdateLectureKnowledgeRequest(null, null, "COMPLETED", null);
        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> lectureKnowledgeService.updateLectureKnowledge(lectureId, request));
    }

    // === deleteLectureKnowledge ===

    @Test
    void deleteLectureKnowledge_shouldDelete_whenExists() {
        doNothing().when(lectureKnowledgeRepository).deleteById(lectureId);

        assertDoesNotThrow(() -> lectureKnowledgeService.deleteLectureKnowledge(lectureId));
        verify(lectureKnowledgeRepository).deleteById(lectureId);
    }

    // === getAllByStatus ===

    @Test
    void getAllByStatus_shouldReturnFilteredList() {
        LectureKnowledge entity = new LectureKnowledge();
        entity.setSyncStatus(SyncStatus.COMPLETED);
        LectureKnowledgeResponse expected = new LectureKnowledgeResponse(
                UUID.randomUUID(), "COMPLETED", null, null, null, null, 10, Instant.now(), Instant.now());

        when(lectureKnowledgeRepository.findBySyncStatus(SyncStatus.COMPLETED)).thenReturn(List.of(entity));
        when(lectureKnowledgeMapper.toResponse(entity)).thenReturn(expected);

        List<LectureKnowledgeResponse> result = lectureKnowledgeService.getAllByStatus("COMPLETED");
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllByStatus_shouldReturnEmptyList_whenNoMatches() {
        when(lectureKnowledgeRepository.findBySyncStatus(SyncStatus.FAILED)).thenReturn(List.of());

        List<LectureKnowledgeResponse> result = lectureKnowledgeService.getAllByStatus("FAILED");
        assertTrue(result.isEmpty());
    }
}
