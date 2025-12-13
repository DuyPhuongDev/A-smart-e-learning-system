package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumSectionResponse;
import com.hcmut.lms.coursemanagement.application.mapper.CurriculumSectionMapper;
import com.hcmut.lms.coursemanagement.application.service.CurriculumSectionService;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumId;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSection;
import com.hcmut.lms.coursemanagement.repository.CurriculumRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CurriculumSectionServiceImpl implements CurriculumSectionService {
    
    private final CurriculumSectionRepository curriculumSectionRepository;
    private final CurriculumRepository curriculumRepository;
    private final CurriculumSectionMapper curriculumSectionMapper;
    
    @Override
    public CurriculumSectionResponse createCurriculumSection(CurriculumSectionRequest request) {
        log.info("Creating curriculum section: {}", request.getName());
        
        CurriculumSection curriculumSection = curriculumSectionMapper.toEntity(request);
        
        CurriculumId curriculumId = new CurriculumId(
                request.getCurriculumCode(),
                request.getCurriculumSpecializationId(),
                request.getCurriculumIntakeYearId()
        );
        
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculumSection.setCurriculum(curriculum);
        
        CurriculumSection savedCurriculumSection = curriculumSectionRepository.save(curriculumSection);
        
        log.info("Curriculum section created successfully with id: {}", savedCurriculumSection.getId());
        return curriculumSectionMapper.toResponse(savedCurriculumSection);
    }
    
    @Override
    public CurriculumSectionResponse updateCurriculumSection(UUID id, CurriculumSectionRequest request) {
        log.info("Updating curriculum section with id: {}", id);
        
        CurriculumSection curriculumSection = curriculumSectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum section not found with id: " + id));
        
        curriculumSectionMapper.updateEntityFromRequest(request, curriculumSection);
        
        if (request.getCurriculumCode() != null) {
            CurriculumId curriculumId = new CurriculumId(
                    request.getCurriculumCode(),
                    request.getCurriculumSpecializationId(),
                    request.getCurriculumIntakeYearId()
            );
            
            Curriculum curriculum = curriculumRepository.findById(curriculumId)
                    .orElseThrow(() -> new RuntimeException("Curriculum not found"));
            curriculumSection.setCurriculum(curriculum);
        }
        
        CurriculumSection updatedCurriculumSection = curriculumSectionRepository.save(curriculumSection);
        
        log.info("Curriculum section updated successfully with id: {}", id);
        return curriculumSectionMapper.toResponse(updatedCurriculumSection);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CurriculumSectionResponse getCurriculumSectionById(UUID id) {
        log.info("Getting curriculum section with id: {}", id);
        
        CurriculumSection curriculumSection = curriculumSectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum section not found with id: " + id));
        
        return curriculumSectionMapper.toResponse(curriculumSection);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CurriculumSectionResponse> getAllCurriculumSections() {
        log.info("Getting all curriculum sections");
        
        return curriculumSectionRepository.findAll().stream()
                .map(curriculumSectionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CurriculumSectionResponse> getCurriculumSectionsByCurriculumId(String code, UUID specializationId, UUID intakeYearId) {
        log.info("Getting curriculum sections by curriculum id: {}", code);
        
        return curriculumSectionRepository.findByCurriculumId(code, specializationId, intakeYearId).stream()
                .map(curriculumSectionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteCurriculumSection(UUID id) {
        log.info("Deleting curriculum section with id: {}", id);
        
        if (!curriculumSectionRepository.existsById(id)) {
            throw new RuntimeException("Curriculum section not found with id: " + id);
        }
        
        curriculumSectionRepository.deleteById(id);
        log.info("Curriculum section deleted successfully with id: {}", id);
    }
}

