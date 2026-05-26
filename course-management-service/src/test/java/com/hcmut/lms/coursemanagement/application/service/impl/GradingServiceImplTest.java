package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coursemanagement.application.dto.request.GradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.GradingResponse;
import com.hcmut.lms.coursemanagement.application.mapper.GradingMapper;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Grading;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradingServiceImplTest {

    @Mock
    private GradingRepository gradingRepository;

    @Mock
    private GradingMapper gradingMapper;

    @InjectMocks
    private GradingServiceImpl gradingService;

    // --- create ---

    @Test
    void createGrading_shouldReturnResponse_whenValidRequest() {
        GradingRequest request = new GradingRequest();
        request.setName("Midterm");
        request.setDescription("Midterm exam grading");

        UUID id = UUID.randomUUID();

        Grading entity = Grading.builder()
                .name("Midterm")
                .description("Midterm exam grading")
                .build();

        Grading savedEntity = Grading.builder()
                .name("Midterm")
                .description("Midterm exam grading")
                .build();
        savedEntity.setId(id);

        GradingResponse response = GradingResponse.builder()
                .id(id)
                .name("Midterm")
                .build();

        when(gradingMapper.toEntity(any())).thenReturn(entity);
        when(gradingRepository.save(entity)).thenReturn(savedEntity);
        when(gradingMapper.toResponse(savedEntity)).thenReturn(response);

        GradingResponse result = gradingService.createGrading(request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(gradingMapper).toEntity(request);
        verify(gradingRepository).save(entity);
        verify(gradingMapper).toResponse(savedEntity);
    }

    // --- getById ---

    @Test
    void getGradingById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Grading entity = Grading.builder().name("Final").build();
        entity.setId(id);

        GradingResponse response = GradingResponse.builder()
                .id(id)
                .name("Final")
                .build();

        when(gradingRepository.findById(id)).thenReturn(Optional.of(entity));
        when(gradingMapper.toResponse(entity)).thenReturn(response);

        GradingResponse result = gradingService.getGradingById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getGradingById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(gradingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gradingService.getGradingById(id));
    }

    // --- getAll ---

    @Test
    void getAllGradings_shouldReturnList() {
        UUID id = UUID.randomUUID();
        Grading entity = Grading.builder().name("Assignment").build();
        entity.setId(id);

        GradingResponse response = GradingResponse.builder()
                .id(id)
                .name("Assignment")
                .build();

        when(gradingRepository.findAll()).thenReturn(List.of(entity));
        when(gradingMapper.toResponse(entity)).thenReturn(response);

        List<GradingResponse> result = gradingService.getAllGradings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(id, result.get(0).getId());
    }

    @Test
    void getAllGradings_shouldReturnEmptyList_whenNone() {
        when(gradingRepository.findAll()).thenReturn(List.of());

        List<GradingResponse> result = gradingService.getAllGradings();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- update ---

    @Test
    void updateGrading_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        GradingRequest request = new GradingRequest();
        request.setName("Updated Midterm");

        Grading existingEntity = Grading.builder().name("Midterm").build();
        existingEntity.setId(id);

        Grading updatedEntity = Grading.builder().name("Updated Midterm").build();
        updatedEntity.setId(id);

        GradingResponse response = GradingResponse.builder()
                .id(id)
                .name("Updated Midterm")
                .build();

        when(gradingRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(gradingRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(gradingMapper.toResponse(updatedEntity)).thenReturn(response);

        GradingResponse result = gradingService.updateGrading(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Updated Midterm", result.getName());
        verify(gradingMapper).updateEntityFromRequest(request, existingEntity);
    }

    @Test
    void updateGrading_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        GradingRequest request = new GradingRequest();

        when(gradingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gradingService.updateGrading(id, request));
    }

    // --- delete ---

    @Test
    void deleteGrading_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(gradingRepository.existsById(id)).thenReturn(true);

        gradingService.deleteGrading(id);

        verify(gradingRepository).deleteById(id);
    }

    @Test
    void deleteGrading_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(gradingRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> gradingService.deleteGrading(id));
        verify(gradingRepository, never()).deleteById(any());
    }
}
