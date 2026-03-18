package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SemesterRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;
import com.hcmut.lms.coursemanagement.application.mapper.SemesterMapper;
import com.hcmut.lms.coursemanagement.application.service.SemesterService;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SemesterServiceImpl implements SemesterService {
    
    private final SemesterRepository semesterRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SemesterMapper semesterMapper;
    
    @Override
    public SemesterResponse createSemester(SemesterRequest request) {
        log.info("Creating semester: {}", request.getSemesterCode());
        
        Semester semester = semesterMapper.toEntity(request);
        
        if (request.getAcademicYearId() != null) {
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new EntityNotFoundException("Academic year not found with id: " + request.getAcademicYearId()));
            semester.setAcademicYear(academicYear);

            semester.validateAgainstAcademicYear(academicYear);
        }
        
        Semester savedSemester = semesterRepository.save(semester);
        
        log.info("Semester created successfully with id: {}", savedSemester.getId());
        return semesterMapper.toResponse(savedSemester);
    }
    
    @Override
    public SemesterResponse updateSemester(UUID id, SemesterRequest request) {
        log.info("Updating semester with id: {}", id);
        
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + id));
        
        semesterMapper.updateEntityFromRequest(request, semester);
        
        if (request.getAcademicYearId() != null) {
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new EntityNotFoundException("Academic year not found with id: " + request.getAcademicYearId()));
            semester.setAcademicYear(academicYear);

            semester.validateAgainstAcademicYear(academicYear);
        }
        
        Semester updatedSemester = semesterRepository.save(semester);
        
        log.info("Semester updated successfully with id: {}", id);
        return semesterMapper.toResponse(updatedSemester);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SemesterResponse getSemesterById(UUID id) {
        log.info("Getting semester with id: {}", id);
        
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + id));
        
        return semesterMapper.toResponse(semester);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SemesterResponse> getAllSemesters() {
        log.info("Getting all semesters");
        
        return semesterRepository.findAll().stream()
                .map(semesterMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<SemesterResponse> getAllSemesters(int page, int size) {
        log.info("Getting all semesters with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("semesterCode").descending());
        Page<Semester> semesterPage = semesterRepository.findAll(pageable);
        
        Page<SemesterResponse> responsePage = semesterPage.map(semesterMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SemesterResponse> getSemestersByAcademicYearId(UUID academicYearId) {
        log.info("Getting semesters by academic year id: {}", academicYearId);
        
        return semesterRepository.findByAcademicYearId(academicYearId).stream()
                .map(semesterMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<SemesterResponse> getSemestersByAcademicYearId(UUID academicYearId, int page, int size) {
        log.info("Getting semesters by academic year id: {} with pagination - page: {}, size: {}", academicYearId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Semester> semesterPage = semesterRepository.findByAcademicYearId(academicYearId, pageable);
        
        Page<SemesterResponse> responsePage = semesterPage.map(semesterMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteSemester(UUID id) {
        log.info("Deleting semester with id: {}", id);

        if (!semesterRepository.existsById(id)) {
            throw new EntityNotFoundException("Semester not found with id: " + id);
        }

        semesterRepository.deleteById(id);
        log.info("Semester deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SemesterResponse getCurrentSemester() {
        log.info("Getting current semester");
        Semester semester = semesterRepository.findCurrentSemester(LocalDate.now())
                .orElseThrow(() -> new EntityNotFoundException("No active semester found for current date"));
        return semesterMapper.toResponse(semester);
    }
}


