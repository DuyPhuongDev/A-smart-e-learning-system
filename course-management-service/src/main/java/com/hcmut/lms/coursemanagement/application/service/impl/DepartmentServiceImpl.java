package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.DepartmentRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DepartmentResponse;
import com.hcmut.lms.coursemanagement.application.mapper.DepartmentMapper;
import com.hcmut.lms.coursemanagement.application.service.DepartmentService;
import com.hcmut.lms.coursemanagement.domain.entity.department.Department;
import com.hcmut.lms.coursemanagement.domain.entity.faculty.Faculty;
import com.hcmut.lms.coursemanagement.repository.DepartmentRepository;
import com.hcmut.lms.coursemanagement.repository.FacultyRepository;
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
public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentMapper departmentMapper;
    
    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        log.info("Creating department: {}", request.getName());
        
        Department department = departmentMapper.toEntity(request);
        
        if (request.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + request.getFacultyId()));
            department.setFaculty(faculty);
        }
        
        Department savedDepartment = departmentRepository.save(department);
        
        log.info("Department created successfully with id: {}", savedDepartment.getId());
        return departmentMapper.toResponse(savedDepartment);
    }
    
    @Override
    public DepartmentResponse updateDepartment(UUID id, DepartmentRequest request) {
        log.info("Updating department with id: {}", id);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        
        departmentMapper.updateEntityFromRequest(request, department);
        
        if (request.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + request.getFacultyId()));
            department.setFaculty(faculty);
        }
        
        Department updatedDepartment = departmentRepository.save(department);
        
        log.info("Department updated successfully with id: {}", id);
        return departmentMapper.toResponse(updatedDepartment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(UUID id) {
        log.info("Getting department with id: {}", id);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        
        return departmentMapper.toResponse(department);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        log.info("Getting all departments");
        
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> getAllDepartments(int page, int size) {
        log.info("Getting all departments with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Department> departmentPage = departmentRepository.findAll(pageable);
        
        Page<DepartmentResponse> responsePage = departmentPage.map(departmentMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentsByFacultyId(UUID facultyId) {
        log.info("Getting departments by faculty id: {}", facultyId);
        
        return departmentRepository.findByFacultyId(facultyId).stream()
                .map(departmentMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> getDepartmentsByFacultyId(UUID facultyId, int page, int size) {
        log.info("Getting departments by faculty id: {} with pagination - page: {}, size: {}", facultyId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Department> departmentPage = departmentRepository.findByFacultyId(facultyId, pageable);
        
        Page<DepartmentResponse> responsePage = departmentPage.map(departmentMapper::toResponse);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    public void deleteDepartment(UUID id) {
        log.info("Deleting department with id: {}", id);
        
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found with id: " + id);
        }
        
        departmentRepository.deleteById(id);
        log.info("Department deleted successfully with id: {}", id);
    }

    @Override
    public DepartmentResponse getDepartmentBySpecialization(UUID specializationId) {
        Department department = departmentRepository.findBySpecializationId(specializationId);
        return departmentMapper.toResponse(department);
    }
}

