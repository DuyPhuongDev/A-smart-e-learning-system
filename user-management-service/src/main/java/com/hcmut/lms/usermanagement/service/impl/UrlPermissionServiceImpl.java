package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.UrlPermissionMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UrlPermissionResponse;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.UrlPermission;
import com.hcmut.lms.usermanagement.repository.ServiceFunctionRepository;
import com.hcmut.lms.usermanagement.repository.UrlPermissionRepository;
import com.hcmut.lms.usermanagement.service.UrlPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlPermissionServiceImpl implements UrlPermissionService {
    
    private final UrlPermissionRepository urlPermissionRepository;
    private final ServiceFunctionRepository serviceFunctionRepository;
    private final UrlPermissionMapper urlPermissionMapper;
    
    @Override
    @Transactional
    public UrlPermissionResponse create(CreateUrlPermissionRequest request) {
        ServiceFunction serviceFunction = serviceFunctionRepository.findById(request.getServiceFunctionId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceFunction", "id", request.getServiceFunctionId()));
        
        UrlPermission urlPermission = urlPermissionMapper.toEntity(request);
        urlPermission.setServiceFunction(serviceFunction);
        urlPermission = urlPermissionRepository.save(urlPermission);
        return urlPermissionMapper.toResponse(urlPermission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UrlPermissionResponse getById(UUID id) {
        UrlPermission urlPermission = urlPermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UrlPermission", "id", id));
        return urlPermissionMapper.toResponse(urlPermission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UrlPermissionResponse> getAll() {
        return urlPermissionRepository.findAll().stream()
                .map(urlPermissionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UrlPermissionResponse> getByServiceFunctionId(UUID serviceFunctionId) {
        return urlPermissionRepository.findByServiceFunctionId(serviceFunctionId).stream()
                .map(urlPermissionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public UrlPermissionResponse update(UUID id, UpdateUrlPermissionRequest request) {
        UrlPermission urlPermission = urlPermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UrlPermission", "id", id));
        
        if (request.getServiceFunctionId() != null) {
            ServiceFunction serviceFunction = serviceFunctionRepository.findById(request.getServiceFunctionId())
                    .orElseThrow(() -> new ResourceNotFoundException("ServiceFunction", "id", request.getServiceFunctionId()));
            urlPermission.setServiceFunction(serviceFunction);
        }
        
        urlPermissionMapper.updateEntity(request, urlPermission);
        urlPermission = urlPermissionRepository.save(urlPermission);
        return urlPermissionMapper.toResponse(urlPermission);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        UrlPermission urlPermission = urlPermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UrlPermission", "id", id));
        urlPermissionRepository.delete(urlPermission);
    }
}

