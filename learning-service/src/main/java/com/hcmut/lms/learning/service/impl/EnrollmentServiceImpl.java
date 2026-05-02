package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.BatchClassLookupRequest;
import com.hcmut.lms.learning.client.dto.ClassEnrollStatus;
import com.hcmut.lms.learning.client.dto.ClassResponse;
import com.hcmut.lms.learning.dto.internal.InternalClassStudentIdsResponse;
import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.EnrolledClassCardResponse;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentResponse;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentWithSubjectResponse;
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

import java.math.BigDecimal;
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
      throw new BusinessException("CAN_NOT_ENROL");
    }

    if (classEnrollStatus.isFull()) {
      throw new BusinessException("CLASS_FULL");
    }

    // Create enrollment
    Enrollment enrollment = enrollmentMapper.toEntity(request);
    enrollment.setEnrolledAt(LocalDateTime.now());
    enrollment.setProgressPercentage(0.0);

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
  public PageResponse<EnrolledClassCardResponse> getEnrolledClasses(
      UUID studentId, String semesterCode,
      String searchTerm, int page, int size) {
    log.info(
        "Getting enrolled classes for student {} with semester={}, search={}", studentId, semesterCode,
        searchTerm);

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
    List<UUID> classIds = allEnrollments.stream().map(Enrollment::getClassId).collect(Collectors.toList());

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
        .toList();

    // Step 6: Apply pagination manually
    int totalElements = filteredEnrollments.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);
    int start = page * size;
    int end = Math.min(start + size, totalElements);

    List<Enrollment> pagedEnrollments = start < totalElements ? filteredEnrollments.subList(
        start, end) : Collections.emptyList();

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
    enrollment.setProgressPercentage(
        enrollment.getProgressPercentage() + 1.0 / courseManagementClient.countNumberLecturesByClassId(classId));
    if (enrollment.getProgressPercentage() >= 1) {
      enrollment.setProgressPercentage(1.0);
      enrollment.setCompletionTime(LocalDateTime.now());
    }
    enrollmentRepository.save(enrollment);
  }

  @Override
  @Transactional
  public EnrollmentResponse changeClass(UUID id, EnrollmentRequest enrollmentRequest) {
    Enrollment enrollment = enrollmentRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Enrollment with id " + id + " not found"));
    // Check if already enrolled
    if (enrollmentRepository.existsByStudentIdAndClassId(
        enrollmentRequest.getStudentId(), enrollmentRequest.getClassId())) {
      throw new BusinessException("ALREADY_ENROLLED");
    }

    // Check class validity
    ClassEnrollStatus classEnrollStatus = courseManagementClient.getEnrollmentStatus(enrollmentRequest.getClassId());

    if (!classEnrollStatus.exist()) {
      throw new EntityNotFoundException("CLASS_NOT_FOUND");
    }

    if (!classEnrollStatus.canEnroll()) {
      throw new BusinessException("CAN_NOT_ENROL");
    }

    if (classEnrollStatus.isFull()) {
      throw new BusinessException("CLASS_FULL");
    }

    enrollment.setClassId(enrollmentRequest.getClassId());

    Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

    // Update current students count in course-management-service
    try {
      courseManagementClient.decrementCurrentStudents(enrollment.getClassId());
      courseManagementClient.incrementCurrentStudents(savedEnrollment.getClassId());
    } catch (Exception e) {
      log.warn("Failed to increment current students for class {}: {}", enrollmentRequest.getClassId(), e.getMessage());

    }
    return enrollmentMapper.toResponse(savedEnrollment);

  }

  @Override
  @Transactional
  public void unEnroll(UUID id) {
    log.info("Unenrolling enrollment with id {}", id);
    Enrollment enrollment = enrollmentRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Enrollment with id " + id + " not found"));
    enrollmentRepository.deleteById(id);

    courseManagementClient.decrementCurrentStudents(enrollment.getClassId());

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

  @Override
  @Transactional(readOnly = true)
  public List<StudentEnrollmentResponse> getStudentEnrollmentsWithSubjects(UUID studentId) {
    log.info("Fetching enrollments for student: {}", studentId);

    List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

    if (enrollments.isEmpty()) {
      log.info("No enrollments found for student: {}", studentId);
      return Collections.emptyList();
    }

    return enrollments.stream().map(enrollmentMapper::toStudentEnrollmentResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<StudentEnrollmentWithSubjectResponse> getStudentEnrollmentsWithSubjectIds(UUID studentId) {
    log.info("Fetching enrollments with subject IDs for student: {}", studentId);

    List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

    if (enrollments.isEmpty()) {
      return Collections.emptyList();
    }

    Set<UUID> classIds = enrollments.stream()
        .map(Enrollment::getClassId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Map<UUID, UUID> classIdToSubjectId = Collections.emptyMap();
    Map<UUID, String> classIdToGradingType = Collections.emptyMap();
    if (!classIds.isEmpty()) {
      try {
        List<ClassResponse> classes = courseManagementClient.getClassSectionsByIds(
            BatchClassLookupRequest.builder().classIds(new ArrayList<>(classIds)).build());
        classIdToSubjectId = classes.stream()
            .filter(c -> c.getId() != null && c.getSubjectId() != null)
            .collect(Collectors.toMap(ClassResponse::getId, ClassResponse::getSubjectId));
        classIdToGradingType = classes.stream()
            .filter(c -> c.getId() != null)
            .collect(Collectors.toMap(ClassResponse::getId, c -> c.getSubjectGradingType() != null ? c.getSubjectGradingType() : "GRADED"));
      } catch (Exception e) {
        log.warn("Failed to resolve class IDs to subject IDs: {}", e.getMessage());
      }
    }

    Map<UUID, UUID> finalClassIdToSubjectId = classIdToSubjectId;
    Map<UUID, String> finalClassIdToGradingType = classIdToGradingType;
    return enrollments.stream().map(enrollment -> StudentEnrollmentWithSubjectResponse.builder()
        .id(enrollment.getId())
        .studentId(enrollment.getStudentId())
        .classId(enrollment.getClassId())
        .subjectId(enrollment.getClassId() != null ? finalClassIdToSubjectId.get(enrollment.getClassId()) : null)
        .finalGrade(enrollment.getFinalGrade())
        .attemptNo(enrollment.getAttemptNo())
        .isPassed(enrollment.getIsPassed())
        .gradingType(enrollment.getClassId() != null ? finalClassIdToGradingType.get(enrollment.getClassId()) : "GRADED")
        .enrolledAt(enrollment.getEnrolledAt() != null ? enrollment.getEnrolledAt().toString() : null)
        .completionTime(enrollment.getCompletionTime() != null ? enrollment.getCompletionTime().toString() : null)
        .build()).collect(Collectors.toList());
  }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getStudentIdsByClassId(UUID classId) {
        return enrollmentRepository.findDistinctStudentIdsByClassId(classId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternalClassStudentIdsResponse> getStudentIdsByClassIds(List<UUID> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return List.of();
        }

        Map<UUID, List<UUID>> grouped = enrollmentRepository.findByClassIdIn(classIds).stream()
                .collect(Collectors.groupingBy(
                        Enrollment::getClassId,
                        Collectors.mapping(Enrollment::getStudentId, Collectors.toCollection(LinkedHashSet::new))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())
                ));

        return classIds.stream()
                .distinct()
                .map(classId -> InternalClassStudentIdsResponse.builder()
                        .classId(classId)
                        .studentIds(grouped.getOrDefault(classId, List.of()))
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getStudentIdsByCourseId(UUID courseId) {
        // In this codebase, class metadata exposes subjectId. We treat courseId input as subjectId.
        List<UUID> classIds = enrollmentRepository.findDistinctClassIds();
        if (classIds.isEmpty()) {
            return List.of();
        }

        List<ClassResponse> classSections = courseManagementClient.getClassSectionsByIds(
                BatchClassLookupRequest.builder()
                        .classIds(classIds)
                        .build()
        );

        Set<UUID> targetClassIds = classSections.stream()
                .filter(item -> courseId.equals(item.getSubjectId()))
                .map(ClassResponse::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (targetClassIds.isEmpty()) {
            return List.of();
        }

        return enrollmentRepository.findDistinctStudentIdsByClassIds(new ArrayList<>(targetClassIds));
    }
}
