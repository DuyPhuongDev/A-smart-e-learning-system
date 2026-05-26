package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SpecializationRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SpecializationResponse;
import com.hcmut.lms.coursemanagement.application.mapper.SpecializationMapper;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.department.Department;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.repository.DepartmentRepository;
import com.hcmut.lms.coursemanagement.repository.SpecializationRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecializationServiceImplTest {

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private SpecializationMapper specializationMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private SpecializationServiceImpl specializationService;

    // --- createSpecialization ---

    @Test
    void createSpecialization_shouldReturnResponse_whenValidRequest() {
        SpecializationRequest request = createRequest("CS101", "Computer Science", "desc", UUID.randomUUID());
        Department department = createDepartment(request.getDepartmentId());
        Specialization entity = createEntity("CS101", "Computer Science");
        Specialization savedEntity = createEntityWithId(UUID.randomUUID(), "CS101", "Computer Science");
        SpecializationResponse expectedResponse = createResponse(savedEntity.getId(), "CS101", "Computer Science");

        when(specializationRepository.existsByCode("CS101")).thenReturn(false);
        when(specializationMapper.toEntity(request)).thenReturn(entity);
        when(departmentRepository.findById(request.getDepartmentId())).thenReturn(Optional.of(department));
        when(specializationRepository.save(entity)).thenReturn(savedEntity);
        when(specializationMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        SpecializationResponse result = specializationService.createSpecialization(request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.getId()).isEqualTo(savedEntity.getId());
        verify(specializationRepository).save(entity);
    }

    @Test
    void createSpecialization_shouldThrowException_whenDuplicateCode() {
        SpecializationRequest request = createRequest("CS101", "Computer Science", null, UUID.randomUUID());
        when(specializationRepository.existsByCode("CS101")).thenReturn(true);

        assertThatThrownBy(() -> specializationService.createSpecialization(request))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("already exists");
        verify(specializationRepository, never()).save(any());
    }

    @Test
    void createSpecialization_shouldThrowException_whenDepartmentNotFound() {
        SpecializationRequest request = createRequest("CS101", "Computer Science", null, UUID.randomUUID());
        Specialization entity = createEntity("CS101", "Computer Science");

        when(specializationRepository.existsByCode("CS101")).thenReturn(false);
        when(specializationMapper.toEntity(request)).thenReturn(entity);
        when(departmentRepository.findById(request.getDepartmentId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationService.createSpecialization(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Department not found");
        verify(specializationRepository, never()).save(any());
    }

    // --- updateSpecialization ---

    @Test
    void updateSpecialization_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        SpecializationRequest request = createRequest("CS102", "Updated CS", null, UUID.randomUUID());
        Specialization existing = createEntityWithId(id, "CS101", "Computer Science");
        Department department = createDepartment(request.getDepartmentId());
        Specialization updatedEntity = createEntityWithId(id, "CS102", "Updated CS");
        updatedEntity.setDepartment(department);
        SpecializationResponse expectedResponse = createResponse(id, "CS102", "Updated CS");

        when(specializationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(specializationRepository.existsByCode("CS102")).thenReturn(false);
        when(departmentRepository.findById(request.getDepartmentId())).thenReturn(Optional.of(department));
        when(specializationRepository.save(existing)).thenReturn(updatedEntity);
        when(specializationMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);

        SpecializationResponse result = specializationService.updateSpecialization(id, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(specializationMapper).updateEntityFromRequest(request, existing);
    }

    @Test
    void updateSpecialization_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        SpecializationRequest request = createRequest("CS102", "CS", null, UUID.randomUUID());

        when(specializationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationService.updateSpecialization(id, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Specialization not found");
    }

    @Test
    void updateSpecialization_shouldThrowException_whenDuplicateCode() {
        UUID id = UUID.randomUUID();
        SpecializationRequest request = createRequest("CS102", "CS", null, null);
        Specialization existing = createEntityWithId(id, "CS101", "Computer Science");

        when(specializationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(specializationRepository.existsByCode("CS102")).thenReturn(true);

        assertThatThrownBy(() -> specializationService.updateSpecialization(id, request))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("already exists");
        verify(specializationRepository, never()).save(any());
    }

    // --- getSpecializationById ---

    @Test
    void getSpecializationById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Specialization entity = createEntityWithId(id, "CS101", "Computer Science");
        SpecializationResponse expectedResponse = createResponse(id, "CS101", "Computer Science");

        when(specializationRepository.findById(id)).thenReturn(Optional.of(entity));
        when(specializationMapper.toResponse(entity)).thenReturn(expectedResponse);

        SpecializationResponse result = specializationService.getSpecializationById(id);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getSpecializationById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(specializationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationService.getSpecializationById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Specialization not found");
    }

    // --- getSpecializationByCode ---

    @Test
    void getSpecializationByCode_shouldReturnResponse_whenExists() {
        String code = "CS101";
        Specialization entity = createEntityWithId(UUID.randomUUID(), code, "Computer Science");
        SpecializationResponse expectedResponse = createResponse(entity.getId(), code, "Computer Science");

        when(specializationRepository.findByCode(code)).thenReturn(Optional.of(entity));
        when(specializationMapper.toResponse(entity)).thenReturn(expectedResponse);

        SpecializationResponse result = specializationService.getSpecializationByCode(code);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getSpecializationByCode_shouldThrowException_whenNotFound() {
        String code = "NONEXISTENT";
        when(specializationRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationService.getSpecializationByCode(code))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Specialization not found");
    }

    // --- getAllSpecializations ---

    @Test
    void getAllSpecializations_shouldReturnList_whenDataExists() {
        Specialization e1 = createEntityWithId(UUID.randomUUID(), "CS101", "CS");
        Specialization e2 = createEntityWithId(UUID.randomUUID(), "EE101", "EE");
        SpecializationResponse r1 = createResponse(e1.getId(), "CS101", "CS");
        SpecializationResponse r2 = createResponse(e2.getId(), "EE101", "EE");

        when(specializationRepository.findAll()).thenReturn(List.of(e1, e2));
        when(specializationMapper.toResponse(e1)).thenReturn(r1);
        when(specializationMapper.toResponse(e2)).thenReturn(r2);

        List<SpecializationResponse> result = specializationService.getAllSpecializations();

        assertThat(result).hasSize(2).containsExactly(r1, r2);
    }

    @Test
    void getAllSpecializations_shouldReturnEmptyList_whenNoData() {
        when(specializationRepository.findAll()).thenReturn(List.of());

        List<SpecializationResponse> result = specializationService.getAllSpecializations();

        assertThat(result).isEmpty();
    }

    // --- getAllSpecializations paginated ---

    @Test
    void getAllSpecializationsPaginated_shouldReturnPageResponse() {
        Specialization e1 = createEntityWithId(UUID.randomUUID(), "CS101", "CS");
        SpecializationResponse r1 = createResponse(e1.getId(), "CS101", "CS");
        Page<Specialization> page = new PageImpl<>(List.of(e1), PageRequest.of(0, 10), 1);

        when(specializationRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(specializationMapper.toResponse(e1)).thenReturn(r1);

        PageResponse<SpecializationResponse> result = specializationService.getAllSpecializations(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPageNumber()).isZero();
    }

    // --- getSpecializationsByDepartmentId ---

    @Test
    void getSpecializationsByDepartmentId_shouldReturnList() {
        UUID deptId = UUID.randomUUID();
        Specialization e1 = createEntityWithId(UUID.randomUUID(), "CS101", "CS");
        SpecializationResponse r1 = createResponse(e1.getId(), "CS101", "CS");

        when(specializationRepository.findByDepartmentId(deptId)).thenReturn(List.of(e1));
        when(specializationMapper.toResponse(e1)).thenReturn(r1);

        List<SpecializationResponse> result = specializationService.getSpecializationsByDepartmentId(deptId);

        assertThat(result).hasSize(1).containsExactly(r1);
    }

    // --- getSpecializationsByDepartmentId paginated ---

    @Test
    void getSpecializationsByDepartmentIdPaginated_shouldReturnPageResponse() {
        UUID deptId = UUID.randomUUID();
        Specialization e1 = createEntityWithId(UUID.randomUUID(), "CS101", "CS");
        SpecializationResponse r1 = createResponse(e1.getId(), "CS101", "CS");
        Page<Specialization> page = new PageImpl<>(List.of(e1), PageRequest.of(0, 10), 1);

        when(specializationRepository.findByDepartmentId(eq(deptId), any(PageRequest.class))).thenReturn(page);
        when(specializationMapper.toResponse(e1)).thenReturn(r1);

        PageResponse<SpecializationResponse> result = specializationService.getSpecializationsByDepartmentId(deptId, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // --- deleteSpecialization ---

    @Test
    void deleteSpecialization_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(specializationRepository.existsById(id)).thenReturn(true);

        specializationService.deleteSpecialization(id);

        verify(specializationRepository).deleteById(id);
    }

    @Test
    void deleteSpecialization_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(specializationRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> specializationService.deleteSpecialization(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Specialization not found");
        verify(specializationRepository, never()).deleteById(any());
    }

    // --- getListSpecializationOptionByMe ---

    @Test
    void getListSpecializationOptionByMe_shouldReturnList_whenStudentExists() {
        UUID studentId = UUID.randomUUID();
        UUID deptId = UUID.randomUUID();
        UserResponse user = new UserResponse();
        user.setId(studentId);
        user.setDepartmentId(deptId);

        Specialization e1 = createEntityWithId(UUID.randomUUID(), "CS101", "CS");
        SpecializationResponse r1 = createResponse(e1.getId(), "CS101", "CS");

        when(userServiceClient.getUserById(studentId)).thenReturn(user);
        when(specializationRepository.findByDepartmentId(deptId)).thenReturn(List.of(e1));
        when(specializationMapper.toResponse(e1)).thenReturn(r1);

        List<SpecializationResponse> result = specializationService.getListSpecializationOptionByMe(studentId);

        assertThat(result).hasSize(1).containsExactly(r1);
    }

    @Test
    void getListSpecializationOptionByMe_shouldReturnEmpty_whenNoSpecializationsForDepartment() {
        UUID studentId = UUID.randomUUID();
        UUID deptId = UUID.randomUUID();
        UserResponse user = new UserResponse();
        user.setId(studentId);
        user.setDepartmentId(deptId);

        when(userServiceClient.getUserById(studentId)).thenReturn(user);
        when(specializationRepository.findByDepartmentId(deptId)).thenReturn(List.of());

        List<SpecializationResponse> result = specializationService.getListSpecializationOptionByMe(studentId);

        assertThat(result).isEmpty();
    }

    // --- helper methods ---

    private SpecializationRequest createRequest(String code, String name, String description, UUID departmentId) {
        SpecializationRequest request = new SpecializationRequest();
        request.setCode(code);
        request.setName(name);
        request.setDescription(description);
        request.setDepartmentId(departmentId);
        return request;
    }

    private Department createDepartment(UUID id) {
        Department department = new Department();
        department.setId(id);
        department.setName("Dept-" + id.toString().substring(0, 8));
        return department;
    }

    private Specialization createEntity(String code, String name) {
        Specialization entity = new Specialization();
        entity.setCode(code);
        entity.setName(name);
        return entity;
    }

    private Specialization createEntityWithId(UUID id, String code, String name) {
        Specialization entity = createEntity(code, name);
        entity.setId(id);
        return entity;
    }

    private SpecializationResponse createResponse(UUID id, String code, String name) {
        return SpecializationResponse.builder()
                .id(id)
                .code(code)
                .name(name)
                .build();
    }
}
