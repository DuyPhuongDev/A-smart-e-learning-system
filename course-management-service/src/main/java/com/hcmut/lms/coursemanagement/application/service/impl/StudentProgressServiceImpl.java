package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.util.StudentGradeUtil;
import com.hcmut.lms.coursemanagement.application.config.CurriculumFallbackConfig;
import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentLearningProgressResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentSubjectDetailResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import com.hcmut.lms.coursemanagement.application.service.StudentProgressService;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.client.LearningServiceClient;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSection;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubjectPriority;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingType;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectLearningOutcome;
import com.hcmut.lms.coursemanagement.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.hcmut.lms.coursemanagement.util.SemesterUtil.computeSemKeyFromSemesterCode;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentProgressServiceImpl implements StudentProgressService {

  private final UserServiceClient userServiceClient;
  private final LearningServiceClient learningServiceClient;
  private final CurriculumRepository curriculumRepository;
  private final CurriculumSectionRepository curriculumSectionRepository;
  private final CurriculumSubjectRepository curriculumSubjectRepository;
  private final SubjectLearningOutcomeRepository subjectLearningOutcomeRepository;
  private final ClassSectionService classSectionService;
  private final SpecializationRepository specializationRepository;
  private final CurriculumSubjectPriorityRepository curriculumSubjectPriorityRepository;
  private final SubjectRepository subjectRepository;
  private final SemesterRepository semesterRepository;
  private final AcademicYearRepository academicYearRepository;
  private final CurriculumFallbackConfig curriculumFallbackConfig;
  private final CurriculumSubjectAllocationService allocationService;
  private final StudentProgressGradeUtil gradeUtil;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public StudentLearningProgressResponse getStudentLearningProgress(UUID userId, UUID specializationId) {
    log.info("Fetching student learning progress for userId: {}, specializationId: {}", userId, specializationId);

    UserResponse user = userServiceClient.getUserById(userId);
    if (user == null || user.getStudentCode() == null) {
      throw new EntityNotFoundException("Student not found with userId: " + userId);
    }

    // Use provided specializationId, or fall back to user's default
    UUID effectiveSpecializationId = specializationId != null ? specializationId : user.getSpecializationId();

    if (user.getIntakeYearId() == null || effectiveSpecializationId == null) {
      throw new EntityNotFoundException("Student intake year or specialization not found for userId: " + userId);
    }

    // Fetch enrollments from learning-service (only basic enrollment data)
    List<StudentEnrollmentResponse> enrollments = learningServiceClient.getStudentEnrollments(userId);
    for (StudentEnrollmentResponse enrollment : enrollments) {
      log.info(
          "Enrollment - classId: {}, finalGrade: {}, isPassed: {}, attemptNo: {}", enrollment.getClassId(),
          enrollment.getFinalGrade(), enrollment.getIsPassed(), enrollment.getAttemptNo());
    }

    // Fetch class information to map classId to subjectId
    List<UUID> classIds = enrollments.stream()
        .map(StudentEnrollmentResponse::getClassId)
        .distinct()
        .collect(Collectors.toList());

    Map<UUID, ClassSectionResponse> classMap = new HashMap<>();
    if (!classIds.isEmpty()) {
      classMap = classSectionService.getClassSectionsByIds(new BatchClassLookupRequest(classIds))
          .stream()
          .collect(Collectors.toMap(ClassSectionResponse::getId, c -> c));
    }

    // Map enrollments by subjectId - keep ALL attempts per subject and track the best result
    Map<UUID, List<StudentEnrollmentResponse>> enrollmentsBySubjectMap = new HashMap<>();
    Map<UUID, StudentEnrollmentResponse> bestEnrollmentBySubjectMap = new HashMap<>();
    for (StudentEnrollmentResponse enrollment : enrollments) {
      ClassSectionResponse classSection = classMap.get(enrollment.getClassId());
      if (classSection != null && classSection.getSubjectId() != null) {
        UUID subjectId = classSection.getSubjectId();
        enrollmentsBySubjectMap.computeIfAbsent(subjectId, k -> new ArrayList<>()).add(enrollment);

        StudentEnrollmentResponse existingBest = bestEnrollmentBySubjectMap.get(subjectId);
        if (existingBest == null || isShouldReplace(enrollment, existingBest, classMap)) {
          bestEnrollmentBySubjectMap.put(subjectId, enrollment);
        }
      }
    }

    // Compute attemptNo per enrollment based on semester chronological order
    Map<UUID, Integer> attemptNoByClassId = computeAttemptNumbers(enrollmentsBySubjectMap, classMap);

    Map<UUID, SubjectProgressMeta> subjectProgressMetaByClassId = buildSubjectProgressMetaByClassId(
        enrollmentsBySubjectMap, classMap);

    Curriculum curriculum = curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(
        effectiveSpecializationId, user.getIntakeYearId()).orElseGet(() -> {
      UUID fallbackId = resolveFallbackIntakeYearId(
          "no curriculum for specializationId=" + effectiveSpecializationId + " intakeYearId=" + user.getIntakeYearId(),
          userId);
      return curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(
              effectiveSpecializationId, fallbackId)
          .orElseThrow(() -> new EntityNotFoundException("Curriculum not found for student userId=" + userId));
    });

    List<CurriculumSection> sections = curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(
        curriculum.getId().getSpecializationId(), curriculum.getId().getIntakeYearId());

    // Fetch all subject grading types to avoid N+1 queries
    Set<UUID> subjectIds = sections.stream()
        .flatMap(section -> section.getCurriculumSubjects().stream())
        .map(cs -> cs.getSubject().getId())
        .collect(Collectors.toSet());

    Map<UUID, SubjectGradingType> subjectGradingTypeMap = subjectRepository.findAllById(subjectIds)
        .stream()
        .collect(Collectors.toMap(
            Subject::getId,
            s -> s.getGradingType() != null ? s.getGradingType() : SubjectGradingType.GRADED));

    StudentLearningProgressResponse.StudentProgramInfo programInfo = buildProgramInfo(user, curriculum);

    CurriculumSubjectAllocationService.SectionAllocationResult allocationResult =
        allocationService.allocateSubjectsToSections(
        sections, bestEnrollmentBySubjectMap, subjectGradingTypeMap);

    StudentLearningProgressResponse.StudentLearningSummary summary = calculateSummary(
        sections, bestEnrollmentBySubjectMap, subjectGradingTypeMap, allocationResult);
    List<StudentLearningProgressResponse.StudentProgressSectionItem> sectionItems = buildSectionItems(
        sections, enrollmentsBySubjectMap, bestEnrollmentBySubjectMap, subjectGradingTypeMap,
        subjectProgressMetaByClassId, attemptNoByClassId, allocationResult);

    log.info("Successfully fetched student learning progress for userId: {}", userId);

    return StudentLearningProgressResponse.builder()
        .programInfo(programInfo)
        .summary(summary)
        .sections(sectionItems)
        .updatedAt(LocalDateTime.now().format(DATE_FORMATTER))
        .build();
  }

  private static boolean isShouldReplace(
      StudentEnrollmentResponse enrollment, StudentEnrollmentResponse existing,
      Map<UUID, ClassSectionResponse> classMap) {
    if (existing.getFinalGrade() == null && enrollment.getFinalGrade() != null) {
      return true;
    }
    if (existing.getFinalGrade() != null && enrollment.getFinalGrade() != null) {
      if (enrollment.getFinalGrade() > existing.getFinalGrade()) {
        return true;
      }
      if (enrollment.getFinalGrade().equals(existing.getFinalGrade())) {
        // Tiebreaker: later semester (higher semKey) is preferred
        Integer enrollKey = classMap.get(enrollment.getClassId()) != null ? computeSemKeyFromSemesterCode(
            classMap.get(enrollment.getClassId()).getSemesterCode()) : null;
        Integer existingKey = classMap.get(existing.getClassId()) != null ? computeSemKeyFromSemesterCode(
            classMap.get(existing.getClassId()).getSemesterCode()) : null;
        if (enrollKey != null && existingKey != null) {
          return enrollKey > existingKey;
        }
      }
    }
    return false;
  }

  @Override
  public StudentSubjectDetailResponse getStudentSubjectDetail(UUID subjectId, UUID userId) {
    log.info("Fetching subject detail for subjectId: {}", subjectId);
    UserResponse user = userServiceClient.getUserById(userId);

    if (user == null || user.getStudentCode() == null) {
      throw new EntityNotFoundException("Student not found with userId: " + userId);
    }

    if (user.getIntakeYearId() == null) {
      throw new EntityNotFoundException("Student intake year not found for userId: " + userId);
    }

    if (user.getSpecializationId() == null) {
      Specialization curSpecialization = specializationRepository.findByDepartmentId(user.getDepartmentId()).getFirst();
      user.setSpecializationId(curSpecialization.getId());
    }

    // Fetch curriculum subject filtered by student's curriculum
    CurriculumSubject curriculumSubject = curriculumSubjectRepository.findBySubjectIdAndCurriculum(
        subjectId,
        user.getSpecializationId(), user.getIntakeYearId()).orElseGet(() -> {
      UUID fallbackId = resolveFallbackIntakeYearId(
          "no curriculum subject for specializationId=" + user.getSpecializationId() + " intakeYearId=" + user.getIntakeYearId(),
          userId);
      return curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, user.getSpecializationId(), fallbackId)
          .orElseThrow(
              () -> new EntityNotFoundException("Subject not found with id: " + subjectId + " in fallback curriculum"));
    });

    // Fetch relations separately using the composite ID
    CurriculumSubject csWithPrerequisites = curriculumSubjectRepository.findByIdWithPrerequisites(
        curriculumSubject.getId().getCurriculumSectionId(), curriculumSubject.getId().getSubjectId(),
        curriculumSubject.getId().getId()).orElse(curriculumSubject);

    CurriculumSubject csWithRecommendations = curriculumSubjectRepository.findByIdWithRecommendations(
        curriculumSubject.getId().getCurriculumSectionId(), curriculumSubject.getId().getSubjectId(),
        curriculumSubject.getId().getId()).orElse(curriculumSubject);

    CurriculumSubject csWithParallels = curriculumSubjectRepository.findByIdWithParallels(
        curriculumSubject.getId().getCurriculumSectionId(), curriculumSubject.getId().getSubjectId(),
        curriculumSubject.getId().getId()).orElse(curriculumSubject);

    List<SubjectLearningOutcome> learningOutcomes =
        subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(
        subjectId);

    // Fetch priority information for recommended year/semester
    Integer recommendedYear = null;
    Integer recommendedSemester = null;

    Optional<CurriculumSubjectPriority> priorityOpt = curriculumSubjectPriorityRepository.findByCompositeId(
        curriculumSubject.getId().getSubjectId(), curriculumSubject.getId().getCurriculumSectionId(),
        curriculumSubject.getId().getId());

    if (priorityOpt.isPresent()) {
      CurriculumSubjectPriority priority = priorityOpt.get();
      recommendedYear = priority.getRecommendedYear();
      recommendedSemester = priority.getRecommendedSemesterInYear();
    }

    // Fetch all enrollments for this subject
    List<StudentEnrollmentResponse> enrollments = learningServiceClient.getStudentEnrollments(userId);

    // Get class information to map classId to subjectId
    List<UUID> classIds = enrollments.stream()
        .map(StudentEnrollmentResponse::getClassId)
        .distinct()
        .collect(Collectors.toList());

    Map<UUID, ClassSectionResponse> classMap;
    if (!classIds.isEmpty()) {
      classMap = classSectionService.getClassSectionsByIds(new BatchClassLookupRequest(classIds))
          .stream()
          .collect(Collectors.toMap(ClassSectionResponse::getId, c -> c));
    } else {
      classMap = new HashMap<>();
    }

    // Fetch subject grading type
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + subjectId));
    SubjectGradingType gradingType = subject.getGradingType() != null ? subject.getGradingType() :
        SubjectGradingType.GRADED;

    // Filter enrollments for this specific subject and build attempts
    List<StudentEnrollmentResponse> subjectEnrollments = enrollments.stream().filter(enrollment -> {
      ClassSectionResponse classSection = classMap.get(enrollment.getClassId());
      return classSection != null && subjectId.equals(classSection.getSubjectId());
    }).sorted((e1, e2) -> {
      // Sort by semester chronological order ascending
      ClassSectionResponse cs1 = classMap.get(e1.getClassId());
      ClassSectionResponse cs2 = classMap.get(e2.getClassId());
      Integer key1 = cs1 != null ? computeSemKeyFromSemesterCode(cs1.getSemesterCode()) : null;
      Integer key2 = cs2 != null ? computeSemKeyFromSemesterCode(cs2.getSemesterCode()) : null;
      if (key1 != null && key2 != null) return key1.compareTo(key2);
      return 0;
    }).toList();

    // Build attempt items with computed attemptNo based on semester order
    List<StudentSubjectDetailResponse.StudentSubjectAttemptItem> attempts = new ArrayList<>();
    for (int i = 0; i < subjectEnrollments.size(); i++) {
      StudentEnrollmentResponse enrollment = subjectEnrollments.get(i);
      ClassSectionResponse classSection = classMap.get(enrollment.getClassId());
      Double grade10 = enrollment.getFinalGrade();
      String letterGrade = grade10 != null ? StudentGradeUtil.convertToLetterGrade(grade10) : null;
      Double grade4 = grade10 != null ? StudentGradeUtil.convertTo4Scale(grade10) : null;
      Boolean isPassed = gradeUtil.isStudentPassedSubject(enrollment, gradingType);

      attempts.add(StudentSubjectDetailResponse.StudentSubjectAttemptItem.builder()
          .attemptNo(i + 1)
          .semesterLabel(classSection != null ? classSection.getSemesterCode() : null)
          .grade10(grade10)
          .letterGrade(letterGrade)
          .grade4(grade4)
          .isPassed(isPassed)
          .isApplied(null) // Will be determined by business logic
          .build());
    }

    // Compute overall pass status: true if any attempt passed; false if attempts to exist but none passed; null otherwise
    Boolean overallIsPassed = attempts.isEmpty() ? null
        : attempts.stream().anyMatch(a -> Boolean.TRUE.equals(a.getIsPassed()));

    // Determine which attempt is applied (best grade or most recent if passed)
    if (!attempts.isEmpty()) {
      // If grades are equal, prefer higher attemptNo (more recent)
      attempts.stream()
          .filter(a -> a.getGrade10() != null)
          .max(Comparator.comparingDouble(
                  (StudentSubjectDetailResponse.StudentSubjectAttemptItem a) -> a.getGrade10() != null ?
                      a.getGrade10() : 0.0)
              .thenComparingInt(a -> a.getAttemptNo() != null ? a.getAttemptNo() : 0))
          .ifPresent(bestAttempt -> bestAttempt.setIsApplied(true));

    }

    List<StudentSubjectDetailResponse.SubjectRelationItem> prerequisites = csWithPrerequisites.getPrerequisites()
        .stream()
        .map(p -> StudentSubjectDetailResponse.SubjectRelationItem.builder()
            .subjectId(p.getPrerequisiteCurriculumSubject().getSubject().getId().toString())
            .subjectCode(p.getPrerequisiteCurriculumSubject().getSubject().getCode())
            .subjectName(p.getPrerequisiteCurriculumSubject().getSubject().getName())
            .build())
        .collect(Collectors.toList());

    List<StudentSubjectDetailResponse.SubjectRelationItem> recommendations = csWithRecommendations.getRecommendations()
        .stream()
        .map(r -> StudentSubjectDetailResponse.SubjectRelationItem.builder()
            .subjectId(r.getRecommendedCurriculumSubject().getSubject().getId().toString())
            .subjectCode(r.getRecommendedCurriculumSubject().getSubject().getCode())
            .subjectName(r.getRecommendedCurriculumSubject().getSubject().getName())
            .build())
        .collect(Collectors.toList());

    List<StudentSubjectDetailResponse.SubjectRelationItem> parallels = csWithParallels.getParallels()
        .stream()
        .map(p -> StudentSubjectDetailResponse.SubjectRelationItem.builder()
            .subjectId(p.getParallelCurriculumSubject().getSubject().getId().toString())
            .subjectCode(p.getParallelCurriculumSubject().getSubject().getCode())
            .subjectName(p.getParallelCurriculumSubject().getSubject().getName())
            .build())
        .collect(Collectors.toList());

    List<StudentSubjectDetailResponse.SubjectLearningOutcomeItem> outcomeItems = learningOutcomes.stream()
        .filter(lo -> lo.getParent() == null)
        .map(this::buildLearningOutcomeItem)
        .collect(Collectors.toList());

    log.info("Successfully fetched subject detail for subjectId: {}", subjectId);

    return StudentSubjectDetailResponse.builder()
        .subjectId(subjectId.toString())
        .recommendedYear(recommendedYear)
        .recommendedSemester(recommendedSemester)
        .isPassed(overallIsPassed)
        .attempts(attempts)
        .prerequisites(prerequisites)
        .recommendations(recommendations)
        .parallels(parallels)
        .learningOutcomes(outcomeItems)
        .build();
  }

  private StudentSubjectDetailResponse.SubjectLearningOutcomeItem buildLearningOutcomeItem(
      SubjectLearningOutcome outcome) {
    List<StudentSubjectDetailResponse.SubjectLearningOutcomeItem> children = null;
    if (outcome.getChildren() != null && !outcome.getChildren().isEmpty()) {
      children = outcome.getChildren().stream().map(this::buildLearningOutcomeItem).collect(Collectors.toList());
    }

    return StudentSubjectDetailResponse.SubjectLearningOutcomeItem.builder()
        .code(outcome.getCode())
        .description(outcome.getDescription())
        .children(children)
        .build();
  }

  private StudentLearningProgressResponse.StudentProgramInfo buildProgramInfo(
      UserResponse user,
      Curriculum curriculum) {
    Integer curriculumYearValue = null;
    if (curriculum.getIntakeYear() != null && curriculum.getIntakeYear().getYearCode() != null) {
      try {
        int parsedYear = Integer.parseInt(curriculum.getIntakeYear().getYearCode().trim());
        curriculumYearValue = parsedYear < 100 ? parsedYear + 2000 : parsedYear;
      } catch (NumberFormatException e) {
        log.warn("Failed to parse year from yearCode: {}", curriculum.getIntakeYear().getYearCode());
      }
    }

    Integer studentIntakeYearValue = null;
    if (user.getIntakeYearId() != null) {
      studentIntakeYearValue = academicYearRepository.findById(user.getIntakeYearId())
          .map(AcademicYear::getYearCode)
          .map(yearCode -> {
            try {
              int parsedYear = Integer.parseInt(yearCode.trim());
              return parsedYear < 100 ? parsedYear + 2000 : parsedYear;
            } catch (NumberFormatException e) {
              log.warn("Failed to parse intake year from yearCode: {}", yearCode);
              return null;
            }
          })
          .orElse(null);
    }

    return StudentLearningProgressResponse.StudentProgramInfo.builder()
        .specializationId(curriculum.getSpecialization().getId().toString())
        .facultyName(curriculum.getSpecialization().getDepartment().getFaculty().getName())
        .specializationName(curriculum.getSpecialization().getName())
        .specializationCode(curriculum.getSpecialization().getCode())
        .curriculumCode(curriculum.getId().getCode())
        .curriculumYear(curriculumYearValue)
        .studentIntakeYear(studentIntakeYearValue)
        .studentName(user.getFullName())
        .studentCode(user.getStudentCode())
        .build();
  }

  private StudentLearningProgressResponse.StudentLearningSummary calculateSummary(
      List<CurriculumSection> sections, Map<UUID, StudentEnrollmentResponse> enrollmentMap,
      Map<UUID, SubjectGradingType> subjectGradingTypeMap,
      CurriculumSubjectAllocationService.SectionAllocationResult allocationResult) {

    int earnedCredits = 0;
    int requiredCredits = 0;
    double totalWeightedGrade10 = 0.0;
    double totalWeightedGrade4 = 0.0;
    int gpaDenominatorCredits = 0;

    for (CurriculumSection section : sections) {
      if (section.getRequiredCredits() != null) {
        requiredCredits += section.getRequiredCredits();
      }

      for (CurriculumSubject cs : section.getCurriculumSubjects()) {
        UUID subjectId = cs.getSubject().getId();
        if (!allocationResult.countedSubjectIds().contains(subjectId)) {
          continue;
        }

        StudentEnrollmentResponse enrollment = enrollmentMap.get(subjectId);
        if (enrollment == null) {
          continue;
        }

        SubjectGradingType gradingType = subjectGradingTypeMap.getOrDefault(subjectId, SubjectGradingType.GRADED);
        int credits = cs.getSubject().getCredits();

        Boolean isPassed = gradeUtil.isStudentPassedSubject(enrollment, gradingType);

        if (isPassed != null && isPassed && credits > 0) {
          earnedCredits += credits;
        }

        if (enrollment.getFinalGrade() != null && credits > 0) {
          double grade10 = enrollment.getFinalGrade();
          totalWeightedGrade10 += grade10 * credits;
          totalWeightedGrade4 += StudentGradeUtil.convertTo4Scale(grade10) * credits;
          gpaDenominatorCredits += credits;
        }
      }
    }

    int remainingCredits = requiredCredits - earnedCredits;

    double cumulativeGpa10 = gpaDenominatorCredits > 0 ? Math.round(
        totalWeightedGrade10 / gpaDenominatorCredits * 100.0) / 100.0 : 0.0;
    double cumulativeGpa4 = gpaDenominatorCredits > 0 ? Math.round(
        totalWeightedGrade4 / gpaDenominatorCredits * 100.0) / 100.0 : 0.0;

    return StudentLearningProgressResponse.StudentLearningSummary.builder()
        .earnedCredits(earnedCredits)
        .requiredCredits(requiredCredits)
        .remainingCredits(remainingCredits)
        .cumulativeGpa10(cumulativeGpa10)
        .cumulativeGpa4(cumulativeGpa4)
        .build();
  }

  private List<StudentLearningProgressResponse.StudentProgressSectionItem> buildSectionItems(
      List<CurriculumSection> sections, Map<UUID, List<StudentEnrollmentResponse>> enrollmentsBySubjectMap,
      Map<UUID, StudentEnrollmentResponse> bestEnrollmentBySubjectMap,
      Map<UUID, SubjectGradingType> subjectGradingTypeMap, Map<UUID, SubjectProgressMeta> subjectProgressMetaByClassId,
      Map<UUID, Integer> attemptNoByClassId,
      CurriculumSubjectAllocationService.SectionAllocationResult allocationResult) {

    CurriculumSection freeElectiveSection = sections.stream()
        .filter(s -> CurriculumSubjectAllocationService.FREE_ELECTIVE_SECTION_KEY.equals(
            CurriculumSubjectAllocationService.normalizeSectionName(s.getName())))
        .findFirst()
        .orElse(null);

    Map<UUID, Subject> subjectById = sections.stream()
        .flatMap(section -> section.getCurriculumSubjects().stream())
        .map(CurriculumSubject::getSubject)
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Subject::getId, s -> s, (a, b) -> a));

    return sections.stream().map(section -> {
      Integer effectiveCompleted = allocationResult.effectiveCompletedCreditsBySection().get(section.getId());
      int completedCredits = effectiveCompleted != null ? effectiveCompleted : 0;
      boolean isFreeElective = section == freeElectiveSection;
      int totalSectionCredits = 0;

      List<StudentLearningProgressResponse.StudentProgressSubjectItem> subjectItems = new ArrayList<>();

      for (CurriculumSubject cs : section.getCurriculumSubjects()) {
        totalSectionCredits += cs.getSubject().getCredits();
        UUID subjectId = cs.getSubject().getId();

        // Skip passed subjects that were moved to free elective (excess from this section)
        if (!isFreeElective && allocationResult.freeElectiveAcceptedExcessSubjectIds().contains(subjectId)) {
          continue;
        }

        List<StudentEnrollmentResponse> subjectEnrollments = enrollmentsBySubjectMap.getOrDefault(
            subjectId, Collections.emptyList());
        StudentEnrollmentResponse bestEnrollment = bestEnrollmentBySubjectMap.get(subjectId);
        SubjectGradingType gradingType = subjectGradingTypeMap.getOrDefault(subjectId, SubjectGradingType.GRADED);

        if (subjectEnrollments.isEmpty()) {
          // No attempts at all — emit one item with null grades
          subjectItems.add(StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
              .subjectId(subjectId.toString())
              .order(cs.getDisplayOrder())
              .subjectCode(cs.getSubject().getCode())
              .subjectName(cs.getSubject().getName())
              .credits(cs.getSubject().getCredits())
              .attemptNo(null)
              .isHighestResult(true)
              .build());
        } else {
          // Emit ALL attempts per subject, each tagged with isHighestResult
          for (StudentEnrollmentResponse enrollment : subjectEnrollments) {
            boolean isHighest = enrollment == bestEnrollment;
            Boolean isPassed = gradeUtil.isStudentPassedSubject(enrollment, gradingType);
            Double grade10 = enrollment.getFinalGrade();
            String letterGrade = grade10 != null ? StudentGradeUtil.convertToLetterGrade(grade10) : null;
            Double grade4 = grade10 != null ? StudentGradeUtil.convertTo4Scale(grade10) : null;
            Integer computedAttemptNo = attemptNoByClassId.get(enrollment.getClassId());
            SubjectProgressMeta meta = subjectProgressMetaByClassId.get(enrollment.getClassId());

            subjectItems.add(StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
                .subjectId(subjectId.toString())
                .order(isHighest ? cs.getDisplayOrder() : null)
                .semesterId(resolveSemesterId(meta))
                .academicYearId(resolveAcademicYearId(meta))
                .semesterCode(resolveSemesterCode(meta))
                .academicYear(resolveAcademicYear(meta))
                .semesterOrder(resolveSemesterOrder(meta))
                .academicYearOrder(resolveAcademicYearOrder(meta))
                .subjectCode(cs.getSubject().getCode())
                .subjectName(cs.getSubject().getName())
                .credits(cs.getSubject().getCredits())
                .grade10(grade10)
                .letterGrade(letterGrade)
                .grade4(grade4)
                .isPassed(isPassed)
                .attemptNo(computedAttemptNo)
                .isHighestResult(isHighest)
                .build());
          }
        }
      }

      // For free elective section: add excess subjects accepted from other sections
      if (isFreeElective && !allocationResult.freeElectiveAcceptedExcessSubjectIds().isEmpty()) {
        for (UUID excessSubjectId : allocationResult.freeElectiveAcceptedExcessSubjectIds()) {
          Subject subject = subjectById.get(excessSubjectId);
          if (subject == null) {
            continue;
          }
          totalSectionCredits += subject.getCredits();

          List<StudentEnrollmentResponse> subjectEnrollments = enrollmentsBySubjectMap.getOrDefault(
              excessSubjectId, Collections.emptyList());
          StudentEnrollmentResponse bestEnrollment = bestEnrollmentBySubjectMap.get(excessSubjectId);
          SubjectGradingType gradingType = subjectGradingTypeMap.getOrDefault(
              excessSubjectId, SubjectGradingType.GRADED);

          if (subjectEnrollments.isEmpty()) {
            subjectItems.add(StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
                .subjectId(excessSubjectId.toString())
                .order(null)
                .subjectCode(subject.getCode())
                .subjectName(subject.getName())
                .credits(subject.getCredits())
                .attemptNo(null)
                .isHighestResult(true)
                .build());
          } else {
            for (StudentEnrollmentResponse enrollment : subjectEnrollments) {
              boolean isHighest = enrollment == bestEnrollment;
              Boolean isPassed = gradeUtil.isStudentPassedSubject(enrollment, gradingType);
              Double grade10 = enrollment.getFinalGrade();
              String letterGrade = grade10 != null ? StudentGradeUtil.convertToLetterGrade(grade10) : null;
              Double grade4 = grade10 != null ? StudentGradeUtil.convertTo4Scale(grade10) : null;
              Integer computedAttemptNo = attemptNoByClassId.get(enrollment.getClassId());
              SubjectProgressMeta meta = subjectProgressMetaByClassId.get(enrollment.getClassId());

              subjectItems.add(StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
                  .subjectId(excessSubjectId.toString())
                  .order(null)
                  .semesterId(resolveSemesterId(meta))
                  .academicYearId(resolveAcademicYearId(meta))
                  .semesterCode(resolveSemesterCode(meta))
                  .academicYear(resolveAcademicYear(meta))
                  .semesterOrder(resolveSemesterOrder(meta))
                  .academicYearOrder(resolveAcademicYearOrder(meta))
                  .subjectCode(subject.getCode())
                  .subjectName(subject.getName())
                  .credits(subject.getCredits())
                  .grade10(grade10)
                  .letterGrade(letterGrade)
                  .grade4(grade4)
                  .isPassed(isPassed)
                  .attemptNo(computedAttemptNo)
                  .isHighestResult(isHighest)
                  .build());
            }
          }
        }
      }

      return StudentLearningProgressResponse.StudentProgressSectionItem.builder()
          .sectionId(section.getId().toString())
          .sectionName(section.getName())
          .displayOrder(section.getDisplayOrder())
          .isRequired(section.getRequiredCredits() != null && section.getRequiredCredits() > 0 || Objects.equals(
              section.getNotes(), "BB"))
          .requiredCredits(section.getRequiredCredits())
          .completedCredits(completedCredits)
          .totalSectionCredits(totalSectionCredits)
          .notes(section.getNotes())
          .subjects(subjectItems)
          .build();
    }).collect(Collectors.toList());
  }

  /**
   * Compute attemptNo per enrollment based on semester chronological order.
   * For each subject, enrollments are sorted by their class section's semester
   * and assigned attempt numbers 1, 2, 3... Returns a map from classId to computed attemptNo.
   */
  private Map<UUID, Integer> computeAttemptNumbers(
      Map<UUID, List<StudentEnrollmentResponse>> enrollmentsBySubjectMap,
      Map<UUID, ClassSectionResponse> classMap) {
    Map<UUID, Integer> attemptNoByClassId = new HashMap<>();
    for (var entry : enrollmentsBySubjectMap.entrySet()) {
      List<StudentEnrollmentResponse> sorted = entry.getValue().stream().sorted(Comparator.comparingInt(e -> {
        ClassSectionResponse cs = classMap.get(e.getClassId());
        Integer key = cs != null ? computeSemKeyFromSemesterCode(cs.getSemesterCode()) : null;
        return key != null ? key : Integer.MAX_VALUE;
      })).toList();
      for (int i = 0; i < sorted.size(); i++) {
        attemptNoByClassId.put(sorted.get(i).getClassId(), i + 1);
      }
    }
    return attemptNoByClassId;
  }

  private Map<UUID, SubjectProgressMeta> buildSubjectProgressMetaByClassId(
      Map<UUID, List<StudentEnrollmentResponse>> enrollmentsBySubjectMap, Map<UUID, ClassSectionResponse> classMap) {
    // Collect semesterId for each classId across ALL enrollments
    Map<UUID, UUID> semesterIdByClassId = new HashMap<>();
    for (var entry : enrollmentsBySubjectMap.entrySet()) {
      for (StudentEnrollmentResponse enrollment : entry.getValue()) {
        ClassSectionResponse classSection = classMap.get(enrollment.getClassId());
        if (classSection != null && classSection.getSemesterId() != null) {
          semesterIdByClassId.put(enrollment.getClassId(), classSection.getSemesterId());
        }
      }
    }

    if (semesterIdByClassId.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<UUID, Semester> semesterById = semesterRepository.findAllById(semesterIdByClassId.values())
        .stream()
        .collect(Collectors.toMap(Semester::getId, semester -> semester));

    List<Semester> orderedSemesters = semesterById.values()
        .stream()
        .sorted(Comparator.comparing(Semester::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Semester::getSemesterCode, Comparator.nullsLast(String::compareTo))
            .thenComparing(Semester::getId))
        .toList();

    Map<UUID, Integer> semesterOrderById = new HashMap<>();
    for (int index = 0; index < orderedSemesters.size(); index++) {
      semesterOrderById.put(orderedSemesters.get(index).getId(), index + 1);
    }

    LinkedHashMap<UUID, Integer> academicYearOrderById = new LinkedHashMap<>();
    for (Semester semester : orderedSemesters) {
      if (semester.getAcademicYear() == null || semester.getAcademicYear().getId() == null) {
        continue;
      }
      academicYearOrderById.computeIfAbsent(
          semester.getAcademicYear().getId(), ignored -> academicYearOrderById.size() + 1);
    }

    // Build result keyed by classId (each enrollment gets its own metadata)
    Map<UUID, SubjectProgressMeta> result = new HashMap<>();
    for (var entry : semesterIdByClassId.entrySet()) {
      Semester semester = semesterById.get(entry.getValue());
      if (semester == null || semester.getAcademicYear() == null || semester.getAcademicYear().getId() == null) {
        continue;
      }
      result.put(
          entry.getKey(), new SubjectProgressMeta(
              semester.getId(), semester.getAcademicYear().getId(), semester.getSemesterCode(),
              extractAcademicYearDisplay(semester.getAcademicYear().getYearCode()),
              semesterOrderById.get(semester.getId()), academicYearOrderById.get(semester.getAcademicYear().getId())));
    }

    return result;
  }

  private String resolveSemesterId(SubjectProgressMeta metadata) {
    return metadata != null && metadata.semesterId() != null ? metadata.semesterId().toString() : null;
  }

  private String resolveAcademicYearId(SubjectProgressMeta metadata) {
    return metadata != null && metadata.academicYearId() != null ? metadata.academicYearId().toString() : null;
  }

  private Integer resolveSemesterOrder(SubjectProgressMeta metadata) {
    return metadata != null ? metadata.semesterOrder() : null;
  }

  private String resolveSemesterCode(SubjectProgressMeta metadata) {
    return metadata != null ? metadata.semesterCode() : null;
  }

  private String resolveAcademicYear(SubjectProgressMeta metadata) {
    return metadata != null ? metadata.academicYear() : null;
  }

  private Integer resolveAcademicYearOrder(SubjectProgressMeta metadata) {
    return metadata != null ? metadata.academicYearOrder() : null;
  }

  private String extractAcademicYearDisplay(String yearCode) {
    if (yearCode == null || yearCode.isBlank()) {
      return null;
    }
    return yearCode.trim();
  }


  private record SubjectProgressMeta(UUID semesterId, UUID academicYearId, String semesterCode, String academicYear,
                                     Integer semesterOrder, Integer academicYearOrder) {
  }

  private UUID resolveFallbackIntakeYearId(String reason, UUID userId) {
    log.warn(
        "Falling back to default intake year code '{}' for userId={}: {}",
        curriculumFallbackConfig.getFallbackIntakeYearCode(), userId, reason);
    return academicYearRepository.findByYearCode(curriculumFallbackConfig.getFallbackIntakeYearCode())
        .map(AcademicYear::getId)
        .orElseThrow(() -> new EntityNotFoundException(
            "Fallback academic year not found with yearCode=" + curriculumFallbackConfig.getFallbackIntakeYearCode()));
  }
}
