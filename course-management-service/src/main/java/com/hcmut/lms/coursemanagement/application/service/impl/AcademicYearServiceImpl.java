package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.AcademicYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.AcademicYearResponse;
import com.hcmut.lms.coursemanagement.application.mapper.AcademicYearMapper;
import com.hcmut.lms.coursemanagement.application.service.AcademicYearService;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
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
public class AcademicYearServiceImpl implements AcademicYearService {
    
    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearMapper academicYearMapper;
    
    @Override
    public AcademicYearResponse createAcademicYear(AcademicYearRequest request) {
        log.info("Creating academic year: {}", request.getYearCode());
        
        AcademicYear academicYear = academicYearMapper.toEntity(request);
        AcademicYear savedAcademicYear = academicYearRepository.save(academicYear);
        
        log.info("Academic year created successfully with id: {}", savedAcademicYear.getId());
        return academicYearMapper.toResponse(savedAcademicYear);
    }
    
    @Override
    public AcademicYearResponse updateAcademicYear(UUID id, AcademicYearRequest request) {
        log.info("Updating academic year with id: {}", id);
        
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Academic year not found with id: " + id));
        
        academicYearMapper.updateEntityFromRequest(request, academicYear);
        AcademicYear updatedAcademicYear = academicYearRepository.save(academicYear);
        
        log.info("Academic year updated successfully with id: {}", id);
        return academicYearMapper.toResponse(updatedAcademicYear);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AcademicYearResponse getAcademicYearById(UUID id) {
        log.info("Getting academic year with id: {}", id);
        
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Academic year not found with id: " + id));
        
        return academicYearMapper.toResponse(academicYear);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AcademicYearResponse> getAllAcademicYears() {
        log.info("Getting all academic years");
        
        return academicYearRepository.findAll().stream()
                .map(academicYearMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<AcademicYearResponse> getAllAcademicYears(int page, int size) {
        log.info("Getting all academic years with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AcademicYear> academicYearPage = academicYearRepository.findAll(pageable);
        
        Page<AcademicYearResponse> responsePage = academicYearPage.map(academicYearMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteAcademicYear(UUID id) {
        log.info("Deleting academic year with id: {}", id);
        
        if (!academicYearRepository.existsById(id)) {
            throw new EntityNotFoundException("Academic year not found with id: " + id);
        }
        
        academicYearRepository.deleteById(id);
        log.info("Academic year deleted successfully with id: {}", id);
    }
}


