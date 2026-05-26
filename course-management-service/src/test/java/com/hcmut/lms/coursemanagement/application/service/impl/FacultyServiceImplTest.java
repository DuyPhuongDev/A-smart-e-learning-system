package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.FacultyRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.FacultyResponse;
import com.hcmut.lms.coursemanagement.application.mapper.FacultyMapper;
import com.hcmut.lms.coursemanagement.domain.entity.faculty.Faculty;
import com.hcmut.lms.coursemanagement.repository.FacultyRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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
class FacultyServiceImplTest {

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private FacultyMapper facultyMapper;

    @InjectMocks
    private FacultyServiceImpl facultyService;

    // --- create ---

    @Test
    void createFaculty_shouldReturnResponse_whenValidRequest() {
        FacultyRequest request = new FacultyRequest();
        request.setName("Computer Science");
        request.setCode("CS");
        request.setDescription("Faculty of Computer Science");

        UUID id = UUID.randomUUID();

        Faculty entity = Faculty.builder()
                .name("Computer Science")
                .code("CS")
                .description("Faculty of Computer Science")
                .build();

        Faculty savedEntity = Faculty.builder()
                .name("Computer Science")
                .code("CS")
                .description("Faculty of Computer Science")
                .build();
        savedEntity.setId(id);

        FacultyResponse response = FacultyResponse.builder()
                .id(id)
                .name("Computer Science")
                .code("CS")
                .build();

        when(facultyMapper.toEntity(any())).thenReturn(entity);
        when(facultyRepository.save(entity)).thenReturn(savedEntity);
        when(facultyMapper.toResponse(savedEntity)).thenReturn(response);

        FacultyResponse result = facultyService.createFaculty(request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("CS", result.getCode());
        verify(facultyMapper).toEntity(request);
        verify(facultyRepository).save(entity);
        verify(facultyMapper).toResponse(savedEntity);
    }

    @Test
    void createFaculty_shouldThrowException_whenDuplicateCode() {
        FacultyRequest request = new FacultyRequest();
        request.setName("Computer Science");
        request.setCode("CS");

        when(facultyRepository.existsByCode("CS")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> facultyService.createFaculty(request));
        verify(facultyRepository, never()).save(any());
    }

    // --- getById ---

    @Test
    void getFacultyById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Faculty entity = Faculty.builder().name("CS").code("CS").build();
        entity.setId(id);

        FacultyResponse response = FacultyResponse.builder()
                .id(id)
                .name("CS")
                .code("CS")
                .build();

        when(facultyRepository.findById(id)).thenReturn(Optional.of(entity));
        when(facultyMapper.toResponse(entity)).thenReturn(response);

        FacultyResponse result = facultyService.getFacultyById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getFacultyById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(facultyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> facultyService.getFacultyById(id));
    }

    // --- getByCode ---

    @Test
    void getFacultyByCode_shouldReturnResponse_whenExists() {
        String code = "CS";
        UUID id = UUID.randomUUID();
        Faculty entity = Faculty.builder().name("Computer Science").code(code).build();
        entity.setId(id);

        FacultyResponse response = FacultyResponse.builder()
                .id(id)
                .name("Computer Science")
                .code(code)
                .build();

        when(facultyRepository.findByCode(code)).thenReturn(Optional.of(entity));
        when(facultyMapper.toResponse(entity)).thenReturn(response);

        FacultyResponse result = facultyService.getFacultyByCode(code);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(code, result.getCode());
    }

    @Test
    void getFacultyByCode_shouldThrowException_whenNotFound() {
        String code = "NONEXISTENT";
        when(facultyRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> facultyService.getFacultyByCode(code));
    }

    // --- getAll ---

    @Test
    void getAllFaculties_shouldReturnList() {
        UUID id = UUID.randomUUID();
        Faculty entity = Faculty.builder().name("CS").code("CS").build();
        entity.setId(id);

        FacultyResponse response = FacultyResponse.builder()
                .id(id)
                .name("CS")
                .code("CS")
                .build();

        when(facultyRepository.findAll()).thenReturn(List.of(entity));
        when(facultyMapper.toResponse(entity)).thenReturn(response);

        List<FacultyResponse> result = facultyService.getAllFaculties();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllFaculties_shouldReturnEmptyList_whenNone() {
        when(facultyRepository.findAll()).thenReturn(List.of());

        List<FacultyResponse> result = facultyService.getAllFaculties();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getAll paginated ---

    @Test
    void getAllFacultiesPaginated_shouldReturnPageResponse() {
        UUID id = UUID.randomUUID();
        Faculty entity = Faculty.builder().name("CS").code("CS").build();
        entity.setId(id);

        FacultyResponse response = FacultyResponse.builder()
                .id(id)
                .name("CS")
                .code("CS")
                .build();

        Page<Faculty> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(facultyRepository.findAll(any(PageRequest.class))).thenReturn(entityPage);
        when(facultyMapper.toResponse(entity)).thenReturn(response);

        PageResponse<FacultyResponse> result = facultyService.getAllFaculties(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // --- update ---

    @Test
    void updateFaculty_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        FacultyRequest request = new FacultyRequest();
        request.setName("Updated CS");
        request.setCode("CS"); // same code as entity -> no duplicate check triggers

        Faculty existingEntity = Faculty.builder().name("CS").code("CS").build();
        existingEntity.setId(id);

        Faculty updatedEntity = Faculty.builder().name("Updated CS").code("CS").build();
        updatedEntity.setId(id);

        FacultyResponse response = FacultyResponse.builder()
                .id(id)
                .name("Updated CS")
                .code("CS")
                .build();

        when(facultyRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(facultyRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(facultyMapper.toResponse(updatedEntity)).thenReturn(response);

        FacultyResponse result = facultyService.updateFaculty(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(facultyMapper).updateEntityFromRequest(request, existingEntity);
    }

    @Test
    void updateFaculty_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        FacultyRequest request = new FacultyRequest();

        when(facultyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> facultyService.updateFaculty(id, request));
    }

    @Test
    void updateFaculty_shouldThrowException_whenDuplicateCodeOnDifferentCode() {
        UUID id = UUID.randomUUID();
        FacultyRequest request = new FacultyRequest();
        request.setCode("NEWCODE");

        Faculty existingEntity = Faculty.builder().name("CS").code("CS").build();
        existingEntity.setId(id);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(facultyRepository.existsByCode("NEWCODE")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> facultyService.updateFaculty(id, request));
        verify(facultyRepository, never()).save(any());
    }

    // --- delete ---

    @Test
    void deleteFaculty_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(facultyRepository.existsById(id)).thenReturn(true);

        facultyService.deleteFaculty(id);

        verify(facultyRepository).deleteById(id);
    }

    @Test
    void deleteFaculty_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(facultyRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> facultyService.deleteFaculty(id));
        verify(facultyRepository, never()).deleteById(any());
    }
}
