package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.GradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.GradingResponse;
import com.hcmut.lms.coursemanagement.application.mapper.GradingMapper;
import com.hcmut.lms.coursemanagement.application.service.GradingService;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Grading;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
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
public class GradingServiceImpl implements GradingService {
    
    private final GradingRepository gradingRepository;
    private final GradingMapper gradingMapper;
    
    @Override
    public GradingResponse createGrading(GradingRequest request) {
        log.info("Creating grading: {}", request.getName());
        
        Grading grading = gradingMapper.toEntity(request);
        Grading savedGrading = gradingRepository.save(grading);
        
        log.info("Grading created successfully with id: {}", savedGrading.getId());
        return gradingMapper.toResponse(savedGrading);
    }
    
    @Override
    public GradingResponse updateGrading(UUID id, GradingRequest request) {
        log.info("Updating grading with id: {}", id);
        
        Grading grading = gradingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grading not found with id: " + id));
        
        gradingMapper.updateEntityFromRequest(request, grading);
        Grading updatedGrading = gradingRepository.save(grading);
        
        log.info("Grading updated successfully with id: {}", id);
        return gradingMapper.toResponse(updatedGrading);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GradingResponse getGradingById(UUID id) {
        log.info("Getting grading with id: {}", id);
        
        Grading grading = gradingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grading not found with id: " + id));
        
        return gradingMapper.toResponse(grading);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GradingResponse> getAllGradings() {
        log.info("Getting all gradings");
        
        return gradingRepository.findAll().stream()
                .map(gradingMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteGrading(UUID id) {
        log.info("Deleting grading with id: {}", id);
        
        if (!gradingRepository.existsById(id)) {
            throw new RuntimeException("Grading not found with id: " + id);
        }
        
        gradingRepository.deleteById(id);
        log.info("Grading deleted successfully with id: {}", id);
    }
}


