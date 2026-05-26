package com.hcmut.lms.usermanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.UrlPermissionMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UrlPermissionResponse;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.UrlPermission;
import com.hcmut.lms.usermanagement.repository.ServiceFunctionRepository;
import com.hcmut.lms.usermanagement.repository.UrlPermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlPermissionServiceImplTest {

    @Mock
    private UrlPermissionRepository urlPermissionRepository;

    @Mock
    private ServiceFunctionRepository serviceFunctionRepository;

    @Mock
    private UrlPermissionMapper urlPermissionMapper;

    @InjectMocks
    private UrlPermissionServiceImpl urlPermissionService;

    @Test
    void create_shouldReturnResponse_whenValidRequest() {
        UUID serviceFunctionId = UUID.randomUUID();
        CreateUrlPermissionRequest request = createUrlPermissionRequest("/api/test", "GET", serviceFunctionId);
        ServiceFunction serviceFunction = createServiceFunction(serviceFunctionId, "fn");
        UrlPermission entity = createUrlPermission("/api/test", "GET", serviceFunction);
        UrlPermission saved = createUrlPermission("/api/test", "GET", serviceFunction);
        UrlPermissionResponse response = createUrlPermissionResponse(saved.getId(), "/api/test", "GET", serviceFunctionId, "fn");

        when(serviceFunctionRepository.findById(serviceFunctionId)).thenReturn(Optional.of(serviceFunction));
        when(urlPermissionMapper.toEntity(request)).thenReturn(entity);
        when(urlPermissionRepository.save(entity)).thenReturn(saved);
        when(urlPermissionMapper.toResponse(saved)).thenReturn(response);

        UrlPermissionResponse result = urlPermissionService.create(request);

        assertEquals(response, result);
        verify(urlPermissionRepository).save(entity);
    }

    @Test
    void create_shouldThrowException_whenServiceFunctionNotFound() {
        UUID serviceFunctionId = UUID.randomUUID();
        CreateUrlPermissionRequest request = createUrlPermissionRequest("/api/test", "GET", serviceFunctionId);

        when(serviceFunctionRepository.findById(serviceFunctionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> urlPermissionService.create(request));
    }

    @Test
    void getById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UrlPermission entity = createUrlPermission("/api/test", "GET", null);
        UrlPermissionResponse response = createUrlPermissionResponse(id, "/api/test", "GET", UUID.randomUUID(), "fn");

        when(urlPermissionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(urlPermissionMapper.toResponse(entity)).thenReturn(response);

        UrlPermissionResponse result = urlPermissionService.getById(id);

        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(urlPermissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> urlPermissionService.getById(id));
    }

    @Test
    void getAll_shouldReturnList() {
        UrlPermission entity = createUrlPermission("/api/test", "GET", null);
        UrlPermissionResponse response = createUrlPermissionResponse(entity.getId(), "/api/test", "GET", UUID.randomUUID(), "fn");

        when(urlPermissionRepository.findAll()).thenReturn(List.of(entity));
        when(urlPermissionMapper.toResponse(entity)).thenReturn(response);

        List<UrlPermissionResponse> result = urlPermissionService.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNone() {
        when(urlPermissionRepository.findAll()).thenReturn(Collections.emptyList());

        List<UrlPermissionResponse> result = urlPermissionService.getAll();

        assertEquals(0, result.size());
    }

    @Test
    void getByServiceFunctionId_shouldReturnList() {
        UUID serviceFunctionId = UUID.randomUUID();
        UrlPermission entity = createUrlPermission("/api/test", "GET", null);
        UrlPermissionResponse response = createUrlPermissionResponse(entity.getId(), "/api/test", "GET", serviceFunctionId, "fn");

        when(urlPermissionRepository.findByServiceFunctionId(serviceFunctionId)).thenReturn(List.of(entity));
        when(urlPermissionMapper.toResponse(entity)).thenReturn(response);

        List<UrlPermissionResponse> result = urlPermissionService.getByServiceFunctionId(serviceFunctionId);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getByServiceFunctionId_shouldReturnEmptyList_whenNone() {
        UUID serviceFunctionId = UUID.randomUUID();
        when(urlPermissionRepository.findByServiceFunctionId(serviceFunctionId)).thenReturn(Collections.emptyList());

        List<UrlPermissionResponse> result = urlPermissionService.getByServiceFunctionId(serviceFunctionId);

        assertEquals(0, result.size());
    }

    @Test
    void update_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UpdateUrlPermissionRequest request = updateUrlPermissionRequest("/api/updated", "POST", null);
        UrlPermission existing = createUrlPermission("/api/original", "GET", null);
        UrlPermission saved = createUrlPermission("/api/updated", "POST", null);
        UrlPermissionResponse response = createUrlPermissionResponse(id, "/api/updated", "POST", UUID.randomUUID(), "fn");

        when(urlPermissionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(urlPermissionRepository.save(existing)).thenReturn(saved);
        when(urlPermissionMapper.toResponse(saved)).thenReturn(response);

        UrlPermissionResponse result = urlPermissionService.update(id, request);

        assertEquals(response, result);
        verify(urlPermissionMapper).updateEntity(request, existing);
    }

    @Test
    void update_shouldUpdateServiceFunction_whenServiceFunctionIdProvided() {
        UUID id = UUID.randomUUID();
        UUID newServiceFunctionId = UUID.randomUUID();
        UpdateUrlPermissionRequest request = updateUrlPermissionRequest("/api/test", "GET", newServiceFunctionId);
        UrlPermission existing = createUrlPermission("/api/test", "GET", null);
        ServiceFunction newServiceFunction = createServiceFunction(newServiceFunctionId, "NewFn");
        UrlPermissionResponse response = createUrlPermissionResponse(id, "/api/test", "GET", newServiceFunctionId, "NewFn");

        when(urlPermissionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(serviceFunctionRepository.findById(newServiceFunctionId)).thenReturn(Optional.of(newServiceFunction));
        when(urlPermissionRepository.save(existing)).thenReturn(existing);
        when(urlPermissionMapper.toResponse(existing)).thenReturn(response);

        UrlPermissionResponse result = urlPermissionService.update(id, request);

        assertEquals(response, result);
        verify(urlPermissionMapper).updateEntity(request, existing);
        verify(urlPermissionRepository).save(existing);
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(urlPermissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> urlPermissionService.update(id, updateUrlPermissionRequest("/api/test", "GET", null)));
    }

    @Test
    void delete_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        UrlPermission entity = createUrlPermission("/api/test", "GET", null);

        when(urlPermissionRepository.findById(id)).thenReturn(Optional.of(entity));

        urlPermissionService.delete(id);

        verify(urlPermissionRepository).delete(entity);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(urlPermissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> urlPermissionService.delete(id));
    }

    private CreateUrlPermissionRequest createUrlPermissionRequest(String urlPattern, String httpMethod, UUID serviceFunctionId) {
        CreateUrlPermissionRequest request = new CreateUrlPermissionRequest();
        request.setUrlPattern(urlPattern);
        request.setHttpMethod(httpMethod);
        request.setServiceFunctionId(serviceFunctionId);
        return request;
    }

    private UpdateUrlPermissionRequest updateUrlPermissionRequest(String urlPattern, String httpMethod, UUID serviceFunctionId) {
        UpdateUrlPermissionRequest request = new UpdateUrlPermissionRequest();
        request.setUrlPattern(urlPattern);
        request.setHttpMethod(httpMethod);
        request.setServiceFunctionId(serviceFunctionId);
        return request;
    }

    private ServiceFunction createServiceFunction(UUID id, String name) {
        ServiceFunction entity = new ServiceFunction();
        entity.setId(id);
        entity.setName(name);
        return entity;
    }

    private UrlPermission createUrlPermission(String urlPattern, String httpMethod, ServiceFunction serviceFunction) {
        UrlPermission entity = new UrlPermission();
        entity.setId(UUID.randomUUID());
        entity.setUrlPattern(urlPattern);
        entity.setHttpMethod(httpMethod);
        entity.setServiceFunction(serviceFunction);
        return entity;
    }

    private UrlPermissionResponse createUrlPermissionResponse(UUID id, String urlPattern, String httpMethod,
            UUID serviceFunctionId, String serviceFunctionName) {
        UrlPermissionResponse response = new UrlPermissionResponse();
        response.setId(id);
        response.setUrlPattern(urlPattern);
        response.setHttpMethod(httpMethod);
        response.setServiceFunctionId(serviceFunctionId);
        response.setServiceFunctionName(serviceFunctionName);
        return response;
    }
}
