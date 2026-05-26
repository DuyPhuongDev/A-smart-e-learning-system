package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.DepartmentRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DepartmentResponse;
import com.hcmut.lms.coursemanagement.application.mapper.DepartmentMapper;
import com.hcmut.lms.coursemanagement.domain.entity.department.Department;
import com.hcmut.lms.coursemanagement.domain.entity.faculty.Faculty;
import com.hcmut.lms.coursemanagement.repository.DepartmentRepository;
import com.hcmut.lms.coursemanagement.repository.FacultyRepository;
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
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    // --- create ---

    @Test
    void createDepartment_shouldReturnResponse_whenValidRequestWithFacultyId() {
        UUID facultyId = UUID.randomUUID();
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Software Engineering");
        request.setDescription("SE Department");
        request.setFacultyId(facultyId);

        UUID deptId = UUID.randomUUID();

        Faculty faculty = Faculty.builder().name("CS").code("CS").build();
        faculty.setId(facultyId);

        Department entity = Department.builder()
                .name("Software Engineering")
                .description("SE Department")
                .build();

        Department savedEntity = Department.builder()
                .name("Software Engineering")
                .description("SE Department")
                .build();
        savedEntity.setId(deptId);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(deptId)
                .name("Software Engineering")
                .facultyId(facultyId)
                .facultyName("CS")
                .build();

        when(departmentMapper.toEntity(any())).thenReturn(entity);
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));
        when(departmentRepository.save(entity)).thenReturn(savedEntity);
        when(departmentMapper.toResponse(savedEntity)).thenReturn(response);

        DepartmentResponse result = departmentService.createDepartment(request);

        assertNotNull(result);
        assertEquals(deptId, result.getId());
        assertEquals(facultyId, result.getFacultyId());
        verify(departmentMapper).toEntity(request);
        verify(facultyRepository).findById(facultyId);
        verify(departmentRepository).save(entity);
        verify(departmentMapper).toResponse(savedEntity);
    }

    @Test
    void createDepartment_shouldReturnResponse_whenValidRequestWithoutFacultyId() {
        DepartmentRequest request = new DepartmentRequest();
        request.setName("General Department");
        request.setDescription("General");
        request.setFacultyId(null);

        UUID deptId = UUID.randomUUID();

        Department entity = Department.builder()
                .name("General Department")
                .description("General")
                .build();

        Department savedEntity = Department.builder()
                .name("General Department")
                .description("General")
                .build();
        savedEntity.setId(deptId);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(deptId)
                .name("General Department")
                .build();

        when(departmentMapper.toEntity(any())).thenReturn(entity);
        when(departmentRepository.save(entity)).thenReturn(savedEntity);
        when(departmentMapper.toResponse(savedEntity)).thenReturn(response);

        DepartmentResponse result = departmentService.createDepartment(request);

        assertNotNull(result);
        assertEquals(deptId, result.getId());
        verify(facultyRepository, never()).findById(any());
    }

    @Test
    void createDepartment_shouldThrowException_whenFacultyNotFound() {
        UUID facultyId = UUID.randomUUID();
        DepartmentRequest request = new DepartmentRequest();
        request.setName("SE");
        request.setFacultyId(facultyId);

        Department entity = Department.builder().name("SE").build();

        when(departmentMapper.toEntity(any())).thenReturn(entity);
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> departmentService.createDepartment(request));
        verify(departmentRepository, never()).save(any());
    }

    // --- getById ---

    @Test
    void getDepartmentById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Department entity = Department.builder().name("SE").build();
        entity.setId(id);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("SE")
                .build();

        when(departmentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(departmentMapper.toResponse(entity)).thenReturn(response);

        DepartmentResponse result = departmentService.getDepartmentById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getDepartmentById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> departmentService.getDepartmentById(id));
    }

    // --- getAll ---

    @Test
    void getAllDepartments_shouldReturnList() {
        UUID id = UUID.randomUUID();
        Department entity = Department.builder().name("SE").build();
        entity.setId(id);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("SE")
                .build();

        when(departmentRepository.findAll()).thenReturn(List.of(entity));
        when(departmentMapper.toResponse(entity)).thenReturn(response);

        List<DepartmentResponse> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllDepartments_shouldReturnEmptyList_whenNone() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        List<DepartmentResponse> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getAll paginated ---

    @Test
    void getAllDepartmentsPaginated_shouldReturnPageResponse() {
        UUID id = UUID.randomUUID();
        Department entity = Department.builder().name("SE").build();
        entity.setId(id);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("SE")
                .build();

        Page<Department> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(departmentRepository.findAll(any(PageRequest.class))).thenReturn(entityPage);
        when(departmentMapper.toResponse(entity)).thenReturn(response);

        PageResponse<DepartmentResponse> result = departmentService.getAllDepartments(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // --- getByFacultyId ---

    @Test
    void getDepartmentsByFacultyId_shouldReturnList() {
        UUID facultyId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Department entity = Department.builder().name("SE").build();
        entity.setId(id);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("SE")
                .build();

        when(departmentRepository.findByFacultyId(facultyId)).thenReturn(List.of(entity));
        when(departmentMapper.toResponse(entity)).thenReturn(response);

        List<DepartmentResponse> result = departmentService.getDepartmentsByFacultyId(facultyId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDepartmentsByFacultyId_shouldReturnEmptyList_whenNone() {
        UUID facultyId = UUID.randomUUID();
        when(departmentRepository.findByFacultyId(facultyId)).thenReturn(List.of());

        List<DepartmentResponse> result = departmentService.getDepartmentsByFacultyId(facultyId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getByFacultyId paginated ---

    @Test
    void getDepartmentsByFacultyIdPaginated_shouldReturnPageResponse() {
        UUID facultyId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Department entity = Department.builder().name("SE").build();
        entity.setId(id);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("SE")
                .build();

        Page<Department> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(departmentRepository.findByFacultyId(eq(facultyId), any(PageRequest.class))).thenReturn(entityPage);
        when(departmentMapper.toResponse(entity)).thenReturn(response);

        PageResponse<DepartmentResponse> result = departmentService.getDepartmentsByFacultyId(facultyId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // --- getBySpecialization ---

    @Test
    void getDepartmentBySpecialization_shouldReturnResponse() {
        UUID specializationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Department entity = Department.builder().name("SE").build();
        entity.setId(id);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("SE")
                .build();

        when(departmentRepository.findBySpecializationId(specializationId)).thenReturn(entity);
        when(departmentMapper.toResponse(entity)).thenReturn(response);

        DepartmentResponse result = departmentService.getDepartmentBySpecialization(specializationId);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    // --- update ---

    @Test
    void updateDepartment_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Updated SE");
        request.setFacultyId(null);

        Department existingEntity = Department.builder().name("SE").build();
        existingEntity.setId(id);

        Department updatedEntity = Department.builder().name("Updated SE").build();
        updatedEntity.setId(id);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("Updated SE")
                .build();

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(departmentRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(departmentMapper.toResponse(updatedEntity)).thenReturn(response);

        DepartmentResponse result = departmentService.updateDepartment(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(departmentMapper).updateEntityFromRequest(request, existingEntity);
    }

    @Test
    void updateDepartment_shouldReturnUpdatedResponse_whenExistsWithFacultyId() {
        UUID id = UUID.randomUUID();
        UUID facultyId = UUID.randomUUID();
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Updated SE");
        request.setFacultyId(facultyId);

        Department existingEntity = Department.builder().name("SE").build();
        existingEntity.setId(id);

        Department updatedEntity = Department.builder().name("Updated SE").build();
        updatedEntity.setId(id);

        Faculty faculty = Faculty.builder().name("CS").code("CS").build();
        faculty.setId(facultyId);

        DepartmentResponse response = DepartmentResponse.builder()
                .id(id)
                .name("Updated SE")
                .facultyId(facultyId)
                .build();

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));
        when(departmentRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(departmentMapper.toResponse(updatedEntity)).thenReturn(response);

        DepartmentResponse result = departmentService.updateDepartment(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(facultyRepository).findById(facultyId);
    }

    @Test
    void updateDepartment_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        DepartmentRequest request = new DepartmentRequest();

        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> departmentService.updateDepartment(id, request));
    }

    // --- delete ---

    @Test
    void deleteDepartment_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.existsById(id)).thenReturn(true);

        departmentService.deleteDepartment(id);

        verify(departmentRepository).deleteById(id);
    }

    @Test
    void deleteDepartment_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> departmentService.deleteDepartment(id));
        verify(departmentRepository, never()).deleteById(any());
    }
}
