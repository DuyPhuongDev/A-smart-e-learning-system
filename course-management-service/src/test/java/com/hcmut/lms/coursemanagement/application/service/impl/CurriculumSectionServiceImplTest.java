package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumSectionResponse;
import com.hcmut.lms.coursemanagement.application.mapper.CurriculumSectionMapper;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumId;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSection;
import com.hcmut.lms.coursemanagement.repository.CurriculumRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSectionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurriculumSectionServiceImplTest {

    @Mock
    private CurriculumSectionRepository curriculumSectionRepository;

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private CurriculumSectionMapper curriculumSectionMapper;

    @InjectMocks
    private CurriculumSectionServiceImpl curriculumSectionService;

    // --- create ---

    @Test
    void createCurriculumSection_shouldReturnResponse_whenValidRequest() {
        UUID specializationId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        CurriculumSectionRequest request = new CurriculumSectionRequest();
        request.setName("Section A");
        request.setCurriculumCode("CS2025");
        request.setCurriculumSpecializationId(specializationId);
        request.setCurriculumIntakeYearId(intakeYearId);

        UUID sectionId = UUID.randomUUID();

        CurriculumSection entity = CurriculumSection.builder()
                .name("Section A")
                .build();

        CurriculumId curriculumId = new CurriculumId("CS2025", specializationId, intakeYearId);
        Curriculum curriculum = Curriculum.builder()
                .id(curriculumId)
                .name("CS Curriculum 2025")
                .build();

        CurriculumSection savedEntity = CurriculumSection.builder()
                .name("Section A")
                .build();
        savedEntity.setId(sectionId);

        CurriculumSectionResponse response = CurriculumSectionResponse.builder()
                .id(sectionId)
                .name("Section A")
                .curriculumCode("CS2025")
                .build();

        when(curriculumSectionMapper.toEntity(any())).thenReturn(entity);
        when(curriculumRepository.findById(curriculumId)).thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.save(entity)).thenReturn(savedEntity);
        when(curriculumSectionMapper.toResponse(savedEntity)).thenReturn(response);

        CurriculumSectionResponse result = curriculumSectionService.createCurriculumSection(request);

        assertNotNull(result);
        assertEquals(sectionId, result.getId());
        verify(curriculumSectionMapper).toEntity(request);
        verify(curriculumRepository).findById(curriculumId);
        verify(curriculumSectionRepository).save(entity);
        verify(curriculumSectionMapper).toResponse(savedEntity);
    }

    @Test
    void createCurriculumSection_shouldThrowException_whenCurriculumNotFound() {
        UUID specializationId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        CurriculumSectionRequest request = new CurriculumSectionRequest();
        request.setName("Section A");
        request.setCurriculumCode("CS2025");
        request.setCurriculumSpecializationId(specializationId);
        request.setCurriculumIntakeYearId(intakeYearId);

        CurriculumSection entity = CurriculumSection.builder().name("Section A").build();

        when(curriculumSectionMapper.toEntity(any())).thenReturn(entity);
        when(curriculumRepository.findById(any(CurriculumId.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> curriculumSectionService.createCurriculumSection(request));
        verify(curriculumSectionRepository, never()).save(any());
    }

    // --- getById ---

    @Test
    void getCurriculumSectionById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        CurriculumSection entity = CurriculumSection.builder().name("Section A").build();
        entity.setId(id);

        CurriculumSectionResponse response = CurriculumSectionResponse.builder()
                .id(id)
                .name("Section A")
                .build();

        when(curriculumSectionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(curriculumSectionMapper.toResponse(entity)).thenReturn(response);

        CurriculumSectionResponse result = curriculumSectionService.getCurriculumSectionById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getCurriculumSectionById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(curriculumSectionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> curriculumSectionService.getCurriculumSectionById(id));
    }

    // --- getAll ---

    @Test
    void getAllCurriculumSections_shouldReturnList() {
        UUID id = UUID.randomUUID();
        CurriculumSection entity = CurriculumSection.builder().name("Section A").build();
        entity.setId(id);

        CurriculumSectionResponse response = CurriculumSectionResponse.builder()
                .id(id)
                .name("Section A")
                .build();

        when(curriculumSectionRepository.findAll()).thenReturn(List.of(entity));
        when(curriculumSectionMapper.toResponse(entity)).thenReturn(response);

        List<CurriculumSectionResponse> result = curriculumSectionService.getAllCurriculumSections();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllCurriculumSections_shouldReturnEmptyList_whenNone() {
        when(curriculumSectionRepository.findAll()).thenReturn(List.of());

        List<CurriculumSectionResponse> result = curriculumSectionService.getAllCurriculumSections();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getByCurriculumId ---

    @Test
    void getCurriculumSectionsByCurriculumId_shouldReturnList() {
        String code = "CS2025";
        UUID specializationId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        CurriculumSection entity = CurriculumSection.builder().name("Section A").build();
        entity.setId(id);

        CurriculumSectionResponse response = CurriculumSectionResponse.builder()
                .id(id)
                .name("Section A")
                .build();

        when(curriculumSectionRepository.findByCurriculumId(code, specializationId, intakeYearId))
                .thenReturn(List.of(entity));
        when(curriculumSectionMapper.toResponse(entity)).thenReturn(response);

        List<CurriculumSectionResponse> result = curriculumSectionService
                .getCurriculumSectionsByCurriculumId(code, specializationId, intakeYearId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // --- update ---

    @Test
    void updateCurriculumSection_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        CurriculumSectionRequest request = new CurriculumSectionRequest();
        request.setName("Updated Section A");
        // No curriculumCode change

        CurriculumSection existingEntity = CurriculumSection.builder().name("Section A").build();
        existingEntity.setId(id);

        CurriculumSection updatedEntity = CurriculumSection.builder().name("Updated Section A").build();
        updatedEntity.setId(id);

        CurriculumSectionResponse response = CurriculumSectionResponse.builder()
                .id(id)
                .name("Updated Section A")
                .build();

        when(curriculumSectionRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(curriculumSectionRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(curriculumSectionMapper.toResponse(updatedEntity)).thenReturn(response);

        CurriculumSectionResponse result = curriculumSectionService.updateCurriculumSection(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(curriculumSectionMapper).updateEntityFromRequest(request, existingEntity);
    }

    @Test
    void updateCurriculumSection_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        CurriculumSectionRequest request = new CurriculumSectionRequest();

        when(curriculumSectionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> curriculumSectionService.updateCurriculumSection(id, request));
    }

    @Test
    void updateCurriculumSection_shouldUpdateWithCurriculumCodeChange() {
        UUID id = UUID.randomUUID();
        UUID specializationId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        CurriculumSectionRequest request = new CurriculumSectionRequest();
        request.setName("Updated Section A");
        request.setCurriculumCode("CS2026");
        request.setCurriculumSpecializationId(specializationId);
        request.setCurriculumIntakeYearId(intakeYearId);

        CurriculumSection existingEntity = CurriculumSection.builder().name("Section A").build();
        existingEntity.setId(id);

        CurriculumId newCurriculumId = new CurriculumId("CS2026", specializationId, intakeYearId);
        Curriculum newCurriculum = Curriculum.builder()
                .id(newCurriculumId)
                .name("CS Curriculum 2026")
                .build();

        CurriculumSection updatedEntity = CurriculumSection.builder().name("Updated Section A").build();
        updatedEntity.setId(id);

        CurriculumSectionResponse response = CurriculumSectionResponse.builder()
                .id(id)
                .name("Updated Section A")
                .build();

        when(curriculumSectionRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(curriculumRepository.findById(newCurriculumId)).thenReturn(Optional.of(newCurriculum));
        when(curriculumSectionRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(curriculumSectionMapper.toResponse(updatedEntity)).thenReturn(response);

        CurriculumSectionResponse result = curriculumSectionService.updateCurriculumSection(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(curriculumRepository).findById(newCurriculumId);
    }

    // --- delete ---

    @Test
    void deleteCurriculumSection_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(curriculumSectionRepository.existsById(id)).thenReturn(true);

        curriculumSectionService.deleteCurriculumSection(id);

        verify(curriculumSectionRepository).deleteById(id);
    }

    @Test
    void deleteCurriculumSection_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(curriculumSectionRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> curriculumSectionService.deleteCurriculumSection(id));
        verify(curriculumSectionRepository, never()).deleteById(any());
    }
}
