package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.SystemServiceMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.response.SystemServiceResponse;
import com.hcmut.lms.usermanagement.model.entity.SystemService;
import com.hcmut.lms.usermanagement.repository.SystemServiceRepository;
import com.hcmut.lms.usermanagement.service.SystemServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemServiceServiceImpl implements SystemServiceService {
    
    private final SystemServiceRepository systemServiceRepository;
    private final SystemServiceMapper systemServiceMapper;
    
    @Override
    @Transactional
    public SystemServiceResponse create(CreateSystemServiceRequest request) {
        SystemService systemService = systemServiceMapper.toEntity(request);
        systemService = systemServiceRepository.save(systemService);
        return systemServiceMapper.toResponse(systemService);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SystemServiceResponse getById(UUID id) {
        SystemService systemService = systemServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemService", "id", id));
        return systemServiceMapper.toResponse(systemService);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SystemServiceResponse> getAll() {
        return systemServiceRepository.findAll().stream()
                .map(systemServiceMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SystemServiceResponse update(UUID id, UpdateSystemServiceRequest request) {
        SystemService systemService = systemServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemService", "id", id));
        
        systemServiceMapper.updateEntity(request, systemService);
        systemService = systemServiceRepository.save(systemService);
        return systemServiceMapper.toResponse(systemService);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        SystemService systemService = systemServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemService", "id", id));
        
        if (!systemService.getServiceFunctions().isEmpty()) {
            throw new RuntimeException("Cannot delete system service that has service functions");
        }
        
        systemServiceRepository.delete(systemService);
    }
}

