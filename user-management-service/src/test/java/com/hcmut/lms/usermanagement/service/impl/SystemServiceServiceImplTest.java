package com.hcmut.lms.usermanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.SystemServiceMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.response.SystemServiceResponse;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.SystemService;
import com.hcmut.lms.usermanagement.repository.SystemServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemServiceServiceImplTest {

    @Mock
    private SystemServiceRepository systemServiceRepository;

    @Mock
    private SystemServiceMapper systemServiceMapper;

    @InjectMocks
    private SystemServiceServiceImpl systemServiceService;

    @Test
    void create_shouldReturnResponse_whenValidRequest() {
        CreateSystemServiceRequest request = createSystemServiceRequest("LMS Core", "Core service");
        SystemService entity = createSystemService("LMS Core", "Core service");
        SystemService saved = createSystemService("LMS Core", "Core service");
        SystemServiceResponse response = createSystemServiceResponse(saved.getId(), "LMS Core", "Core service");

        when(systemServiceMapper.toEntity(request)).thenReturn(entity);
        when(systemServiceRepository.save(entity)).thenReturn(saved);
        when(systemServiceMapper.toResponse(saved)).thenReturn(response);

        SystemServiceResponse result = systemServiceService.create(request);

        assertEquals(response, result);
        verify(systemServiceMapper).toEntity(request);
        verify(systemServiceRepository).save(entity);
        verify(systemServiceMapper).toResponse(saved);
    }

    @Test
    void getById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        SystemService entity = createSystemService("LMS Core", "desc");
        SystemServiceResponse response = createSystemServiceResponse(id, "LMS Core", "desc");

        when(systemServiceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(systemServiceMapper.toResponse(entity)).thenReturn(response);

        SystemServiceResponse result = systemServiceService.getById(id);

        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(systemServiceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> systemServiceService.getById(id));
    }

    @Test
    void getAll_shouldReturnList() {
        SystemService entity = createSystemService("LMS Core", "desc");
        SystemServiceResponse response = createSystemServiceResponse(entity.getId(), "LMS Core", "desc");

        when(systemServiceRepository.findAll()).thenReturn(List.of(entity));
        when(systemServiceMapper.toResponse(entity)).thenReturn(response);

        List<SystemServiceResponse> result = systemServiceService.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNone() {
        when(systemServiceRepository.findAll()).thenReturn(Collections.emptyList());

        List<SystemServiceResponse> result = systemServiceService.getAll();

        assertEquals(0, result.size());
    }

    @Test
    void update_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UpdateSystemServiceRequest request = updateSystemServiceRequest("Updated", "Updated desc");
        SystemService existing = createSystemService("Original", "Old desc");
        SystemService saved = createSystemService("Updated", "Updated desc");
        SystemServiceResponse response = createSystemServiceResponse(id, "Updated", "Updated desc");

        when(systemServiceRepository.findById(id)).thenReturn(Optional.of(existing));
        when(systemServiceRepository.save(existing)).thenReturn(saved);
        when(systemServiceMapper.toResponse(saved)).thenReturn(response);

        SystemServiceResponse result = systemServiceService.update(id, request);

        assertEquals(response, result);
        verify(systemServiceMapper).updateEntity(request, existing);
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(systemServiceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> systemServiceService.update(id, updateSystemServiceRequest("Name", "desc")));
    }

    @Test
    void delete_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        SystemService entity = createSystemService("LMS Core", "desc");
        entity.setServiceFunctions(new HashSet<>());

        when(systemServiceRepository.findById(id)).thenReturn(Optional.of(entity));

        systemServiceService.delete(id);

        verify(systemServiceRepository).delete(entity);
    }

    @Test
    void delete_shouldThrowException_whenHasServiceFunctions() {
        UUID id = UUID.randomUUID();
        SystemService entity = createSystemService("LMS Core", "desc");
        entity.setServiceFunctions(new HashSet<>(List.of(new ServiceFunction())));

        when(systemServiceRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(RuntimeException.class, () -> systemServiceService.delete(id));
        verify(systemServiceRepository, never()).delete(any());
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(systemServiceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> systemServiceService.delete(id));
    }

    private CreateSystemServiceRequest createSystemServiceRequest(String name, String description) {
        CreateSystemServiceRequest request = new CreateSystemServiceRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    private UpdateSystemServiceRequest updateSystemServiceRequest(String name, String description) {
        UpdateSystemServiceRequest request = new UpdateSystemServiceRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    private SystemService createSystemService(String name, String description) {
        SystemService entity = new SystemService();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setDescription(description);
        entity.setServiceFunctions(new HashSet<>());
        return entity;
    }

    private SystemServiceResponse createSystemServiceResponse(UUID id, String name, String description) {
        SystemServiceResponse response = new SystemServiceResponse();
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        return response;
    }
}
