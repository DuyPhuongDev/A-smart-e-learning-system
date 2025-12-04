package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.IntakeYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.IntakeYearResponse;
import com.hcmut.lms.coursemanagement.application.mapper.IntakeYearMapper;
import com.hcmut.lms.coursemanagement.application.service.IntakeYearService;
import com.hcmut.lms.coursemanagement.domain.entity.intakeYear.IntakeYear;
import com.hcmut.lms.coursemanagement.repository.IntakeYearRepository;
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
public class IntakeYearServiceImpl implements IntakeYearService {
    
    private final IntakeYearRepository intakeYearRepository;
    private final IntakeYearMapper intakeYearMapper;
    
    @Override
    public IntakeYearResponse createIntakeYear(IntakeYearRequest request) {
        log.info("Creating intake year: {}", request.getStartYear());
        
        if (request.getStartYear() != null && intakeYearRepository.existsByStartYear(request.getStartYear())) {
            throw new EntityExistsException("Intake year with start year " + request.getStartYear() + " already exists");
        }
        
        IntakeYear intakeYear = intakeYearMapper.toEntity(request);
        IntakeYear savedIntakeYear = intakeYearRepository.save(intakeYear);
        
        log.info("Intake year created successfully with id: {}", savedIntakeYear.getId());
        return intakeYearMapper.toResponse(savedIntakeYear);
    }
    
    @Override
    public IntakeYearResponse updateIntakeYear(UUID id, IntakeYearRequest request) {
        log.info("Updating intake year with id: {}", id);
        
        IntakeYear intakeYear = intakeYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intake year not found with id: " + id));
        
        if (request.getStartYear() != null && !request.getStartYear().equals(intakeYear.getStartYear()) && intakeYearRepository.existsByStartYear(request.getStartYear())) {
                throw new EntityExistsException("Intake year with start year " + request.getStartYear() + " already exists");
            }


        intakeYearMapper.updateEntityFromRequest(request, intakeYear);
        IntakeYear updatedIntakeYear = intakeYearRepository.save(intakeYear);
        
        log.info("Intake year updated successfully with id: {}", id);
        return intakeYearMapper.toResponse(updatedIntakeYear);
    }
    
    @Override
    @Transactional(readOnly = true)
    public IntakeYearResponse getIntakeYearById(UUID id) {
        log.info("Getting intake year with id: {}", id);
        
        IntakeYear intakeYear = intakeYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intake year not found with id: " + id));
        
        return intakeYearMapper.toResponse(intakeYear);
    }
    
    @Override
    @Transactional(readOnly = true)
    public IntakeYearResponse getIntakeYearByStartYear(Integer startYear) {
        log.info("Getting intake year with start year: {}", startYear);
        
        IntakeYear intakeYear = intakeYearRepository.findByStartYear(startYear)
                .orElseThrow(() -> new EntityNotFoundException("Intake year not found with start year: " + startYear));
        
        return intakeYearMapper.toResponse(intakeYear);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IntakeYearResponse> getAllIntakeYears() {
        log.info("Getting all intake years");
        
        return intakeYearRepository.findAll().stream()
                .map(intakeYearMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<IntakeYearResponse> getAllIntakeYears(int page, int size) {
        log.info("Getting all intake years with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<IntakeYear> intakeYearPage = intakeYearRepository.findAll(pageable);
        
        Page<IntakeYearResponse> responsePage = intakeYearPage.map(intakeYearMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteIntakeYear(UUID id) {
        log.info("Deleting intake year with id: {}", id);
        
        if (!intakeYearRepository.existsById(id)) {
            throw new EntityNotFoundException("Intake year not found with id: " + id);
        }
        
        intakeYearRepository.deleteById(id);
        log.info("Intake year deleted successfully with id: {}", id);
    }
}

