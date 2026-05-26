package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.config.CurriculumFallbackConfig;
import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SemesterGpaResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentLearningProgressResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentSubjectDetailResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import com.hcmut.lms.coursemanagement.client.LearningServiceClient;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.*;
import com.hcmut.lms.coursemanagement.domain.entity.department.Department;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.faculty.Faculty;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.domain.entity.subject.*;
import com.hcmut.lms.coursemanagement.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProgressServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LearningServiceClient learningServiceClient;

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private CurriculumSectionRepository curriculumSectionRepository;

    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;

    @Mock
    private SubjectLearningOutcomeRepository subjectLearningOutcomeRepository;

    @Mock
    private ClassSectionService classSectionService;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private CurriculumSubjectPriorityRepository curriculumSubjectPriorityRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private CurriculumFallbackConfig curriculumFallbackConfig;

    @Mock
    private CurriculumSubjectAllocationService allocationService;

    @Mock
    private StudentProgressGradeUtil gradeUtil;

    @InjectMocks
    private StudentProgressServiceImpl studentProgressService;

    private UUID userId;
    private UUID specializationId;
    private UUID intakeYearId;
    private UUID subjectId;
    private UUID classId;
    private UUID curriculumSectionId;
    private UserResponse user;
    private Curriculum curriculum;
    private CurriculumSection curriculumSection;
    private CurriculumSubject curriculumSubject;
    private Subject subject;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        specializationId = UUID.randomUUID();
        intakeYearId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        classId = UUID.randomUUID();
        curriculumSectionId = UUID.randomUUID();

        user = new UserResponse();
        user.setId(userId);
        user.setStudentCode("SV001");
        user.setFirstName("Test");
        user.setLastName("Student");
        user.setSpecializationId(specializationId);
        user.setIntakeYearId(intakeYearId);
        user.setDepartmentId(UUID.randomUUID());

        AcademicYear ay = new AcademicYear();
        ay.setId(intakeYearId);
        ay.setYearCode("2022");
        ay.setStartDate(LocalDate.of(2022, 9, 1));

        Specialization spec = new Specialization();
        spec.setId(specializationId);
        spec.setCode("CS");
        spec.setName("Computer Science");

        Department dept = new Department();
        dept.setId(UUID.randomUUID());
        dept.setName("CSE");

        Faculty faculty = new Faculty();
        faculty.setName("Engineering");

        dept.setFaculty(faculty);
        spec.setDepartment(dept);

        CurriculumId cid = new CurriculumId("CURR2022", specializationId, intakeYearId);
        curriculum = new Curriculum();
        curriculum.setId(cid);
        curriculum.setName("Curriculum 2022");
        curriculum.setTotalCredits(150);
        curriculum.setSpecialization(spec);
        curriculum.setIntakeYear(ay);

        subject = new Subject();
        subject.setId(subjectId);
        subject.setCode("SUB101");
        subject.setName("Subject 101");
        subject.setCredits(3);
        subject.setGradingType(SubjectGradingType.GRADED);

        curriculumSection = new CurriculumSection();
        curriculumSection.setId(curriculumSectionId);
        curriculumSection.setName("Core Section");
        curriculumSection.setRequiredCredits(10);
        curriculumSection.setDisplayOrder(1);

        CurriculumSubjectId csId = new CurriculumSubjectId(curriculumSectionId, subjectId, 1);
        curriculumSubject = new CurriculumSubject();
        curriculumSubject.setId(csId);
        curriculumSubject.setSubject(subject);
        curriculumSubject.setCurriculumSection(curriculumSection);
        curriculumSubject.setIsRequired(true);
        curriculumSubject.setDisplayOrder(1);
        curriculumSubject.setPrerequisites(new ArrayList<>());
        curriculumSubject.setRecommendations(new ArrayList<>());
        curriculumSubject.setParallels(new ArrayList<>());

        curriculumSection.setCurriculumSubjects(List.of(curriculumSubject));
    }

    // -- getStudentLearningProgress --

    @Test
    void getStudentLearningProgress_shouldThrowException_whenStudentNotFound() {
        when(userServiceClient.getUserById(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentLearningProgress(userId, specializationId));
    }

    @Test
    void getStudentLearningProgress_shouldThrowException_whenNotAStudent() {
        UserResponse nonStudent = new UserResponse();
        nonStudent.setId(userId);
        nonStudent.setStudentCode(null);

        when(userServiceClient.getUserById(userId)).thenReturn(nonStudent);

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentLearningProgress(userId, specializationId));
    }

    @Test
    void getStudentLearningProgress_shouldThrowException_whenNoIntakeYear() {
        user.setIntakeYearId(null);
        when(userServiceClient.getUserById(userId)).thenReturn(user);

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentLearningProgress(userId, specializationId));
    }

    @Test
    void getStudentLearningProgress_shouldReturnProgress_whenEmptyEnrollments() {
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(), Map.of(), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertNotNull(result.getProgramInfo());
        assertNotNull(result.getSummary());
        assertEquals("SV001", result.getProgramInfo().getStudentCode());
    }

    @Test
    void getStudentLearningProgress_shouldReturnProgress_whenWithEnrollments() {
        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setId(UUID.randomUUID());
        enrollment.setClassId(classId);
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);
        enrollment.setAttemptNo(1);

        ClassSectionResponse classResponse = ClassSectionResponse.builder()
                .id(classId)
                .subjectId(subjectId)
                .semesterCode("HK221")
                .semesterId(UUID.randomUUID())
                .build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enrollment));
        when(classSectionService.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(curriculumSectionId, 3), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertNotNull(result.getSections());
        assertEquals(1, result.getSections().size());
    }

    @Test
    void getStudentLearningProgress_shouldUseFallbackCurriculum_whenDirectNotFound() {
        UUID fallbackIntakeYearId = UUID.randomUUID();
        AcademicYear fallbackAy = new AcademicYear();
        fallbackAy.setId(fallbackIntakeYearId);
        fallbackAy.setYearCode("2021");
        fallbackAy.setStartDate(LocalDate.of(2021, 9, 1));

        Curriculum fallbackCurriculum = new Curriculum();
        CurriculumId fallbackCid = new CurriculumId("CURR2021", specializationId, fallbackIntakeYearId);
        fallbackCurriculum.setId(fallbackCid);
        fallbackCurriculum.setName("Fallback Curriculum");
        fallbackCurriculum.setSpecialization(curriculum.getSpecialization());
        fallbackCurriculum.setIntakeYear(fallbackAy);

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.empty());
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2021");
        when(academicYearRepository.findByYearCode("2021")).thenReturn(Optional.of(fallbackAy));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, fallbackIntakeYearId))
                .thenReturn(Optional.of(fallbackCurriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, fallbackIntakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(), Map.of(), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertEquals("CURR2021", result.getProgramInfo().getCurriculumCode());
    }

    // -- getStudentSubjectDetail --

    @Test
    void getStudentSubjectDetail_shouldThrowException_whenStudentNotFound() {
        when(userServiceClient.getUserById(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentSubjectDetail(subjectId, userId));
    }

    @Test
    void getStudentSubjectDetail_shouldThrowException_whenNoIntakeYear() {
        user.setIntakeYearId(null);
        when(userServiceClient.getUserById(userId)).thenReturn(user);

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentSubjectDetail(subjectId, userId));
    }

    @Test
    void getStudentSubjectDetail_shouldUseDepartmentFallback_whenNoSpecialization() {
        user.setSpecializationId(null);

        Specialization deptSpec = new Specialization();
        deptSpec.setId(UUID.randomUUID());
        deptSpec.setName("Default CS");

        Subject subjectFull = new Subject();
        subjectFull.setId(subjectId);
        subjectFull.setGradingType(SubjectGradingType.GRADED);

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(specializationRepository.findByDepartmentId(user.getDepartmentId()))
                .thenReturn(List.of(deptSpec));
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(eq(subjectId), eq(deptSpec.getId()), eq(intakeYearId)))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of());
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subjectFull));

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
        assertEquals(subjectId.toString(), result.getSubjectId());
    }

    @Test
    void getStudentSubjectDetail_shouldReturnDetail_whenValid() {
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of());
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
    }

    @Test
    void getStudentSubjectDetail_shouldUseFallbackCurriculum_whenDirectNotFound() {
        UUID fallbackIntakeYearId = UUID.randomUUID();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.empty());
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2021");
        when(academicYearRepository.findByYearCode("2021")).thenReturn(Optional.of(
                AcademicYear.builder().id(fallbackIntakeYearId).yearCode("2021")
                        .startDate(LocalDate.of(2021, 9, 1)).build()));
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, fallbackIntakeYearId))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of());
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
    }

    @Test
    void getStudentSubjectDetail_shouldIncludePrerequisitesAndLearningOutcomes() {
        // Setup prerequisite
        Subject prereqSubject = new Subject();
        prereqSubject.setId(UUID.randomUUID());
        prereqSubject.setCode("PREREQ101");
        prereqSubject.setName("Prerequisite Subject");

        CurriculumSubject prereqCs = new CurriculumSubject();
        prereqCs.setSubject(prereqSubject);

        SubjectPrerequisite prereq = new SubjectPrerequisite();
        prereq.setPrerequisiteCurriculumSubject(prereqCs);
        curriculumSubject.getPrerequisites().add(prereq);

        // Setup learning outcomes
        SubjectLearningOutcome outcome = new SubjectLearningOutcome();
        outcome.setCode("LO1");
        outcome.setDescription("Understand basics");
        outcome.setChildren(new ArrayList<>());

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(outcome));
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
        assertEquals(1, result.getPrerequisites().size());
        assertEquals("PREREQ101", result.getPrerequisites().get(0).getSubjectCode());
        assertEquals(1, result.getLearningOutcomes().size());
    }

    // -- getGpaTrend --

    @Test
    void getGpaTrend_shouldReturnEmptyList_whenEmptySections() {
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(), Map.of(), Set.of()));

        List<SemesterGpaResponse> result = studentProgressService.getGpaTrend(userId, specializationId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getGpaTrend_shouldThrowException_whenStudentNotFound() {
        when(userServiceClient.getUserById(userId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getGpaTrend(userId, specializationId));
    }

    // -- getStudentLearningProgress: isShouldReplace branches --

    @Test
    void getStudentLearningProgress_shouldReplaceEnrollment_whenNewHasHigherGrade() {
        UUID classId1 = UUID.randomUUID();
        UUID classId2 = UUID.randomUUID();

        StudentEnrollmentResponse enroll1 = new StudentEnrollmentResponse();
        enroll1.setId(UUID.randomUUID());
        enroll1.setClassId(classId1);
        enroll1.setFinalGrade(6.0);
        enroll1.setIsPassed(true);

        StudentEnrollmentResponse enroll2 = new StudentEnrollmentResponse();
        enroll2.setId(UUID.randomUUID());
        enroll2.setClassId(classId2);
        enroll2.setFinalGrade(8.0);
        enroll2.setIsPassed(true);

        ClassSectionResponse classResp1 = ClassSectionResponse.builder()
                .id(classId1).subjectId(subjectId).semesterCode("HK221").build();
        ClassSectionResponse classResp2 = ClassSectionResponse.builder()
                .id(classId2).subjectId(subjectId).semesterCode("HK221").build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enroll1, enroll2));
        when(classSectionService.getClassSectionsByIds(any())).thenReturn(List.of(classResp1, classResp2));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(curriculumSectionId, 3), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
    }

    @Test
    void getStudentLearningProgress_shouldReplaceEnrollment_whenOldHasNullGrade() {
        UUID classId1 = UUID.randomUUID();
        UUID classId2 = UUID.randomUUID();

        StudentEnrollmentResponse enroll1 = new StudentEnrollmentResponse();
        enroll1.setId(UUID.randomUUID());
        enroll1.setClassId(classId1);
        enroll1.setFinalGrade(null);
        enroll1.setIsPassed(true);

        StudentEnrollmentResponse enroll2 = new StudentEnrollmentResponse();
        enroll2.setId(UUID.randomUUID());
        enroll2.setClassId(classId2);
        enroll2.setFinalGrade(7.0);
        enroll2.setIsPassed(true);

        ClassSectionResponse classResp1 = ClassSectionResponse.builder()
                .id(classId1).subjectId(subjectId).semesterCode("HK221").build();
        ClassSectionResponse classResp2 = ClassSectionResponse.builder()
                .id(classId2).subjectId(subjectId).semesterCode("HK221").build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enroll1, enroll2));
        when(classSectionService.getClassSectionsByIds(any())).thenReturn(List.of(classResp1, classResp2));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(curriculumSectionId, 3), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
    }

    @Test
    void getStudentLearningProgress_shouldTiebreakBySemester_whenEqualGrades() {
        UUID classId1 = UUID.randomUUID();
        UUID classId2 = UUID.randomUUID();

        StudentEnrollmentResponse enroll1 = new StudentEnrollmentResponse();
        enroll1.setId(UUID.randomUUID());
        enroll1.setClassId(classId1);
        enroll1.setFinalGrade(8.0);
        enroll1.setIsPassed(true);

        StudentEnrollmentResponse enroll2 = new StudentEnrollmentResponse();
        enroll2.setId(UUID.randomUUID());
        enroll2.setClassId(classId2);
        enroll2.setFinalGrade(8.0);
        enroll2.setIsPassed(true);

        ClassSectionResponse classResp1 = ClassSectionResponse.builder()
                .id(classId1).subjectId(subjectId).semesterCode("HK221").build();
        ClassSectionResponse classResp2 = ClassSectionResponse.builder()
                .id(classId2).subjectId(subjectId).semesterCode("HK231").build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enroll1, enroll2));
        when(classSectionService.getClassSectionsByIds(any())).thenReturn(List.of(classResp1, classResp2));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(curriculumSectionId, 3), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
    }

    @Test
    void getStudentLearningProgress_shouldKeepFirstEnrollment_whenBothGradesNull() {
        UUID classId1 = UUID.randomUUID();
        UUID classId2 = UUID.randomUUID();

        StudentEnrollmentResponse enroll1 = new StudentEnrollmentResponse();
        enroll1.setId(UUID.randomUUID());
        enroll1.setClassId(classId1);
        enroll1.setFinalGrade(null);
        enroll1.setIsPassed(true);

        StudentEnrollmentResponse enroll2 = new StudentEnrollmentResponse();
        enroll2.setId(UUID.randomUUID());
        enroll2.setClassId(classId2);
        enroll2.setFinalGrade(null);
        enroll2.setIsPassed(true);

        ClassSectionResponse classResp1 = ClassSectionResponse.builder()
                .id(classId1).subjectId(subjectId).semesterCode("HK221").build();
        ClassSectionResponse classResp2 = ClassSectionResponse.builder()
                .id(classId2).subjectId(subjectId).semesterCode("HK221").build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enroll1, enroll2));
        when(classSectionService.getClassSectionsByIds(any())).thenReturn(List.of(classResp1, classResp2));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(curriculumSectionId, 3), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
    }

    // -- getStudentSubjectDetail with enrollments --

    @Test
    void getStudentSubjectDetail_shouldBuildAttempts_whenHasEnrollments() {
        UUID enrollmentClassId = UUID.randomUUID();

        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setId(UUID.randomUUID());
        enrollment.setClassId(enrollmentClassId);
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        ClassSectionResponse classResp = ClassSectionResponse.builder()
                .id(enrollmentClassId)
                .subjectId(subjectId)
                .semesterCode("HK221")
                .build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of());
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enrollment));
        when(classSectionService.getClassSectionsByIds(any())).thenReturn(List.of(classResp));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
        assertNotNull(result.getAttempts());
        assertFalse(result.getAttempts().isEmpty());
        assertEquals(8.0, result.getAttempts().get(0).getGrade10());
        assertTrue(result.getAttempts().get(0).getIsApplied());
    }

    // -- getStudentSubjectDetail with recommendations, parallels, and priority --

    @Test
    void getStudentSubjectDetail_shouldIncludeRecommendationsParallelsAndPriority() {
        Subject recSubject = new Subject();
        recSubject.setId(UUID.randomUUID());
        recSubject.setCode("REC101");
        recSubject.setName("Recommended Subject");

        CurriculumSubject recCs = new CurriculumSubject();
        recCs.setSubject(recSubject);

        SubjectRecommendation rec = new SubjectRecommendation();
        rec.setRecommendedCurriculumSubject(recCs);
        curriculumSubject.getRecommendations().add(rec);

        Subject parallelSubject = new Subject();
        parallelSubject.setId(UUID.randomUUID());
        parallelSubject.setCode("PAR101");
        parallelSubject.setName("Parallel Subject");

        CurriculumSubject parallelCs = new CurriculumSubject();
        parallelCs.setSubject(parallelSubject);

        SubjectParallel parallel = new SubjectParallel();
        parallel.setParallelCurriculumSubject(parallelCs);
        curriculumSubject.getParallels().add(parallel);

        CurriculumSubjectPriority priority = new CurriculumSubjectPriority();
        priority.setRecommendedYear(3);
        priority.setRecommendedSemesterInYear(1);

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of());
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.of(priority));
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
        assertEquals(1, result.getRecommendations().size());
        assertEquals("REC101", result.getRecommendations().get(0).getSubjectCode());
        assertEquals(1, result.getParallels().size());
        assertEquals("PAR101", result.getParallels().get(0).getSubjectCode());
        assertEquals(3, result.getRecommendedYear());
        assertEquals(1, result.getRecommendedSemester());
    }

    @Test
    void getStudentSubjectDetail_shouldThrowException_whenFallbackCurriculumAlsoNotFound() {
        UUID fallbackIntakeYearId = UUID.randomUUID();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.empty());
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2021");
        when(academicYearRepository.findByYearCode("2021")).thenReturn(Optional.of(
                AcademicYear.builder().id(fallbackIntakeYearId).yearCode("2021")
                        .startDate(LocalDate.of(2021, 9, 1)).build()));
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, fallbackIntakeYearId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentSubjectDetail(subjectId, userId));
    }

    // -- getGpaTrend with graded subjects (covers inner loop + buildSubjectProgressMetaByClassId) --

    @Test
    void getGpaTrend_shouldReturnGpaData_whenHasGradedSubjects() {
        UUID semesterId = UUID.randomUUID();
        UUID classIdLocal = UUID.randomUUID();

        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setId(UUID.randomUUID());
        enrollment.setClassId(classIdLocal);
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        ClassSectionResponse classResp = ClassSectionResponse.builder()
                .id(classIdLocal)
                .subjectId(subjectId)
                .semesterCode("HK221")
                .semesterId(semesterId)
                .build();

        AcademicYear academicYear = AcademicYear.builder()
                .id(intakeYearId)
                .yearCode("2022")
                .startDate(LocalDate.of(2022, 9, 1))
                .build();

        Semester semester = Semester.builder()
                .id(semesterId)
                .semesterCode("HK221")
                .startDate(LocalDate.of(2022, 9, 1))
                .academicYear(academicYear)
                .build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enrollment));
        when(classSectionService.getClassSectionsByIds(any(BatchClassLookupRequest.class)))
                .thenReturn(List.of(classResp));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(semesterRepository.findAllById(anyCollection())).thenReturn(List.of(semester));
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(academicYear));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(curriculumSectionId, 3), Set.of()));
        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        List<SemesterGpaResponse> result = studentProgressService.getGpaTrend(userId, specializationId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("HK221", result.get(0).getSemesterCode());
        assertEquals(8.0, result.get(0).getSemesterGpa10());
        assertEquals(3, result.get(0).getTotalCredits());
        assertEquals(3, result.get(0).getGradedCredits());
    }

    // -- getStudentLearningProgress: free elective excess subjects --

    @Test
    void getStudentLearningProgress_shouldIncludeFreeElectiveExcessSubjects() {
        UUID section1Id = UUID.randomUUID();
        UUID section2Id = UUID.randomUUID();

        // Regular section with a subject
        CurriculumSection coreSection = new CurriculumSection();
        coreSection.setId(section1Id);
        coreSection.setName("Core Section");
        coreSection.setRequiredCredits(3);
        coreSection.setDisplayOrder(1);
        coreSection.setCurriculumSubjects(List.of(curriculumSubject));

        // Free elective section
        CurriculumSection freeElectiveSection = new CurriculumSection();
        freeElectiveSection.setId(section2Id);
        freeElectiveSection.setName("Tự chọn tự do");
        freeElectiveSection.setRequiredCredits(6);
        freeElectiveSection.setDisplayOrder(2);
        freeElectiveSection.setCurriculumSubjects(List.of());

        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setId(UUID.randomUUID());
        enrollment.setClassId(classId);
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        ClassSectionResponse classResp = ClassSectionResponse.builder()
                .id(classId)
                .subjectId(subjectId)
                .semesterCode("HK221")
                .semesterId(UUID.randomUUID())
                .build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enrollment));
        when(classSectionService.getClassSectionsByIds(any(BatchClassLookupRequest.class)))
                .thenReturn(List.of(classResp));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(coreSection, freeElectiveSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(), Map.of(), Set.of(subjectId)));
        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertEquals(2, result.getSections().size());
    }

    // -- getStudentSubjectDetail: sort comparator with multiple enrollments --

    @Test
    void getStudentSubjectDetail_shouldSortAttemptsBySemester_whenMultipleEnrollments() {
        UUID classId1 = UUID.randomUUID();
        UUID classId2 = UUID.randomUUID();

        StudentEnrollmentResponse enroll1 = new StudentEnrollmentResponse();
        enroll1.setId(UUID.randomUUID());
        enroll1.setClassId(classId1);
        enroll1.setFinalGrade(7.0);
        enroll1.setIsPassed(true);

        StudentEnrollmentResponse enroll2 = new StudentEnrollmentResponse();
        enroll2.setId(UUID.randomUUID());
        enroll2.setClassId(classId2);
        enroll2.setFinalGrade(8.0);
        enroll2.setIsPassed(true);

        ClassSectionResponse classResp1 = ClassSectionResponse.builder()
                .id(classId1).subjectId(subjectId).semesterCode("HK221").build();
        ClassSectionResponse classResp2 = ClassSectionResponse.builder()
                .id(classId2).subjectId(subjectId).semesterCode("HK231").build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of());
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enroll1, enroll2));
        when(classSectionService.getClassSectionsByIds(any(BatchClassLookupRequest.class)))
                .thenReturn(List.of(classResp1, classResp2));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
        assertEquals(2, result.getAttempts().size());
        // HK221 (semKey=20221) before HK231 (semKey=20231)
        assertEquals(1, result.getAttempts().get(0).getAttemptNo());
        assertEquals(2, result.getAttempts().get(1).getAttemptNo());
    }

    // -- buildLearningOutcomeItem recursive children --

    @Test
    void getStudentSubjectDetail_shouldBuildLearningOutcomeTree_whenNested() {
        SubjectLearningOutcome childOutcome = new SubjectLearningOutcome();
        childOutcome.setCode("LO1.1");
        childOutcome.setDescription("Child outcome");

        SubjectLearningOutcome parentOutcome = new SubjectLearningOutcome();
        parentOutcome.setCode("LO1");
        parentOutcome.setDescription("Parent outcome");
        parentOutcome.setChildren(List.of(childOutcome));

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithPrerequisites(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithRecommendations(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(curriculumSubjectRepository.findByIdWithParallels(any(), any(), any()))
                .thenReturn(Optional.of(curriculumSubject));
        when(subjectLearningOutcomeRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(parentOutcome));
        when(curriculumSubjectPriorityRepository.findByCompositeId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        StudentSubjectDetailResponse result = studentProgressService.getStudentSubjectDetail(subjectId, userId);

        assertNotNull(result);
        assertEquals(1, result.getLearningOutcomes().size());
        assertEquals("LO1", result.getLearningOutcomes().get(0).getCode());
        assertEquals(1, result.getLearningOutcomes().get(0).getChildren().size());
        assertEquals("LO1.1", result.getLearningOutcomes().get(0).getChildren().get(0).getCode());
    }

    // -- calculateSummary: earned credits path --

    @Test
    void getStudentLearningProgress_shouldCalculateEarnedCredits_whenSubjectPassed() {
        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setId(UUID.randomUUID());
        enrollment.setClassId(classId);
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        ClassSectionResponse classResp = ClassSectionResponse.builder()
                .id(classId).subjectId(subjectId).semesterCode("HK221").build();

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of(enrollment));
        when(classSectionService.getClassSectionsByIds(any(BatchClassLookupRequest.class)))
                .thenReturn(List.of(classResp));
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(curriculumSectionId, 3), Set.of()));
        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertEquals(3, result.getSummary().getEarnedCredits());
        assertTrue(result.getSummary().getCumulativeGpa10() > 0);
    }

    // -- resolveFallbackIntakeYearId: academicYear not found for fallback yearCode --

    @Test
    void getStudentLearningProgress_shouldThrowException_whenFallbackYearNotFound() {
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.empty());
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2020");
        when(academicYearRepository.findByYearCode("2020")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentLearningProgress(userId, specializationId));
    }

    // -- getStudentSubjectDetail: fallback academic year not found --

    @Test
    void getStudentSubjectDetail_shouldThrowException_whenFallbackYearNotFound() {
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(curriculumSubjectRepository.findBySubjectIdAndCurriculum(subjectId, specializationId, intakeYearId))
                .thenReturn(Optional.empty());
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2020");
        when(academicYearRepository.findByYearCode("2020")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> studentProgressService.getStudentSubjectDetail(subjectId, userId));
    }

    // -- buildProgramInfo: NumberFormatException for curriculum yearCode --

    @Test
    void getStudentLearningProgress_shouldHandleNonNumericYearCode() {
        AcademicYear ayWithBadCode = new AcademicYear();
        ayWithBadCode.setId(UUID.randomUUID());
        ayWithBadCode.setYearCode("N/A");
        ayWithBadCode.setStartDate(LocalDate.of(2022, 9, 1));

        curriculum.setIntakeYear(ayWithBadCode);

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(ayWithBadCode));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(), Map.of(), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertNull(result.getProgramInfo().getCurriculumYear());
    }

    // -- buildProgramInfo: student intake year resolution with 4-digit yearCode --

    @Test
    void getStudentLearningProgress_shouldParseStudentIntakeYear_when4DigitYearCode() {
        AcademicYear studentAy = new AcademicYear();
        studentAy.setId(intakeYearId);
        studentAy.setYearCode("2024");
        studentAy.setStartDate(LocalDate.of(2024, 9, 1));

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(studentAy));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(), Map.of(), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertEquals(2024, result.getProgramInfo().getStudentIntakeYear());
    }

    // -- calculateSummary: null required credits and null enrollment --

    @Test
    void getStudentLearningProgress_shouldHandleNullRequiredCreditsAndNullEnrollment() {
        // Section with null required credits
        curriculumSection.setRequiredCredits(null);

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(subjectId), Map.of(), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertEquals(0, result.getSummary().getEarnedCredits());
        assertEquals(0, result.getSummary().getRequiredCredits());
    }

    // -- buildProgramInfo: student intake year parseInt with 2-digit code --

    @Test
    void getStudentLearningProgress_shouldConvert2DigitYearCodeTo4Digit() {
        AcademicYear studentAy = new AcademicYear();
        studentAy.setId(intakeYearId);
        studentAy.setYearCode("22");
        studentAy.setStartDate(LocalDate.of(2022, 9, 1));

        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(learningServiceClient.getStudentEnrollments(userId)).thenReturn(List.of());
        when(curriculumRepository.findBySpecializationIdAndIntakeYearIdWithDetails(specializationId, intakeYearId))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSectionRepository.findBySpecializationAndIntakeYearWithSubjects(specializationId, intakeYearId))
                .thenReturn(List.of(curriculumSection));
        when(subjectRepository.findAllById(anySet())).thenReturn(List.of(subject));
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(studentAy));
        when(allocationService.allocateSubjectsToSections(anyList(), anyMap(), anyMap()))
                .thenReturn(new CurriculumSubjectAllocationService.SectionAllocationResult(
                        Set.of(), Map.of(), Set.of()));

        StudentLearningProgressResponse result =
                studentProgressService.getStudentLearningProgress(userId, specializationId);

        assertNotNull(result);
        assertEquals(2022, result.getProgramInfo().getStudentIntakeYear());
    }
}
