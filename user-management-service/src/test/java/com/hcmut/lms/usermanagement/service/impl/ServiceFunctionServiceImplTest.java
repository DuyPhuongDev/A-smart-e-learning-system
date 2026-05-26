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
import com.hcmut.lms.usermanagement.mapper.ServiceFunctionMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.ServiceFunctionResponse;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.SystemService;
import com.hcmut.lms.usermanagement.model.entity.UrlPermission;
import com.hcmut.lms.usermanagement.repository.ServiceFunctionRepository;
import com.hcmut.lms.usermanagement.repository.SystemServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceFunctionServiceImplTest {

    @Mock
    private ServiceFunctionRepository serviceFunctionRepository;

    @Mock
    private SystemServiceRepository systemServiceRepository;

    @Mock
    private ServiceFunctionMapper serviceFunctionMapper;

    @InjectMocks
    private ServiceFunctionServiceImpl serviceFunctionService;

    @Test
    void create_shouldReturnResponse_whenValidRequest() {
        UUID systemServiceId = UUID.randomUUID();
        CreateServiceFunctionRequest request = createServiceFunctionRequest("fn", "desc", systemServiceId);
        SystemService systemService = createSystemService(systemServiceId, "System", "desc");
        ServiceFunction entity = createServiceFunction("fn", "desc", systemService);
        ServiceFunction saved = createServiceFunction("fn", "desc", systemService);
        ServiceFunctionResponse response = createServiceFunctionResponse(saved.getId(), "fn", "desc", systemServiceId, "System");

        when(systemServiceRepository.findById(systemServiceId)).thenReturn(Optional.of(systemService));
        when(serviceFunctionMapper.toEntity(request)).thenReturn(entity);
        when(serviceFunctionRepository.save(entity)).thenReturn(saved);
        when(serviceFunctionMapper.toResponse(saved)).thenReturn(response);

        ServiceFunctionResponse result = serviceFunctionService.create(request);

        assertEquals(response, result);
        verify(serviceFunctionRepository).save(entity);
    }

    @Test
    void create_shouldThrowException_whenSystemServiceNotFound() {
        UUID systemServiceId = UUID.randomUUID();
        CreateServiceFunctionRequest request = createServiceFunctionRequest("fn", "desc", systemServiceId);

        when(systemServiceRepository.findById(systemServiceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceFunctionService.create(request));
    }

    @Test
    void getById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        ServiceFunction entity = createServiceFunction("fn", "desc", null);
        ServiceFunctionResponse response = createServiceFunctionResponse(id, "fn", "desc", UUID.randomUUID(), "System");

        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(serviceFunctionMapper.toResponse(entity)).thenReturn(response);

        ServiceFunctionResponse result = serviceFunctionService.getById(id);

        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceFunctionService.getById(id));
    }

    @Test
    void getAll_shouldReturnList() {
        ServiceFunction entity = createServiceFunction("fn", "desc", null);
        ServiceFunctionResponse response = createServiceFunctionResponse(entity.getId(), "fn", "desc", UUID.randomUUID(), "System");

        when(serviceFunctionRepository.findAll()).thenReturn(List.of(entity));
        when(serviceFunctionMapper.toResponse(entity)).thenReturn(response);

        List<ServiceFunctionResponse> result = serviceFunctionService.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNone() {
        when(serviceFunctionRepository.findAll()).thenReturn(Collections.emptyList());

        List<ServiceFunctionResponse> result = serviceFunctionService.getAll();

        assertEquals(0, result.size());
    }

    @Test
    void getBySystemServiceId_shouldReturnList() {
        UUID systemServiceId = UUID.randomUUID();
        ServiceFunction entity = createServiceFunction("fn", "desc", null);
        ServiceFunctionResponse response = createServiceFunctionResponse(entity.getId(), "fn", "desc", systemServiceId, "System");

        when(serviceFunctionRepository.findBySystemServiceId(systemServiceId)).thenReturn(List.of(entity));
        when(serviceFunctionMapper.toResponse(entity)).thenReturn(response);

        List<ServiceFunctionResponse> result = serviceFunctionService.getBySystemServiceId(systemServiceId);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getBySystemServiceId_shouldReturnEmptyList_whenNone() {
        UUID systemServiceId = UUID.randomUUID();
        when(serviceFunctionRepository.findBySystemServiceId(systemServiceId)).thenReturn(Collections.emptyList());

        List<ServiceFunctionResponse> result = serviceFunctionService.getBySystemServiceId(systemServiceId);

        assertEquals(0, result.size());
    }

    @Test
    void update_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UpdateServiceFunctionRequest request = updateServiceFunctionRequest("Updated", "desc", null);
        ServiceFunction existing = createServiceFunction("Original", "desc", null);
        ServiceFunction saved = createServiceFunction("Updated", "desc", null);
        ServiceFunctionResponse response = createServiceFunctionResponse(id, "Updated", "desc", UUID.randomUUID(), "System");

        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(serviceFunctionRepository.save(existing)).thenReturn(saved);
        when(serviceFunctionMapper.toResponse(saved)).thenReturn(response);

        ServiceFunctionResponse result = serviceFunctionService.update(id, request);

        assertEquals(response, result);
        verify(serviceFunctionMapper).updateEntity(request, existing);
    }

    @Test
    void update_shouldUpdateSystemService_whenSystemServiceIdProvided() {
        UUID id = UUID.randomUUID();
        UUID newSystemServiceId = UUID.randomUUID();
        UpdateServiceFunctionRequest request = updateServiceFunctionRequest("fn", "desc", newSystemServiceId);
        ServiceFunction existing = createServiceFunction("fn", "desc", null);
        SystemService newSystemService = createSystemService(newSystemServiceId, "NewSystem", "desc");
        ServiceFunctionResponse response = createServiceFunctionResponse(id, "fn", "desc", newSystemServiceId, "NewSystem");

        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(systemServiceRepository.findById(newSystemServiceId)).thenReturn(Optional.of(newSystemService));
        when(serviceFunctionRepository.save(existing)).thenReturn(existing);
        when(serviceFunctionMapper.toResponse(existing)).thenReturn(response);

        ServiceFunctionResponse result = serviceFunctionService.update(id, request);

        assertEquals(response, result);
        verify(serviceFunctionMapper).updateEntity(request, existing);
        verify(serviceFunctionRepository).save(existing);
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceFunctionService.update(id, updateServiceFunctionRequest("fn", "desc", null)));
    }

    @Test
    void delete_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        ServiceFunction entity = createServiceFunction("fn", "desc", null);
        entity.setUrlPermissions(new HashSet<>());

        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.of(entity));

        serviceFunctionService.delete(id);

        verify(serviceFunctionRepository).delete(entity);
    }

    @Test
    void delete_shouldThrowException_whenHasUrlPermissions() {
        UUID id = UUID.randomUUID();
        ServiceFunction entity = createServiceFunction("fn", "desc", null);
        entity.setUrlPermissions(new HashSet<>(List.of(new UrlPermission())));

        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(RuntimeException.class, () -> serviceFunctionService.delete(id));
        verify(serviceFunctionRepository, never()).delete(any());
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(serviceFunctionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceFunctionService.delete(id));
    }

    private CreateServiceFunctionRequest createServiceFunctionRequest(String name, String description, UUID systemServiceId) {
        CreateServiceFunctionRequest request = new CreateServiceFunctionRequest();
        request.setName(name);
        request.setDescription(description);
        request.setSystemServiceId(systemServiceId);
        return request;
    }

    private UpdateServiceFunctionRequest updateServiceFunctionRequest(String name, String description, UUID systemServiceId) {
        UpdateServiceFunctionRequest request = new UpdateServiceFunctionRequest();
        request.setName(name);
        request.setDescription(description);
        request.setSystemServiceId(systemServiceId);
        return request;
    }

    private SystemService createSystemService(UUID id, String name, String description) {
        SystemService entity = new SystemService();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(description);
        return entity;
    }

    private ServiceFunction createServiceFunction(String name, String description, SystemService systemService) {
        ServiceFunction entity = new ServiceFunction();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setDescription(description);
        entity.setSystemService(systemService);
        entity.setUrlPermissions(new HashSet<>());
        entity.setRolesServiceFunctions(new HashSet<>());
        return entity;
    }

    private ServiceFunctionResponse createServiceFunctionResponse(UUID id, String name, String description,
            UUID systemServiceId, String systemServiceName) {
        ServiceFunctionResponse response = new ServiceFunctionResponse();
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        response.setSystemServiceId(systemServiceId);
        response.setSystemServiceName(systemServiceName);
        return response;
    }
}
