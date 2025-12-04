package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.mapper.ClassSectionMapper;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
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
public class ClassSectionServiceImpl implements ClassSectionService {
    
    private final ClassSectionRepository classSectionRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final ClassSectionMapper classSectionMapper;
    
    @Override
    public ClassSectionResponse createClassSection(CurrentUserInfo currentUser, ClassSectionRequest request) {
        log.info("Creating class section: {}", request.getSectionName());
        
        ClassSection classSection = classSectionMapper.toEntity(request);

        classSection.setCreatedBy(currentUser.getId());

        if (request.getSubjectId() != null && request.getSemesterId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + request.getSubjectId()));
            classSection.setSubject(subject);

            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + request.getSemesterId()));
            classSection.setSemester(semester);

            classSection.setIsOfficial(false);
        }else {
            classSection.setIsOfficial(true);
            classSection.setTeacherId(currentUser.getId());
        }

        if(request.getTeacherId()!=null){
            request.setTeacherId(request.getTeacherId());
        }
        
        ClassSection savedClassSection = classSectionRepository.save(classSection);
        
        log.info("Class section created successfully with id: {}", savedClassSection.getId());
        return classSectionMapper.toResponseDTO(savedClassSection);
    }
    
    @Override
    public ClassSectionResponse updateClassSection(UUID id, ClassSectionRequest request) {
        log.info("Updating class section with id: {}", id);
        
        ClassSection classSection = classSectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));
        
        classSectionMapper.updateEntityFromDTO(request, classSection);
        
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + request.getSubjectId()));
            classSection.setSubject(subject);
        }
        
        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + request.getSemesterId()));
            classSection.setSemester(semester);
        }
        
        ClassSection updatedClassSection = classSectionRepository.save(classSection);
        
        log.info("Class section updated successfully with id: {}", id);
        return classSectionMapper.toResponseDTO(updatedClassSection);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassSectionResponse getClassSectionById(UUID id) {
        log.info("Getting class section with id: {}", id);
        
        ClassSection classSection = classSectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));
        
        return classSectionMapper.toResponseDTO(classSection);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> getAllClassSections() {
        log.info("Getting all class sections");
        
        return classSectionRepository.findAll().stream()
                .map(classSectionMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getAllClassSections(int page, int size) {
        log.info("Getting all class sections with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findAll(pageable);
        
        Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> getClassSectionsBySubjectId(UUID subjectId) {
        log.info("Getting class sections by subject id: {}", subjectId);
        
        return classSectionRepository.findBySubjectId(subjectId).stream()
                .map(classSectionMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getClassSectionsBySubjectId(UUID subjectId, int page, int size) {
        log.info("Getting class sections by subject id: {} with pagination - page: {}, size: {}", subjectId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findBySubjectId(subjectId, pageable);
        
        Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> getClassSectionsBySemesterId(UUID semesterId) {
        log.info("Getting class sections by semester id: {}", semesterId);
        
        return classSectionRepository.findBySemesterId(semesterId).stream()
                .map(classSectionMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getClassSectionsBySemesterId(UUID semesterId, int page, int size) {
        log.info("Getting class sections by semester id: {} with pagination - page: {}, size: {}", semesterId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findBySemesterId(semesterId, pageable);
        
        Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> getClassSectionsByTeacherId(UUID teacherId) {
        log.info("Getting class sections by teacher id: {}", teacherId);
        
        return classSectionRepository.findByTeacherId(teacherId).stream()
                .map(classSectionMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getClassSectionsByTeacherId(UUID teacherId, int page, int size) {
        log.info("Getting class sections by teacher id: {} with pagination - page: {}, size: {}", teacherId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findByTeacherId(teacherId, pageable);
        
        Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteClassSection(UUID id) {
        log.info("Deleting class section with id: {}", id);
        
        if (!classSectionRepository.existsById(id)) {
            throw new EntityNotFoundException("Class section not found with id: " + id);
        }
        
        classSectionRepository.deleteById(id);
        log.info("Class section deleted successfully with id: {}", id);
    }

    @Override
    public ClassSectionResponse assignTeacherToClassSection(UUID id, ClassSectionRequest request) {
        log.info("Assign teacher {} to class {}",request.getTeacherId(), id);

        ClassSection classSection = classSectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));

        classSection.setTeacherId(request.getTeacherId());

        ClassSection updatedClassSection = classSectionRepository.save(classSection);

        log.info("Assign successfully");
        return classSectionMapper.toResponseDTO(updatedClassSection);
    }
}

