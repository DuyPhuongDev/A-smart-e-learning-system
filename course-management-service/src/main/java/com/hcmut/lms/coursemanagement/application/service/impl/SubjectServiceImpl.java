package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;
import com.hcmut.lms.coursemanagement.application.mapper.SubjectMapper;
import com.hcmut.lms.coursemanagement.application.service.SubjectService;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectServiceImpl implements SubjectService {
    
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    
    @Override
    public SubjectResponse createSubject(SubjectRequest request) {
        log.info("Creating subject: {}", request.getName());
        
        // Check if subject code already exists
        if (request.getCode() != null && subjectRepository.existsByCode(request.getCode())) {
            throw new EntityExistsException("Subject with code " + request.getCode() + " already exists");
        }
        
        Subject subject = subjectMapper.toEntity(request);
        Subject savedSubject = subjectRepository.save(subject);
        
        log.info("Subject created successfully with id: {}", savedSubject.getId());
        return subjectMapper.toResponse(savedSubject);
    }
    
    @Override
    public SubjectResponse updateSubject(UUID id, SubjectRequest request) {
        log.info("Updating subject with id: {}", id);
        
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
        
        // Check if new code conflicts with existing subjects
        if (request.getCode() != null && !request.getCode().equals(subject.getCode())) {
            if (subjectRepository.existsByCode(request.getCode())) {
                throw new EntityExistsException("Subject with code " + request.getCode() + " already exists");
            }
        }
        
        subjectMapper.updateEntityFromRequest(request, subject);
        Subject updatedSubject = subjectRepository.save(subject);
        
        log.info("Subject updated successfully with id: {}", id);
        return subjectMapper.toResponse(updatedSubject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(UUID id) {
        log.info("Getting subject with id: {}", id);
        
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
        
        return subjectMapper.toResponse(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectByCode(String code) {
        log.info("Getting subject with code: {}", code);
        
        Subject subject = subjectRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Subject not found with code: " + code));
        
        return subjectMapper.toResponse(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        log.info("Getting all subjects");
        
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubjectResponse> getAllSubjects(int page, int size, String keyword) {

        log.info("Getting subjects - page: {}, size: {}, keyword: {}", page, size, keyword);

        Pageable pageable = PageRequest.of(page, size);

        Page<Subject> subjectPage;

        if (keyword == null || keyword.isBlank()) {

            subjectPage = subjectRepository.findAll(pageable);

        } else {

            Optional<Subject> subject = subjectRepository.findByCode(keyword);

            if (subject.isPresent()) {
                subjectPage = new PageImpl<>(List.of(subject.get()), pageable, 1);
            } else {
                subjectPage = subjectRepository.findByNameContainingIgnoreCase(keyword, pageable);
            }

        }

        Page<SubjectResponse> responsePage = subjectPage.map(subjectMapper::toResponse);

        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteSubject(UUID id) {
        log.info("Deleting subject with id: {}", id);
        
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id: " + id);
        }
        
        subjectRepository.deleteById(id);
        log.info("Subject deleted successfully with id: {}", id);
    }
}


