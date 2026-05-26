package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.AcademicYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.AcademicYearResponse;
import com.hcmut.lms.coursemanagement.application.mapper.AcademicYearMapper;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AcademicYearServiceImplTest {

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private AcademicYearMapper academicYearMapper;

    @InjectMocks
    private AcademicYearServiceImpl academicYearService;

    // --- create ---

    @Test
    void createAcademicYear_shouldReturnResponse_whenValidRequest() {
        AcademicYearRequest request = new AcademicYearRequest();
        request.setYearCode("2025-2026");
        request.setStartDate(LocalDate.of(2025, 9, 1));
        request.setEndDate(LocalDate.of(2026, 7, 31));

        UUID id = UUID.randomUUID();

        AcademicYear entity = AcademicYear.builder()
                .yearCode("2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        AcademicYear savedEntity = AcademicYear.builder()
                .yearCode("2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();
        savedEntity.setId(id);

        AcademicYearResponse response = AcademicYearResponse.builder()
                .id(id)
                .yearCode("2025-2026")
                .build();

        when(academicYearMapper.toEntity(any())).thenReturn(entity);
        when(academicYearRepository.save(entity)).thenReturn(savedEntity);
        when(academicYearMapper.toResponse(savedEntity)).thenReturn(response);

        AcademicYearResponse result = academicYearService.createAcademicYear(request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(academicYearMapper).toEntity(request);
        verify(academicYearRepository).save(entity);
        verify(academicYearMapper).toResponse(savedEntity);
    }

    // --- getById ---

    @Test
    void getAcademicYearById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        AcademicYear entity = AcademicYear.builder().yearCode("2025-2026").build();
        entity.setId(id);

        AcademicYearResponse response = AcademicYearResponse.builder()
                .id(id)
                .yearCode("2025-2026")
                .build();

        when(academicYearRepository.findById(id)).thenReturn(Optional.of(entity));
        when(academicYearMapper.toResponse(entity)).thenReturn(response);

        AcademicYearResponse result = academicYearService.getAcademicYearById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getAcademicYearById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(academicYearRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> academicYearService.getAcademicYearById(id));
    }

    // --- getAll ---

    @Test
    void getAllAcademicYears_shouldReturnList() {
        UUID id = UUID.randomUUID();
        AcademicYear entity = AcademicYear.builder().yearCode("2025-2026").build();
        entity.setId(id);

        AcademicYearResponse response = AcademicYearResponse.builder()
                .id(id)
                .yearCode("2025-2026")
                .build();

        when(academicYearRepository.findAll()).thenReturn(List.of(entity));
        when(academicYearMapper.toResponse(entity)).thenReturn(response);

        List<AcademicYearResponse> result = academicYearService.getAllAcademicYears();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(id, result.get(0).getId());
    }

    @Test
    void getAllAcademicYears_shouldReturnEmptyList_whenNone() {
        when(academicYearRepository.findAll()).thenReturn(List.of());

        List<AcademicYearResponse> result = academicYearService.getAllAcademicYears();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getAll paginated ---

    @Test
    void getAllAcademicYearsPaginated_shouldReturnPageResponse() {
        UUID id = UUID.randomUUID();
        AcademicYear entity = AcademicYear.builder().yearCode("2025-2026").build();
        entity.setId(id);

        AcademicYearResponse response = AcademicYearResponse.builder()
                .id(id)
                .yearCode("2025-2026")
                .build();

        Page<AcademicYear> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(academicYearRepository.findAll(any(PageRequest.class))).thenReturn(entityPage);
        when(academicYearMapper.toResponse(entity)).thenReturn(response);

        PageResponse<AcademicYearResponse> result = academicYearService.getAllAcademicYears(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(id, result.getContent().get(0).getId());
    }

    // --- update ---

    @Test
    void updateAcademicYear_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        AcademicYearRequest request = new AcademicYearRequest();
        request.setYearCode("2026-2027");

        AcademicYear existingEntity = AcademicYear.builder().yearCode("2025-2026").build();
        existingEntity.setId(id);

        AcademicYear updatedEntity = AcademicYear.builder().yearCode("2026-2027").build();
        updatedEntity.setId(id);

        AcademicYearResponse response = AcademicYearResponse.builder()
                .id(id)
                .yearCode("2026-2027")
                .build();

        when(academicYearRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(academicYearRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(academicYearMapper.toResponse(updatedEntity)).thenReturn(response);

        AcademicYearResponse result = academicYearService.updateAcademicYear(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("2026-2027", result.getYearCode());
        verify(academicYearMapper).updateEntityFromRequest(request, existingEntity);
    }

    @Test
    void updateAcademicYear_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        AcademicYearRequest request = new AcademicYearRequest();

        when(academicYearRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> academicYearService.updateAcademicYear(id, request));
    }

    // --- delete ---

    @Test
    void deleteAcademicYear_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(academicYearRepository.existsById(id)).thenReturn(true);

        academicYearService.deleteAcademicYear(id);

        verify(academicYearRepository).deleteById(id);
    }

    @Test
    void deleteAcademicYear_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(academicYearRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> academicYearService.deleteAcademicYear(id));
        verify(academicYearRepository, never()).deleteById(any());
    }
}
