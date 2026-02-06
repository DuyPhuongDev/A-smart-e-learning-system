package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.BatchClassLookupRequest;
import com.hcmut.lms.learning.client.dto.ClassEnrollStatus;
import com.hcmut.lms.learning.client.dto.ClassResponse;
import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.EnrolledClassCardResponse;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.exception.BusinessException;
import com.hcmut.lms.learning.mapper.EnrollmentMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.service.EnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final CourseManagementClient courseManagementClient;

    @Override
    @Transactional
    public EnrollmentResponse makeEnrollment(EnrollmentRequest request) {
        log.info("Processing enrollment for student {} in class {}", request.getStudentId(), request.getClassId());
        
        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndClassId(request.getStudentId(), request.getClassId())) {
            throw new BusinessException("ALREADY_ENROLLED");
        }

        // Check class validity
        ClassEnrollStatus classEnrollStatus = courseManagementClient.getEnrollmentStatus(request.getClassId());

        if (!classEnrollStatus.exist()) {
            throw new EntityNotFoundException("CLASS_NOT_FOUND");
        }

        if (!classEnrollStatus.canEnroll()) {
            throw new BusinessException("CLASS_NOT_OPEN");
        }

        if (classEnrollStatus.isFull()) {
            throw new BusinessException("CLASS_FULL");
        }

        // Create enrollment
        Enrollment enrollment = enrollmentMapper.toEntity(request);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setProgressPercentage(0.0);
        enrollment.setAttemptNo(1);
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        // Update current students count in course-management-service
        try {
            courseManagementClient.incrementCurrentStudents(request.getClassId());
        } catch (Exception e) {
            log.warn("Failed to increment current students for class {}: {}", request.getClassId(), e.getMessage());
            // Don't fail the enrollment if this fails - it can be reconciled later
        }
        
        log.info("Enrollment successful for student {} in class {}", request.getStudentId(), request.getClassId());
        return enrollmentMapper.toResponse(savedEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EnrolledClassCardResponse> getEnrolledClasses(UUID studentId, String semesterCode, 
                                                                      String searchTerm, int page, int size) {
        log.info("Getting enrolled classes for student {} with semester={}, search={}", 
                studentId, semesterCode, searchTerm);
        
        // Step 1: Get all enrollments for the student (we need all to apply server-side filtering)
        List<Enrollment> allEnrollments = enrollmentRepository.findByStudentId(studentId);
        
        if (allEnrollments.isEmpty()) {
            return PageResponse.<EnrolledClassCardResponse>builder()
                    .content(Collections.emptyList())
                    .pageNumber(page)
                    .pageSize(size)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .empty(true)
                    .build();
        }
        
        // Step 2: Get class IDs
        List<UUID> classIds = allEnrollments.stream()
                .map(Enrollment::getClassId)
                .collect(Collectors.toList());
        
        // Step 3: Fetch class info from course-management-service with filters
        BatchClassLookupRequest lookupRequest = BatchClassLookupRequest.builder()
                .classIds(classIds)
                .semesterCode(semesterCode)
                .searchTerm(searchTerm)
                .build();
        
        List<ClassResponse> classSections = courseManagementClient.getClassSectionsByIds(lookupRequest);
        
        // Step 4: Create a map for quick lookup
        Map<UUID, ClassResponse> classInfoMap = classSections.stream()
                .collect(Collectors.toMap(ClassResponse::getId, Function.identity()));
        
        // Step 5: Filter enrollments to only those with matching class sections
        List<Enrollment> filteredEnrollments = allEnrollments.stream()
                .filter(e -> classInfoMap.containsKey(e.getClassId()))
                .sorted(Comparator.comparing(Enrollment::getEnrolledAt).reversed())
                .collect(Collectors.toList());
        
        // Step 6: Apply pagination manually
        int totalElements = filteredEnrollments.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, totalElements);
        
        List<Enrollment> pagedEnrollments = start < totalElements 
                ? filteredEnrollments.subList(start, end) 
                : Collections.emptyList();
        
        // Step 7: Map to response DTOs
        List<EnrolledClassCardResponse> content = pagedEnrollments.stream()
                .map(enrollment -> mapToCardResponse(enrollment, classInfoMap.get(enrollment.getClassId())))
                .collect(Collectors.toList());
        
        return PageResponse.<EnrolledClassCardResponse>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .empty(content.isEmpty())
                .build();
    }

    @Override
    public boolean isEnrolled(UUID studentId, UUID classId) {
        return enrollmentRepository.existsByStudentIdAndClassId(studentId, classId);
    }

    @Override
    @Transactional
    public void updateProgress(UUID studentId, UUID classId) {
        // get enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndClassId(studentId, classId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment with class id: " + classId + " not found"));

        // update percent when lecture done
        enrollment.setProgressPercentage(enrollment.getProgressPercentage() + 1.0 /courseManagementClient.countNumberLecturesByClassId(classId));
        if(enrollment.getProgressPercentage() >= 1) enrollment.setCompletionTime(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    /**
     * Map enrollment and class info to card response
     */
    private EnrolledClassCardResponse mapToCardResponse(Enrollment enrollment, ClassResponse classInfo) {
        return EnrolledClassCardResponse.builder()
                // Enrollment info
                .enrollmentId(enrollment.getId())
                .enrolledAt(enrollment.getEnrolledAt())
                .progressPercentage(enrollment.getProgressPercentage())
                .finalGrade(enrollment.getFinalGrade())
                .attemptNo(enrollment.getAttemptNo())
                // Class info
                .id(classInfo.getId())
                .sectionName(classInfo.getSectionName())
                .code(classInfo.getCode())
                .status(classInfo.getStatus())
                .description(classInfo.getDescription())
                .thumbnailUrl(classInfo.getThumbnailUrl())
                .durationHours(classInfo.getDurationHours())
                .level(classInfo.getLevel())
                .language(classInfo.getLanguage())
                // Subject info
                .subjectId(classInfo.getSubjectId())
                .subjectName(classInfo.getSubjectName())
                // Semester info
                .semesterId(classInfo.getSemesterId())
                .semesterCode(classInfo.getSemesterCode())
                // Teacher info
                .teacherId(classInfo.getTeacherId())
                .teacherName(classInfo.getTeacherName())
                .createdAt(classInfo.getCreatedAt())
                .build();
    }
}
