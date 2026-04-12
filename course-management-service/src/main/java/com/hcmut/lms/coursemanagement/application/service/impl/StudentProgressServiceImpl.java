package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentLearningProgressResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentSubjectDetailResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import com.hcmut.lms.coursemanagement.application.service.StudentProgressService;
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

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public StudentLearningProgressResponse getStudentLearningProgress(UUID userId) {
    log.info("Fetching student learning progress for userId: {}", userId);

    UserResponse user = userServiceClient.getUserById(userId);
    if (user == null || user.getStudentCode() == null) {
      throw new EntityNotFoundException("Student not found with userId: " + userId);
    }

    if (user.getIntakeYearId() == null || user.getSpecializationId() == null) {
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

    // Map enrollments by subjectId - keep only the best result (highest grade, or most recent if no grade)
    Map<UUID, StudentEnrollmentResponse> enrollmentBySubjectMap = new HashMap<>();
    for (StudentEnrollmentResponse enrollment : enrollments) {
      ClassSectionResponse classSection = classMap.get(enrollment.getClassId());
      if (classSection != null && classSection.getSubjectId() != null) {
        UUID subjectId = classSection.getSubjectId();
        StudentEnrollmentResponse existing = enrollmentBySubjectMap.get(subjectId);

        if (existing == null) {
          enrollmentBySubjectMap.put(subjectId, enrollment);
        } else {
          // Keep the better enrollment: higher grade, or more recent if grades are equal/null
          boolean shouldReplace = isShouldReplace(enrollment, existing);

          if (shouldReplace) {
            enrollmentBySubjectMap.put(subjectId, enrollment);
          }
        }
      }
    }

    Map<UUID, SubjectProgressMeta> subjectProgressMetaBySubjectId = buildSubjectProgressMetaBySubjectId(
        enrollmentBySubjectMap,
        classMap);

    Curriculum curriculum = curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(
            user.getSpecializationId(), user.getIntakeYearId())
        .orElseThrow(() -> new EntityNotFoundException("Curriculum not found for student"));

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
    StudentLearningProgressResponse.StudentLearningSummary summary = calculateSummary(
        sections, enrollmentBySubjectMap, subjectGradingTypeMap);
    List<StudentLearningProgressResponse.StudentProgressSectionItem> sectionItems = buildSectionItems(
        sections,
        enrollmentBySubjectMap,
        subjectGradingTypeMap,
        subjectProgressMetaBySubjectId);

    log.info("Successfully fetched student learning progress for userId: {}", userId);

    return StudentLearningProgressResponse.builder()
        .programInfo(programInfo)
        .summary(summary)
        .sections(sectionItems)
        .updatedAt(LocalDateTime.now().format(DATE_FORMATTER))
        .build();
  }

  private static boolean isShouldReplace(StudentEnrollmentResponse enrollment, StudentEnrollmentResponse existing) {
    boolean shouldReplace = false;

    if (existing.getFinalGrade() == null && enrollment.getFinalGrade() != null) {
      shouldReplace = true;
    } else if (existing.getFinalGrade() != null && enrollment.getFinalGrade() != null) {
      if (enrollment.getFinalGrade() > existing.getFinalGrade()) {
        shouldReplace = true;
      } else if (enrollment.getFinalGrade().equals(existing.getFinalGrade())) {
        // If grades are equal, prefer more recent (higher attemptNo or later completion)
        if (enrollment.getAttemptNo() != null && existing.getAttemptNo() != null) {
          shouldReplace = enrollment.getAttemptNo() > existing.getAttemptNo();
        }
      }
    }
    return shouldReplace;
  }

  @Override
  public StudentSubjectDetailResponse getStudentSubjectDetail(UUID subjectId, UUID userId) {
    log.info("Fetching subject detail for subjectId: {}", subjectId);
    UserResponse user = userServiceClient.getUserById(userId);

    if (user == null || user.getStudentCode() == null) {
      throw new EntityNotFoundException("Student not found with userId: " + userId);
    }

    if (user.getIntakeYearId() == null) {
      throw new EntityNotFoundException("Student intake year or specialization not found for userId: " + userId);
    }

    if (user.getSpecializationId() == null) {
      Specialization curSpecialization = specializationRepository.findByDepartmentId(user.getDepartmentId()).getFirst();
      user.setSpecializationId(curSpecialization.getId());
    }

    // Fetch curriculum subject filtered by student's curriculum
    CurriculumSubject curriculumSubject = curriculumSubjectRepository.findBySubjectIdAndCurriculum(
            subjectId,
            user.getSpecializationId(), user.getIntakeYearId())
        .orElseThrow(() -> new EntityNotFoundException(
            "Subject not found with id: " + subjectId + " in curriculum " + "for specialization: " + user.getSpecializationId() + " and intake year: " + user.getIntakeYearId()));

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
    List<StudentSubjectDetailResponse.StudentSubjectAttemptItem> attempts = enrollments.stream().filter(enrollment -> {
      ClassSectionResponse classSection = classMap.get(enrollment.getClassId());
      return classSection != null && subjectId.equals(classSection.getSubjectId());
    }).sorted((e1, e2) -> {
      // Sort by attemptNo ascending
      if (e1.getAttemptNo() != null && e2.getAttemptNo() != null) {
        return e1.getAttemptNo().compareTo(e2.getAttemptNo());
      }
      return 0;
    }).map(enrollment -> {
      ClassSectionResponse classSection = classMap.get(enrollment.getClassId());
      Double grade10 = enrollment.getFinalGrade();
      String letterGrade = grade10 != null ? convertToLetterGrade(grade10) : null;
      Double grade4 = grade10 != null ? convertTo4Scale(grade10) : null;
      Boolean isPassed = isStudentPassedSubject(enrollment, gradingType);

      return StudentSubjectDetailResponse.StudentSubjectAttemptItem.builder()
          .attemptNo(enrollment.getAttemptNo())
          .semesterLabel(classSection != null ? classSection.getSemesterCode() : null)
          .grade10(grade10)
          .letterGrade(letterGrade)
          .grade4(grade4)
          .isPassed(isPassed)
          .isApplied(null) // Will be determined by business logic
          .build();
    }).collect(Collectors.toList());

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
    Integer year = null;
    if (curriculum.getIntakeYear() != null && curriculum.getIntakeYear().getYearCode() != null) {
      try {
        int parsedYear = Integer.parseInt(curriculum.getIntakeYear().getYearCode().trim());
        year = parsedYear < 100 ? parsedYear + 2000 : parsedYear;
      } catch (NumberFormatException e) {
        log.warn("Failed to parse year from yearCode: {}", curriculum.getIntakeYear().getYearCode());
      }
    }

    return StudentLearningProgressResponse.StudentProgramInfo.builder()
        .facultyName(curriculum.getSpecialization().getDepartment().getFaculty().getName())
        .specializationName(curriculum.getSpecialization().getName())
        .specializationCode(curriculum.getSpecialization().getCode())
        .curriculumCode(curriculum.getId().getCode())
        .curriculumYear(year)
        .studentName(user.getFullName())
        .studentCode(user.getStudentCode())
        .build();
  }

  private StudentLearningProgressResponse.StudentLearningSummary calculateSummary(
      List<CurriculumSection> sections,
      Map<UUID, StudentEnrollmentResponse> enrollmentMap, Map<UUID, SubjectGradingType> subjectGradingTypeMap) {

    int earnedCredits = 0;
    int requiredCredits = 0;
    double totalWeightedGrade10 = 0.0;
    double totalWeightedGrade4 = 0.0;
    int totalCredits = 0;

    for (CurriculumSection section : sections) {
      if (section.getRequiredCredits() != null) {
        requiredCredits += section.getRequiredCredits();
      }

      for (CurriculumSubject cs : section.getCurriculumSubjects()) {
        StudentEnrollmentResponse enrollment = enrollmentMap.get(cs.getSubject().getId());
        if (enrollment != null) {
          UUID subjectId = cs.getSubject().getId();
          SubjectGradingType gradingType = subjectGradingTypeMap.getOrDefault(subjectId, SubjectGradingType.GRADED);
          int credits = cs.getSubject().getCredits();

          Boolean isPassed = isStudentPassedSubject(enrollment, gradingType);

          if (isPassed != null && isPassed) {
            earnedCredits += credits;
          }

          // Only include in GPA calculation if there's a grade
          if (enrollment.getFinalGrade() != null) {
            double grade10 = enrollment.getFinalGrade();
            totalWeightedGrade10 += grade10 * credits;
            totalWeightedGrade4 += convertTo4Scale(grade10) * credits;
            totalCredits += credits;
          }
        }
      }
    }

    // GPA = sum(grade * credits) / sum(credits)
    double cumulativeGpa10 = totalCredits > 0 ? Math.round(totalWeightedGrade10 / totalCredits * 100.0) / 100.0 : 0.0;
    double cumulativeGpa4 = totalCredits > 0 ? Math.round(totalWeightedGrade4 / totalCredits * 100.0) / 100.0 : 0.0;

    return StudentLearningProgressResponse.StudentLearningSummary.builder()
        .earnedCredits(earnedCredits)
        .requiredCredits(requiredCredits)
        .cumulativeGpa10(cumulativeGpa10)
        .cumulativeGpa4(cumulativeGpa4)
        .build();
  }

  private List<StudentLearningProgressResponse.StudentProgressSectionItem> buildSectionItems(
      List<CurriculumSection> sections, Map<UUID, StudentEnrollmentResponse> enrollmentMap,
      Map<UUID, SubjectGradingType> subjectGradingTypeMap,
      Map<UUID, SubjectProgressMeta> subjectProgressMetaBySubjectId) {

    return sections.stream().map(section -> {
      int completedCredits = 0;
      int totalSectionCredits = 0;

      List<StudentLearningProgressResponse.StudentProgressSubjectItem> subjectItems = new ArrayList<>();

      for (CurriculumSubject cs : section.getCurriculumSubjects()) {
        totalSectionCredits += cs.getSubject().getCredits();
        StudentEnrollmentResponse enrollment = enrollmentMap.get(cs.getSubject().getId());

        Double grade10 = null;
        String letterGrade = null;
        Double grade4 = null;
        Boolean isPassed = null;

        if (enrollment != null) {
          UUID subjectId = cs.getSubject().getId();
          SubjectGradingType gradingType = subjectGradingTypeMap.getOrDefault(subjectId, SubjectGradingType.GRADED);

          isPassed = isStudentPassedSubject(enrollment, gradingType);

          if (enrollment.getFinalGrade() != null) {
            grade10 = enrollment.getFinalGrade();
            letterGrade = convertToLetterGrade(grade10);
            grade4 = convertTo4Scale(grade10);
          }
        }

        if (isPassed != null && isPassed) {
          completedCredits += cs.getSubject().getCredits();
        }

        subjectItems.add(StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId(cs.getSubject().getId().toString())
            .order(cs.getDisplayOrder())
            .semesterId(resolveSemesterId(subjectProgressMetaBySubjectId.get(cs.getSubject().getId())))
            .academicYearId(resolveAcademicYearId(subjectProgressMetaBySubjectId.get(cs.getSubject().getId())))
            .semesterCode(resolveSemesterCode(subjectProgressMetaBySubjectId.get(cs.getSubject().getId())))
            .academicYear(resolveAcademicYear(subjectProgressMetaBySubjectId.get(cs.getSubject().getId())))
            .semesterOrder(resolveSemesterOrder(subjectProgressMetaBySubjectId.get(cs.getSubject().getId())))
            .academicYearOrder(resolveAcademicYearOrder(subjectProgressMetaBySubjectId.get(cs.getSubject().getId())))
            .subjectCode(cs.getSubject().getCode())
            .subjectName(cs.getSubject().getName())
            .credits(cs.getSubject().getCredits())
            .grade10(grade10)
            .letterGrade(letterGrade)
            .grade4(grade4)
            .isPassed(isPassed)
            .build());
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

  private String convertToLetterGrade(double grade) {
    if (grade >= 9.5) return "A+";
    if (grade >= 8.5) return "A";
    if (grade >= 8.0) return "B+";
    if (grade >= 7.0) return "B";
    if (grade >= 6.5) return "C+";
    if (grade >= 5.5) return "C";
    if (grade >= 5.0) return "D+";
    if (grade >= 4.0) return "D";
    return "F";
  }

  private double convertTo4Scale(double grade) {
    if (grade >= 8.5) return 4.0;  // A+/A: 9.5-10.0 and 8.5-9.4
    if (grade >= 8.0) return 3.5;  // B+: 8.0-8.4
    if (grade >= 7.0) return 3.0;  // B: 7.0-7.9
    if (grade >= 6.5) return 2.5;  // C+: 6.5-6.9
    if (grade >= 5.5) return 2.0;  // C: 5.5-6.4
    if (grade >= 5.0) return 1.5;  // D+: 5.0-5.4
    if (grade >= 4.0) return 1.0;  // D: 4.0-4.9
    return 0.0;                     // F: < 4.0
  }

  /**
   * Determines if a student passed a subject based on the grading type.
   *
   * @param enrollment  The student enrollment data
   * @param gradingType The subject's grading type
   * @return true if the student passed, false if failed, null if not yet determined
   */
  private Boolean isStudentPassedSubject(StudentEnrollmentResponse enrollment, SubjectGradingType gradingType) {
    if (enrollment == null) {
      return null;
    }

    switch (gradingType) {
      case GRADED:
        // For graded subjects, use grade >= 4.0
        return enrollment.getFinalGrade() != null ? enrollment.getFinalGrade() >= 4.0 : null;

      case PASS_FAIL:
        // For pass/fail subjects, use isPassed from enrollment
        return enrollment.getIsPassed();

      case BOTH:
        // For both types: isPassed is true OR grade >= 5.0
        Boolean isPassed = enrollment.getIsPassed();
        Double grade = enrollment.getFinalGrade();

        if (isPassed != null && isPassed) {
          return true;
        }
        if (grade != null && grade >= 5.0) {
          return true;
        }
        // If we have either value and neither condition passed, return false
        if (isPassed != null || grade != null) {
          return false;
        }
        return null;

      default:
        // Default to graded logic
        return enrollment.getFinalGrade() != null ? enrollment.getFinalGrade() >= 4.0 : null;
    }
  }

  private Map<UUID, SubjectProgressMeta> buildSubjectProgressMetaBySubjectId(
      Map<UUID, StudentEnrollmentResponse> enrollmentBySubjectMap,
      Map<UUID, ClassSectionResponse> classMap) {
    Map<UUID, UUID> semesterIdBySubjectId = new HashMap<>();
    for (Map.Entry<UUID, StudentEnrollmentResponse> entry : enrollmentBySubjectMap.entrySet()) {
      ClassSectionResponse classSection = classMap.get(entry.getValue().getClassId());
      if (classSection != null && classSection.getSemesterId() != null) {
        semesterIdBySubjectId.put(entry.getKey(), classSection.getSemesterId());
      }
    }

    if (semesterIdBySubjectId.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<UUID, Semester> semesterById = semesterRepository.findAllById(semesterIdBySubjectId.values())
        .stream()
        .collect(Collectors.toMap(Semester::getId, semester -> semester));

    List<Semester> orderedSemesters = semesterById.values().stream()
        .sorted(Comparator
            .comparing(Semester::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
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
      academicYearOrderById.computeIfAbsent(semester.getAcademicYear().getId(), ignored -> academicYearOrderById.size() + 1);
    }

    Map<UUID, SubjectProgressMeta> result = new HashMap<>();
    for (Map.Entry<UUID, UUID> entry : semesterIdBySubjectId.entrySet()) {
      Semester semester = semesterById.get(entry.getValue());
      if (semester == null || semester.getAcademicYear() == null || semester.getAcademicYear().getId() == null) {
        continue;
      }
      result.put(entry.getKey(), new SubjectProgressMeta(
          semester.getId(),
          semester.getAcademicYear().getId(),
          semester.getSemesterCode(),
          extractAcademicYearDisplay(semester.getAcademicYear().getYearCode()),
          semesterOrderById.get(semester.getId()),
          academicYearOrderById.get(semester.getAcademicYear().getId())));
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

  private record SubjectProgressMeta(
      UUID semesterId,
      UUID academicYearId,
      String semesterCode,
      String academicYear,
      Integer semesterOrder,
      Integer academicYearOrder
  ) {
  }
}
