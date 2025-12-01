package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.ServiceFunctionMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.ServiceFunctionResponse;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.SystemService;
import com.hcmut.lms.usermanagement.repository.ServiceFunctionRepository;
import com.hcmut.lms.usermanagement.repository.SystemServiceRepository;
import com.hcmut.lms.usermanagement.service.ServiceFunctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceFunctionServiceImpl implements ServiceFunctionService {
    
    private final ServiceFunctionRepository serviceFunctionRepository;
    private final SystemServiceRepository systemServiceRepository;
    private final ServiceFunctionMapper serviceFunctionMapper;
    
    @Override
    @Transactional
    public ServiceFunctionResponse create(CreateServiceFunctionRequest request) {
        SystemService systemService = systemServiceRepository.findById(request.getSystemServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("SystemService", "id", request.getSystemServiceId()));
        
        ServiceFunction serviceFunction = serviceFunctionMapper.toEntity(request);
        serviceFunction.setSystemService(systemService);
        serviceFunction = serviceFunctionRepository.save(serviceFunction);
        return serviceFunctionMapper.toResponse(serviceFunction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ServiceFunctionResponse getById(UUID id) {
        ServiceFunction serviceFunction = serviceFunctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceFunction", "id", id));
        return serviceFunctionMapper.toResponse(serviceFunction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceFunctionResponse> getAll() {
        return serviceFunctionRepository.findAll().stream()
                .map(serviceFunctionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceFunctionResponse> getBySystemServiceId(UUID systemServiceId) {
        return serviceFunctionRepository.findBySystemServiceId(systemServiceId).stream()
                .map(serviceFunctionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ServiceFunctionResponse update(UUID id, UpdateServiceFunctionRequest request) {
        ServiceFunction serviceFunction = serviceFunctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceFunction", "id", id));
        
        if (request.getSystemServiceId() != null) {
            SystemService systemService = systemServiceRepository.findById(request.getSystemServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("SystemService", "id", request.getSystemServiceId()));
            serviceFunction.setSystemService(systemService);
        }
        
        serviceFunctionMapper.updateEntity(request, serviceFunction);
        serviceFunction = serviceFunctionRepository.save(serviceFunction);
        return serviceFunctionMapper.toResponse(serviceFunction);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        ServiceFunction serviceFunction = serviceFunctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceFunction", "id", id));
        
        if (!serviceFunction.getUrlPermissions().isEmpty()) {
            throw new RuntimeException("Cannot delete service function that has URL permissions");
        }
        
        serviceFunctionRepository.delete(serviceFunction);
    }
}

