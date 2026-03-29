package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.FacultyRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.FacultyResponse;
import com.hcmut.lms.coursemanagement.application.mapper.FacultyMapper;
import com.hcmut.lms.coursemanagement.application.service.FacultyService;
import com.hcmut.lms.coursemanagement.domain.entity.faculty.Faculty;
import com.hcmut.lms.coursemanagement.repository.FacultyRepository;
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
public class FacultyServiceImpl implements FacultyService {
    
    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;
    
    @Override
    public FacultyResponse createFaculty(FacultyRequest request) {
        log.info("Creating faculty: {}", request.getName());
        
        if (request.getCode() != null && facultyRepository.existsByCode(request.getCode())) {
            throw new EntityExistsException("Faculty with code " + request.getCode() + " already exists");
        }
        
        Faculty faculty = facultyMapper.toEntity(request);
        Faculty savedFaculty = facultyRepository.save(faculty);
        
        log.info("Faculty created successfully with id: {}", savedFaculty.getId());
        return facultyMapper.toResponse(savedFaculty);
    }
    
    @Override
    public FacultyResponse updateFaculty(UUID id, FacultyRequest request) {
        log.info("Updating faculty with id: {}", id);
        
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + id));
        
        if (request.getCode() != null && !request.getCode().equals(faculty.getCode()) && facultyRepository.existsByCode(request.getCode())) {
                throw new EntityExistsException("Faculty with code " + request.getCode() + " already exists");
        }

        facultyMapper.updateEntityFromRequest(request, faculty);
        Faculty updatedFaculty = facultyRepository.save(faculty);
        
        log.info("Faculty updated successfully with id: {}", id);
        return facultyMapper.toResponse(updatedFaculty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FacultyResponse getFacultyById(UUID id) {
        log.info("Getting faculty with id: {}", id);
        
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + id));
        
        return facultyMapper.toResponse(faculty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FacultyResponse getFacultyByCode(String code) {
        log.info("Getting faculty with code: {}", code);
        
        Faculty faculty = facultyRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with code: " + code));
        
        return facultyMapper.toResponse(faculty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponse> getAllFaculties() {
        log.info("Getting all faculties");
        
        return facultyRepository.findAll().stream()
                .map(facultyMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<FacultyResponse> getAllFaculties(int page, int size) {
        log.info("Getting all faculties with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Faculty> facultyPage = facultyRepository.findAll(pageable);
        
        Page<FacultyResponse> responsePage = facultyPage.map(facultyMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteFaculty(UUID id) {
        log.info("Deleting faculty with id: {}", id);
        
        if (!facultyRepository.existsById(id)) {
            throw new EntityNotFoundException("Faculty not found with id: " + id);
        }
        
        facultyRepository.deleteById(id);
        log.info("Faculty deleted successfully with id: {}", id);
    }
}

