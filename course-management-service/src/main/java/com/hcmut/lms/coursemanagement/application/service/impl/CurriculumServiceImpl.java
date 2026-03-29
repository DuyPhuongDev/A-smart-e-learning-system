package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumResponse;
import com.hcmut.lms.coursemanagement.application.mapper.CurriculumMapper;
import com.hcmut.lms.coursemanagement.application.service.CurriculumService;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumId;
import com.hcmut.lms.coursemanagement.domain.entity.intakeYear.IntakeYear;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.repository.CurriculumRepository;
import com.hcmut.lms.coursemanagement.repository.IntakeYearRepository;
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
public class CurriculumServiceImpl implements CurriculumService {
    
    private final CurriculumRepository curriculumRepository;
    private final SpecializationRepository specializationRepository;
    private final IntakeYearRepository intakeYearRepository;
    private final CurriculumMapper curriculumMapper;
    
    @Override
    public CurriculumResponse createCurriculum(CurriculumRequest request) {
        log.info("Creating curriculum: {}", request.getCode());
        
        CurriculumId curriculumId = new CurriculumId(
                request.getCode(),
                request.getSpecializationId(),
                request.getIntakeYearId()
        );
        
        if (curriculumRepository.existsById(curriculumId)) {
            throw new EntityExistsException("Curriculum already exists with the given composite key");
        }
        
        Curriculum curriculum = curriculumMapper.toEntity(request);
        curriculum.setId(curriculumId);
        
        if (request.getSpecializationId() != null) {
            Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                    .orElseThrow(() -> new EntityNotFoundException("Specialization not found with id: " + request.getSpecializationId()));
            curriculum.setSpecialization(specialization);
        }
        
        if (request.getIntakeYearId() != null) {
            IntakeYear intakeYear = intakeYearRepository.findById(request.getIntakeYearId())
                    .orElseThrow(() -> new EntityNotFoundException("Intake year not found with id: " + request.getIntakeYearId()));
            curriculum.setIntakeYear(intakeYear);
        }
        
        Curriculum savedCurriculum = curriculumRepository.save(curriculum);
        
        log.info("Curriculum created successfully");
        return curriculumMapper.toResponse(savedCurriculum);
    }
    
    @Override
    public CurriculumResponse updateCurriculum(String code, UUID specializationId, UUID intakeYearId, CurriculumRequest request) {
        log.info("Updating curriculum: {}", code);
        
        CurriculumId curriculumId = new CurriculumId(code, specializationId, intakeYearId);
        
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new EntityNotFoundException("Curriculum not found"));
        
        curriculumMapper.updateEntityFromRequest(request, curriculum);
        
        Curriculum updatedCurriculum = curriculumRepository.save(curriculum);
        
        log.info("Curriculum updated successfully");
        return curriculumMapper.toResponse(updatedCurriculum);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CurriculumResponse getCurriculumById(String code, UUID specializationId, UUID intakeYearId) {
        log.info("Getting curriculum: {}", code);
        
        CurriculumId curriculumId = new CurriculumId(code, specializationId, intakeYearId);
        
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new EntityNotFoundException("Curriculum not found"));
        
        return curriculumMapper.toResponse(curriculum);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getAllCurriculums() {
        log.info("Getting all curriculums");
        
        return curriculumRepository.findAll().stream()
                .map(curriculumMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<CurriculumResponse> getAllCurriculums(int page, int size) {
        log.info("Getting all curriculums with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Curriculum> curriculumPage = curriculumRepository.findAll(pageable);
        
        Page<CurriculumResponse> responsePage = curriculumPage.map(curriculumMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getCurriculumsBySpecializationId(UUID specializationId) {
        log.info("Getting curriculums by specialization id: {}", specializationId);
        
        return curriculumRepository.findByIdSpecializationId(specializationId).stream()
                .map(curriculumMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<CurriculumResponse> getCurriculumsBySpecializationId(UUID specializationId, int page, int size) {
        log.info("Getting curriculums by specialization id: {} with pagination - page: {}, size: {}", specializationId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Curriculum> curriculumPage = curriculumRepository.findByIdSpecializationId(specializationId, pageable);
        
        Page<CurriculumResponse> responsePage = curriculumPage.map(curriculumMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getCurriculumsByIntakeYearId(UUID intakeYearId) {
        log.info("Getting curriculums by intake year id: {}", intakeYearId);
        
        return curriculumRepository.findByIdIntakeYearId(intakeYearId).stream()
                .map(curriculumMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<CurriculumResponse> getCurriculumsByIntakeYearId(UUID intakeYearId, int page, int size) {
        log.info("Getting curriculums by intake year id: {} with pagination - page: {}, size: {}", intakeYearId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Curriculum> curriculumPage = curriculumRepository.findByIdIntakeYearId(intakeYearId, pageable);
        
        Page<CurriculumResponse> responsePage = curriculumPage.map(curriculumMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteCurriculum(String code, UUID specializationId, UUID intakeYearId) {
        log.info("Deleting curriculum: {}", code);
        
        CurriculumId curriculumId = new CurriculumId(code, specializationId, intakeYearId);
        
        if (!curriculumRepository.existsById(curriculumId)) {
            throw new EntityNotFoundException("Curriculum not found");
        }
        
        curriculumRepository.deleteById(curriculumId);
        log.info("Curriculum deleted successfully");
    }
}

