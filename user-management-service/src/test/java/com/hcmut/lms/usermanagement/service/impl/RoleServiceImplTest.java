package com.hcmut.lms.usermanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hcmut.lms.usermanagement.exception.DuplicateResourceException;
import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.RoleMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponse;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void create_shouldReturnResponse_whenValidRequest() {
        CreateRoleRequest request = createRoleRequest("STUDENT", "Student role");
        Role role = createRole("STUDENT", "Student role");
        Role savedRole = createRole("STUDENT", "Student role");
        RoleResponse response = createRoleResponse(savedRole.getId(), "STUDENT", "Student role");

        when(roleRepository.existsByName("STUDENT")).thenReturn(false);
        when(roleMapper.toEntity(request)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(savedRole);
        when(roleMapper.toResponse(savedRole)).thenReturn(response);

        RoleResponse result = roleService.create(request);

        assertEquals(response, result);
        verify(roleRepository).existsByName("STUDENT");
        verify(roleMapper).toEntity(request);
        verify(roleRepository).save(role);
        verify(roleMapper).toResponse(savedRole);
    }

    @Test
    void create_shouldThrowException_whenDuplicateName() {
        CreateRoleRequest request = createRoleRequest("ADMIN", "Admin role");
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> roleService.create(request));
        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Role role = createRole("ADMIN", "Admin role");
        RoleResponse response = createRoleResponse(id, "ADMIN", "Admin role");

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleMapper.toResponse(role)).thenReturn(response);

        RoleResponse result = roleService.getById(id);

        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getById(id));
    }

    @Test
    void getAll_shouldReturnList() {
        Role role = createRole("ADMIN", "Admin role");
        RoleResponse response = createRoleResponse(role.getId(), "ADMIN", "Admin role");

        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toResponse(role)).thenReturn(response);

        List<RoleResponse> result = roleService.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNone() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoleResponse> result = roleService.getAll();

        assertEquals(0, result.size());
    }

    @Test
    void update_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UpdateRoleRequest request = updateRoleRequest("TEACHER_UPDATED", "Updated desc");
        Role existing = createRole("TEACHER", "Old desc");
        Role saved = createRole("TEACHER_UPDATED", "Updated desc");
        RoleResponse response = createRoleResponse(id, "TEACHER_UPDATED", "Updated desc");

        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.existsByName("TEACHER_UPDATED")).thenReturn(false);
        when(roleRepository.save(existing)).thenReturn(saved);
        when(roleMapper.toResponse(saved)).thenReturn(response);

        RoleResponse result = roleService.update(id, request);

        assertEquals(response, result);
        verify(roleMapper).updateEntity(request, existing);
    }

    @Test
    void update_shouldThrowException_whenDuplicateNameChanged() {
        UUID id = UUID.randomUUID();
        UpdateRoleRequest request = updateRoleRequest("EXISTING", "desc");
        Role existing = createRole("ORIGINAL", "desc");

        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.existsByName("EXISTING")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> roleService.update(id, request));
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        UpdateRoleRequest request = updateRoleRequest("NAME", "desc");

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.update(id, request));
    }

    @Test
    void delete_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        Role role = createRole("ADMIN", "desc");
        role.setUsers(new HashSet<>());

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        roleService.delete(id);

        verify(roleRepository).delete(role);
    }

    @Test
    void delete_shouldThrowException_whenHasUsers() {
        UUID id = UUID.randomUUID();
        Role role = createRole("ADMIN", "desc");
        role.setUsers(new HashSet<>(List.of(new User())));

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        assertThrows(RuntimeException.class, () -> roleService.delete(id));
        verify(roleRepository, never()).delete(any());
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.delete(id));
    }

    private CreateRoleRequest createRoleRequest(String name, String description) {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName(name);
        request.setDescription(description);
        request.setIsActive(true);
        return request;
    }

    private UpdateRoleRequest updateRoleRequest(String name, String description) {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(name);
        role.setDescription(description);
        role.setIsActive(true);
        role.setUsers(new HashSet<>());
        role.setRolesServiceFunctions(new HashSet<>());
        return role;
    }

    private RoleResponse createRoleResponse(UUID id, String name, String description) {
        RoleResponse response = new RoleResponse();
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        response.setIsActive(true);
        return response;
    }
}
