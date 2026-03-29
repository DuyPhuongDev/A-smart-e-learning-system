package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SpecializationRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SpecializationResponse;
import com.hcmut.lms.coursemanagement.application.mapper.SpecializationMapper;
import com.hcmut.lms.coursemanagement.application.service.SpecializationService;
import com.hcmut.lms.coursemanagement.domain.entity.department.Department;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.repository.DepartmentRepository;
import com.hcmut.lms.coursemanagement.repository.SpecializationRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpecializationServiceImpl implements SpecializationService {
    
    private final SpecializationRepository specializationRepository;
    private final DepartmentRepository departmentRepository;
    private final SpecializationMapper specializationMapper;
    
    @Override
    public SpecializationResponse createSpecialization(SpecializationRequest request) {
        log.info("Creating specialization: {}", request.getName());
        
        if (request.getCode() != null && specializationRepository.existsByCode(request.getCode())) {
            throw new EntityExistsException("Specialization with code " + request.getCode() + " already exists");
        }
        
        Specialization specialization = specializationMapper.toEntity(request);
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
            specialization.setDepartment(department);
        }
        
        Specialization savedSpecialization = specializationRepository.save(specialization);
        
        log.info("Specialization created successfully with id: {}", savedSpecialization.getId());
        return specializationMapper.toResponse(savedSpecialization);
    }
    
    @Override
    public SpecializationResponse updateSpecialization(UUID id, SpecializationRequest request) {
        log.info("Updating specialization with id: {}", id);
        
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Specialization not found with id: " + id));
        
        if (request.getCode() != null && !request.getCode().equals(specialization.getCode()) && specializationRepository.existsByCode(request.getCode())) {
                throw new EntityExistsException("Specialization with code " + request.getCode() + " already exists");
        }

        specializationMapper.updateEntityFromRequest(request, specialization);
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + request.getDepartmentId()));
            specialization.setDepartment(department);
        }
        
        Specialization updatedSpecialization = specializationRepository.save(specialization);
        
        log.info("Specialization updated successfully with id: {}", id);
        return specializationMapper.toResponse(updatedSpecialization);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SpecializationResponse getSpecializationById(UUID id) {
        log.info("Getting specialization with id: {}", id);
        
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Specialization not found with id: " + id));
        
        return specializationMapper.toResponse(specialization);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SpecializationResponse getSpecializationByCode(String code) {
        log.info("Getting specialization with code: {}", code);
        
        Specialization specialization = specializationRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Specialization not found with code: " + code));
        
        return specializationMapper.toResponse(specialization);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SpecializationResponse> getAllSpecializations() {
        log.info("Getting all specializations");
        
        return specializationRepository.findAll().stream()
                .map(specializationMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<SpecializationResponse> getAllSpecializations(int page, int size) {
        log.info("Getting all specializations with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Specialization> specializationPage = specializationRepository.findAll(pageable);
        
        Page<SpecializationResponse> responsePage = specializationPage.map(specializationMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SpecializationResponse> getSpecializationsByDepartmentId(UUID departmentId) {
        log.info("Getting specializations by department id: {}", departmentId);
        
        return specializationRepository.findByDepartmentId(departmentId).stream()
                .map(specializationMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<SpecializationResponse> getSpecializationsByDepartmentId(UUID departmentId, int page, int size) {
        log.info("Getting specializations by department id: {} with pagination - page: {}, size: {}", departmentId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Specialization> specializationPage = specializationRepository.findByDepartmentId(departmentId, pageable);
        
        Page<SpecializationResponse> responsePage = specializationPage.map(specializationMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteSpecialization(UUID id) {
        log.info("Deleting specialization with id: {}", id);
        
        if (!specializationRepository.existsById(id)) {
            throw new EntityNotFoundException("Specialization not found with id: " + id);
        }
        
        specializationRepository.deleteById(id);
        log.info("Specialization deleted successfully with id: {}", id);
    }
}

