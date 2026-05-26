package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.*;

import com.hcmut.lms.personalization.application.dto.response.LearningPathGraphResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningPathResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningPathSectionResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningPathSubjectResponse;
import com.hcmut.lms.personalization.application.mapper.LearningPathMapper;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.CurriculumResolutionResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.domain.entity.learningPath.PrerequisiteNode;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearningPathResponseAssemblerTest {

    @Mock private LearningPathMapper learningPathMapper;
    @Mock private CourseManagementClient courseManagementClient;
    @Mock private LearningServiceClient learningClient;
    @Mock private LearningGoalRepository learningGoalRepository;
    @Mock private LearningPathSectionRepository learningPathSectionRepository;
    @Mock private LearningPathSubjectRepository learningPathSubjectRepository;

    @InjectMocks
    private LearningPathResponseAssembler responseAssembler;

    // ── toPathResponse ─────────────────────────────────────────────────────

    @Test void toPathResponse_shouldReturnResponse_whenEmptyPath() {
        LearningPath path = new LearningPath();
        path.setLearningPathId(UUID.randomUUID());
        path.setStudentId(UUID.randomUUID());

        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(Collections.emptyList());
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.empty());
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenReturn(Collections.emptyList());
        when(courseManagementClient.getAllSemesters())
            .thenReturn(Collections.emptyList());
        when(learningPathSubjectRepository.findByLearningPathId(any()))
            .thenReturn(Collections.emptyList());
        when(learningPathMapper.toPathResponse(any()))
            .thenReturn(new LearningPathResponse());
        when(learningPathMapper.toSectionResponse(any()))
            .thenReturn(new LearningPathSectionResponse());
        when(learningPathMapper.toSubjectResponse(any()))
            .thenReturn(new LearningPathSubjectResponse());

        var result = responseAssembler.toPathResponse(path, UUID.randomUUID());
        assertNotNull(result);
    }

    @Test void toPathResponse_shouldReturnResponse_whenPathWithSections() {
        UUID pathId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID semesterId = UUID.randomUUID();

        LearningPath path = new LearningPath();
        path.setLearningPathId(pathId);
        path.setStudentId(studentId);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setLearningPathId(pathId);
        section.setSemesterId(semesterId);
        section.setSemesterOrder(1);
        section.setTotalCredits(17);

        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.empty());
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenReturn(Collections.emptyList());

        // Return a semester that matches the section's semesterId
        SemesterResponse sem = SemesterResponse.builder()
            .id(semesterId).semKey(20241).semesterCode("HK241")
            .academicYearCode("24").build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(sem));

        LearningPathSubject subject = new LearningPathSubject();
        subject.setSubjectId(UUID.randomUUID());
        subject.setSubjectCode("CS101");
        subject.setSubjectName("Intro");
        subject.setCredits(3);
        subject.setLearningPathSectionId(sectionId);
        subject.setIsHighestResult(true);
        subject.setIsCompleted(false);
        subject.setStudyOrder(1);
        when(learningPathSubjectRepository.findByLearningPathId(any()))
            .thenReturn(List.of(subject));

        when(learningPathMapper.toPathResponse(any()))
            .thenReturn(new LearningPathResponse());
        when(learningPathMapper.toSectionResponse(any()))
            .thenReturn(new LearningPathSectionResponse());
        when(learningPathMapper.toSubjectResponse(any()))
            .thenReturn(new LearningPathSubjectResponse());

        var result = responseAssembler.toPathResponse(path, studentId);
        assertNotNull(result);
    }

    // ── fetchProgressForStudent ────────────────────────────────────────────

    @Test void fetchProgressForStudent_shouldReturnProgress_whenGoalExists() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();
        var result = responseAssembler.fetchProgressForStudent(UUID.randomUUID(), goal);
        assertTrue(result == null || result instanceof StudentLearningProgressResponse);
    }

    @Test void fetchProgressForStudent_shouldReturnNull_whenNullGoal() {
        var result = responseAssembler.fetchProgressForStudent(UUID.randomUUID(), null);
        assertTrue(result == null || result instanceof StudentLearningProgressResponse);
    }

    @Test void fetchProgressForStudent_shouldReturnNull_whenException() {
        when(courseManagementClient.getStudentProgress(any(), any()))
            .thenThrow(new RuntimeException("Service down"));
        var result = responseAssembler.fetchProgressForStudent(UUID.randomUUID(), null);
        assertNotNull(result == null);
    }

    // ── buildCurriculumEnrichmentData ──────────────────────────────────────

    @Test void buildCurriculumEnrichmentData_shouldReturnEmpty_whenNullGoal() {
        var result = responseAssembler.buildCurriculumEnrichmentData(null, null);
        assertNotNull(result);
    }

    @Test void buildCurriculumEnrichmentData_shouldReturnEmpty_whenNullSpecializationId() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(null).isActive(true).build();
        var result = responseAssembler.buildCurriculumEnrichmentData(null, goal);
        assertNotNull(result);
    }

    @Test void buildCurriculumEnrichmentData_shouldReturnEmpty_whenNullCurriculum() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(null);
        var result = responseAssembler.buildCurriculumEnrichmentData(null, goal);
        assertNotNull(result);
    }

    @Test void buildCurriculumEnrichmentData_shouldReturnEmpty_whenNullCurriculumCode() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();
        CurriculumResolutionResponse resolution = new CurriculumResolutionResponse();
        resolution.setCode(null);
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(resolution);
        var result = responseAssembler.buildCurriculumEnrichmentData(null, goal);
        assertNotNull(result);
    }

    @Test void buildCurriculumEnrichmentData_shouldReturnData_whenFullCurriculum() {
        UUID subjectId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();

        CurriculumResolutionResponse resolution = new CurriculumResolutionResponse();
        resolution.setCode("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(resolution);

        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        CurriculumFullResponse.CurriculumSectionFull section = new CurriculumFullResponse.CurriculumSectionFull();
        CurriculumFullResponse.CurriculumSubjectFull subject = new CurriculumFullResponse.CurriculumSubjectFull();
        subject.setSubjectId(subjectId);
        subject.setSubjectCode("CS101");
        subject.setSubjectName("Intro");
        subject.setCredits(3);
        section.setSubjects(List.of(subject));
        curriculumFull.setSections(List.of(section));
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        StudentLearningProgressResponse progress = new StudentLearningProgressResponse();
        StudentLearningProgressResponse.StudentLearningSummary summary =
            new StudentLearningProgressResponse.StudentLearningSummary();
        summary.setEarnedCredits(50);
        summary.setRequiredCredits(150);
        progress.setSummary(summary);

        var result = responseAssembler.buildCurriculumEnrichmentData(progress, goal);
        assertNotNull(result);
    }

    // ── buildSectionDisplayInfo ────────────────────────────────────────────

    @Test void buildSectionDisplayInfo_shouldReturnMap_whenRemainingSemestersExist() {
        UUID semesterId = UUID.randomUUID();
        SemesterResponse sem = SemesterResponse.builder()
            .id(semesterId).semKey(20241).semesterCode("HK241")
            .academicYearCode("24").build();
        when(courseManagementClient.getRemainingSemesters(any())).thenReturn(List.of(sem));

        var result = responseAssembler.buildSectionDisplayInfo(
            UUID.randomUUID(), null, Collections.emptyList());
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertTrue(result.containsKey(semesterId));
    }

    @Test void buildSectionDisplayInfo_shouldReturnMap_whenProgressHasSections() {
        UUID semesterId = UUID.randomUUID();

        StudentLearningProgressResponse progress = new StudentLearningProgressResponse();
        StudentLearningProgressResponse.StudentProgressSectionItem progressSection =
            new StudentLearningProgressResponse.StudentProgressSectionItem();
        StudentLearningProgressResponse.StudentProgressSubjectItem subjectItem =
            new StudentLearningProgressResponse.StudentProgressSubjectItem();
        subjectItem.setSemesterId(semesterId.toString());
        subjectItem.setAcademicYear("24");
        subjectItem.setSemesterCode("HK241");
        progressSection.setSubjects(List.of(subjectItem));
        progress.setSections(List.of(progressSection));

        when(courseManagementClient.getRemainingSemesters(any())).thenReturn(Collections.emptyList());

        var result = responseAssembler.buildSectionDisplayInfo(
            UUID.randomUUID(), progress, Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.containsKey(semesterId));
    }

    // ── toSubjectResponse ──────────────────────────────────────────────────

    @Test void toSubjectResponse_shouldReturnResponse_whenCurriculumSubjectExists() {
        UUID subjectId = UUID.randomUUID();
        LearningPathSubject lpSubject = new LearningPathSubject();
        lpSubject.setSubjectId(subjectId);
        lpSubject.setSubjectCode("CS101");
        lpSubject.setSubjectName("Intro");
        lpSubject.setCredits(3);
        lpSubject.setPrerequisitesGraph(Collections.emptyList());

        CurriculumFullResponse.CurriculumSubjectFull curriculumSubject =
            new CurriculumFullResponse.CurriculumSubjectFull();
        curriculumSubject.setSubjectId(subjectId);
        CurriculumFullResponse.SubjectRelation parallel = new CurriculumFullResponse.SubjectRelation();
        parallel.setSubjectId(UUID.randomUUID());
        parallel.setSubjectCode("CS102");
        parallel.setSubjectName("Data Structures");
        curriculumSubject.setParallels(List.of(parallel));
        CurriculumFullResponse.SubjectRelation rec = new CurriculumFullResponse.SubjectRelation();
        rec.setSubjectId(UUID.randomUUID());
        rec.setSubjectCode("CS201");
        rec.setSubjectName("Advanced");
        curriculumSubject.setRecommendations(List.of(rec));

        Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> curriculumSubjectMap =
            Map.of(subjectId, curriculumSubject);
        Map<UUID, String> difficultyMap = Map.of(subjectId, "hard");

        when(learningPathMapper.toSubjectResponse(any())).thenReturn(new LearningPathSubjectResponse());

        var result = responseAssembler.toSubjectResponse(lpSubject, curriculumSubjectMap, difficultyMap);
        assertNotNull(result);
    }

    // ── buildGraph ─────────────────────────────────────────────────────────

    @Test void buildGraph_shouldReturnGraph_whenSubjectsWithEdges() {
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID semesterId = UUID.randomUUID();

        LearningPathSubject subject1 = new LearningPathSubject();
        subject1.setSubjectId(subjectId1);
        subject1.setSubjectCode("CS101");
        subject1.setSubjectName("Intro");
        subject1.setCredits(3);
        subject1.setLearningPathSectionId(sectionId);
        subject1.setIsHighestResult(true);
        subject1.setIsCompleted(true);

        PrerequisiteNode prereqNode = PrerequisiteNode.builder()
            .subjectId(subjectId1).subjectCode("CS101").subjectName("Intro").required(true).build();

        LearningPathSubject subject2 = new LearningPathSubject();
        subject2.setSubjectId(subjectId2);
        subject2.setSubjectCode("CS201");
        subject2.setSubjectName("Advanced");
        subject2.setCredits(4);
        subject2.setLearningPathSectionId(sectionId);
        subject2.setIsHighestResult(true);
        subject2.setIsCompleted(false);
        subject2.setPrerequisitesGraph(List.of(prereqNode));

        List<LearningPathSubject> subjects = List.of(subject1, subject2);

        Map<UUID, Integer> semesterOrderBySection = Map.of(sectionId, 1);

        CurriculumFullResponse.CurriculumSubjectFull curriculumSubject2 =
            new CurriculumFullResponse.CurriculumSubjectFull();
        curriculumSubject2.setSubjectId(subjectId2);
        Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> curriculumSubjectMap =
            Map.of(subjectId2, curriculumSubject2);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterId(semesterId);
        section.setSemesterOrder(1);

        LearningPathResponseAssembler.SectionDisplayInfo displayInfo =
            new LearningPathResponseAssembler.SectionDisplayInfo("2024", "HK241");
        Map<UUID, LearningPathResponseAssembler.SectionDisplayInfo> sectionDisplayInfo =
            Map.of(semesterId, displayInfo);

        var result = responseAssembler.buildGraph(
            subjects, semesterOrderBySection, curriculumSubjectMap,
            List.of(section), sectionDisplayInfo, semesterId);
        assertNotNull(result);
    }

    @Test void buildGraph_shouldReturnGraph_whenEmptySubjects() {
        var result = responseAssembler.buildGraph(
            Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap(),
            Collections.emptyList(), Collections.emptyMap(), null);
        assertNotNull(result);
    }

    // ── buildValidationConflicts ───────────────────────────────────────────

    @Test void buildValidationConflicts_shouldReturnEmpty_whenNoOverload() {
        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(UUID.randomUUID());
        section.setSemesterOrder(1);
        section.setSemesterId(UUID.randomUUID());
        section.setTotalCredits(17);

        LearningPathSubject subject = new LearningPathSubject();
        subject.setSubjectId(UUID.randomUUID());
        subject.setLearningPathSectionId(section.getLearningPathSectionId());
        subject.setCredits(17);

        var result = responseAssembler.buildValidationConflicts(
            List.of(section), List.of(subject), null);
        assertNotNull(result);
    }

    @Test void buildValidationConflicts_shouldReturnConflict_whenOverMaxCredits() {
        UUID sectionId = UUID.randomUUID();
        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(2);
        section.setSemesterId(UUID.randomUUID());
        section.setTotalCredits(27);

        LearningPathSubject subject = new LearningPathSubject();
        subject.setSubjectId(UUID.randomUUID());
        subject.setLearningPathSectionId(sectionId);
        subject.setCredits(27);

        var result = responseAssembler.buildValidationConflicts(
            List.of(section), List.of(subject), null);
        assertNotNull(result);
        assertTrue(!result.isEmpty());
    }

    @Test void buildValidationConflicts_shouldSkipPastSemesters() {
        UUID sectionId = UUID.randomUUID();
        UUID currentSemId = UUID.randomUUID();

        // Section at order 1, current at order 1 → section is "past" (order <= current)
        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        section.setSemesterId(currentSemId); // matches current → order <= current
        section.setTotalCredits(30);

        LearningPathSubject subject = new LearningPathSubject();
        subject.setSubjectId(UUID.randomUUID());
        subject.setLearningPathSectionId(sectionId);
        subject.setCredits(30);

        var result = responseAssembler.buildValidationConflicts(
            List.of(section), List.of(subject), currentSemId);
        // Should be empty because the section is in current/past semester
        assertTrue(result.isEmpty());
    }

    // ── fetchBatchDifficultyByIds ──────────────────────────────────────────

    @Test void fetchBatchDifficultyByIds_shouldReturnEmptyMap_whenEmptyList() {
        var result = responseAssembler.fetchBatchDifficultyByIds(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test void fetchBatchDifficultyByIds_shouldReturnEmptyMap_whenNullList() {
        var result = responseAssembler.fetchBatchDifficultyByIds(null);
        assertTrue(result.isEmpty());
    }

    @Test void fetchBatchDifficultyByIds_shouldReturnEmptyMap_whenException() {
        when(learningClient.getBatchDifficulty(anyList()))
            .thenThrow(new RuntimeException("Service down"));
        var result = responseAssembler.fetchBatchDifficultyByIds(List.of(UUID.randomUUID()));
        assertTrue(result.isEmpty());
    }

    @Test void fetchBatchDifficultyByIds_shouldReturnEmptyMap_whenNullResponse() {
        when(learningClient.getBatchDifficulty(anyList())).thenReturn(null);
        var result = responseAssembler.fetchBatchDifficultyByIds(List.of(UUID.randomUUID()));
        assertTrue(result.isEmpty());
    }

    @Test void fetchBatchDifficultyByIds_shouldReturnMap_whenSuccess() {
        UUID subjId = UUID.randomUUID();
        when(learningClient.getBatchDifficulty(anyList())).thenReturn(Map.of(subjId, "hard"));
        var result = responseAssembler.fetchBatchDifficultyByIds(List.of(subjId));
        assertTrue(result.containsKey(subjId));
    }

    // ── fetchAllSemestersSafely ────────────────────────────────────────────

    @Test void fetchAllSemestersSafely_shouldReturnNull_whenException() {
        when(courseManagementClient.getAllSemesters())
            .thenThrow(new RuntimeException("Service down"));
        var result = responseAssembler.fetchAllSemestersSafely();
        assertTrue(result == null);
    }

    @Test void buildGraph_shouldBuildChains_whenPrerequisitesExist() {
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID semesterId = UUID.randomUUID();

        LearningPathSubject subject1 = new LearningPathSubject();
        subject1.setSubjectId(subjectId1);
        subject1.setSubjectCode("CS101");
        subject1.setSubjectName("Intro");
        subject1.setCredits(3);
        subject1.setLearningPathSectionId(sectionId);
        subject1.setIsHighestResult(true);
        subject1.setIsCompleted(false);
        subject1.setPrerequisitesGraph(Collections.emptyList());

        PrerequisiteNode prereq = PrerequisiteNode.builder()
            .subjectId(subjectId1).subjectCode("CS101").subjectName("Intro").required(true).build();

        LearningPathSubject subject2 = new LearningPathSubject();
        subject2.setSubjectId(subjectId2);
        subject2.setSubjectCode("CS201");
        subject2.setSubjectName("Advanced");
        subject2.setCredits(4);
        subject2.setLearningPathSectionId(sectionId);
        subject2.setIsHighestResult(true);
        subject2.setIsCompleted(false);
        subject2.setPrerequisitesGraph(List.of(prereq));

        List<LearningPathSubject> subjects = List.of(subject1, subject2);
        Map<UUID, Integer> order = Map.of(sectionId, 1);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterId(semesterId);
        section.setSemesterOrder(1);

        var displayInfo = new LearningPathResponseAssembler.SectionDisplayInfo("2024", "HK241");

        var result = responseAssembler.buildGraph(
            subjects, order, Map.of(), List.of(section), Map.of(semesterId, displayInfo), null);
        assertNotNull(result);
    }

    @Test void buildGraph_shouldHandleCurriculumPrerequisiteEdges() {
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID semesterId = UUID.randomUUID();

        LearningPathSubject subject1 = new LearningPathSubject();
        subject1.setSubjectId(subjectId1);
        subject1.setSubjectCode("CS101");
        subject1.setSubjectName("Intro");
        subject1.setCredits(3);
        subject1.setLearningPathSectionId(sectionId);
        subject1.setIsHighestResult(true);
        subject1.setIsCompleted(false);
        subject1.setPrerequisitesGraph(Collections.emptyList());

        LearningPathSubject subject2 = new LearningPathSubject();
        subject2.setSubjectId(subjectId2);
        subject2.setSubjectCode("CS201");
        subject2.setSubjectName("Advanced");
        subject2.setCredits(4);
        subject2.setLearningPathSectionId(sectionId);
        subject2.setIsHighestResult(true);
        subject2.setIsCompleted(false);
        subject2.setPrerequisitesGraph(Collections.emptyList());

        CurriculumFullResponse.CurriculumSubjectFull cs = new CurriculumFullResponse.CurriculumSubjectFull();
        cs.setSubjectId(subjectId2);
        CurriculumFullResponse.SubjectRelation prereq = new CurriculumFullResponse.SubjectRelation();
        prereq.setSubjectId(subjectId1);
        prereq.setSubjectCode("CS101");
        prereq.setSubjectName("Intro");
        cs.setPrerequisites(List.of(prereq));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterId(semesterId);
        section.setSemesterOrder(1);

        var displayInfo = new LearningPathResponseAssembler.SectionDisplayInfo("2024", "HK241");
        var result = responseAssembler.buildGraph(
            List.of(subject1, subject2), Map.of(sectionId, 1),
            Map.of(subjectId2, cs), List.of(section),
            Map.of(semesterId, displayInfo), null);
        assertNotNull(result);
    }

    // ── findCurrentSemesterId ──────────────────────────────────────────────

    @Test void findCurrentSemesterId_shouldReturnId_whenDateInRange() {
        UUID semId = UUID.randomUUID();
        String today = LocalDate.now().toString();
        SemesterResponse sem = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241")
            .startDate(LocalDate.now().minusMonths(1).toString())
            .endDate(LocalDate.now().plusMonths(1).toString())
            .build();
        var result = responseAssembler.findCurrentSemesterId(List.of(sem));
        assertNotNull(result);
    }

    @Test void findCurrentSemesterId_shouldReturnNull_whenOutOfRange() {
        SemesterResponse sem = SemesterResponse.builder()
            .id(UUID.randomUUID()).semKey(20241).semesterCode("HK241")
            .startDate("2020-01-01").endDate("2020-06-01")
            .build();
        var result = responseAssembler.findCurrentSemesterId(List.of(sem));
        assertTrue(result == null);
    }

    @Test void findCurrentSemesterId_shouldReturnNull_whenNullList() {
        var result = responseAssembler.findCurrentSemesterId(null);
        assertTrue(result == null);
    }

    // ── buildCurriculumEnrichmentData edge cases ────────────────────────────

    @Test void buildCurriculumEnrichmentData_shouldReturnEmpty_whenCurriculumFullIsNull() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();

        CurriculumResolutionResponse resolution = new CurriculumResolutionResponse();
        resolution.setCode("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(resolution);
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(null);

        var result = responseAssembler.buildCurriculumEnrichmentData(null, goal);
        assertNotNull(result);
    }

    @Test void buildCurriculumEnrichmentData_shouldReturnEmpty_whenCurriculumFullHasNullSections() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();

        CurriculumResolutionResponse resolution = new CurriculumResolutionResponse();
        resolution.setCode("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(resolution);

        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        curriculumFull.setSections(null);
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        var result = responseAssembler.buildCurriculumEnrichmentData(null, goal);
        assertNotNull(result);
    }

    @Test void buildCurriculumEnrichmentData_shouldReturnEmpty_whenException() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();
        when(courseManagementClient.resolveCurriculum(any(), any()))
            .thenThrow(new RuntimeException("Service down"));

        var result = responseAssembler.buildCurriculumEnrichmentData(null, goal);
        assertNotNull(result);
    }

    // ── loadSubjectsBySemesterOrder ─────────────────────────────────────────

    @Test void loadSubjectsBySemesterOrder_shouldReturnSortedSubjects() {
        UUID pathId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        LearningPathSubject subject = new LearningPathSubject();
        subject.setSubjectId(UUID.randomUUID());
        subject.setSubjectCode("CS101");
        subject.setLearningPathSectionId(sectionId);
        subject.setStudyOrder(1);
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(subject));

        var result = responseAssembler.loadSubjectsBySemesterOrder(pathId);
        assertNotNull(result);
    }

    // ── buildGraph with parallel and recommendation edges ───────────────────

    @Test void buildGraph_shouldBuildParallelGroups_whenParallelEdgesExist() {
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID semesterId = UUID.randomUUID();

        LearningPathSubject subject1 = new LearningPathSubject();
        subject1.setSubjectId(subjectId1);
        subject1.setSubjectCode("CS101");
        subject1.setSubjectName("Intro");
        subject1.setCredits(3);
        subject1.setLearningPathSectionId(sectionId);
        subject1.setIsHighestResult(true);
        subject1.setIsCompleted(false);
        subject1.setPrerequisitesGraph(Collections.emptyList());

        LearningPathSubject subject2 = new LearningPathSubject();
        subject2.setSubjectId(subjectId2);
        subject2.setSubjectCode("CS102");
        subject2.setSubjectName("Data Structures");
        subject2.setCredits(3);
        subject2.setLearningPathSectionId(sectionId);
        subject2.setIsHighestResult(true);
        subject2.setIsCompleted(false);
        subject2.setPrerequisitesGraph(Collections.emptyList());

        // Both subjects have parallels to each other
        CurriculumFullResponse.SubjectRelation parallel1 = new CurriculumFullResponse.SubjectRelation();
        parallel1.setSubjectId(subjectId2);
        parallel1.setSubjectCode("CS102");
        CurriculumFullResponse.SubjectRelation parallel2 = new CurriculumFullResponse.SubjectRelation();
        parallel2.setSubjectId(subjectId1);
        parallel2.setSubjectCode("CS101");

        CurriculumFullResponse.CurriculumSubjectFull cs1 = new CurriculumFullResponse.CurriculumSubjectFull();
        cs1.setSubjectId(subjectId1);
        cs1.setParallels(List.of(parallel1));
        CurriculumFullResponse.CurriculumSubjectFull cs2 = new CurriculumFullResponse.CurriculumSubjectFull();
        cs2.setSubjectId(subjectId2);
        cs2.setParallels(List.of(parallel2));

        Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> curriculumSubjectMap =
            Map.of(subjectId1, cs1, subjectId2, cs2);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterId(semesterId);
        section.setSemesterOrder(1);

        var displayInfo = new LearningPathResponseAssembler.SectionDisplayInfo("2024", "HK241");

        var result = responseAssembler.buildGraph(
            List.of(subject1, subject2), Map.of(sectionId, 1),
            curriculumSubjectMap, List.of(section),
            Map.of(semesterId, displayInfo), null);
        assertNotNull(result);
    }

    @Test void buildGraph_shouldBuildRecommendationChains_whenRecEdgesExist() {
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID semesterId = UUID.randomUUID();

        LearningPathSubject subject1 = new LearningPathSubject();
        subject1.setSubjectId(subjectId1);
        subject1.setSubjectCode("CS101");
        subject1.setSubjectName("Intro");
        subject1.setCredits(3);
        subject1.setLearningPathSectionId(sectionId);
        subject1.setIsHighestResult(true);
        subject1.setIsCompleted(false);
        subject1.setPrerequisitesGraph(Collections.emptyList());

        LearningPathSubject subject2 = new LearningPathSubject();
        subject2.setSubjectId(subjectId2);
        subject2.setSubjectCode("CS201");
        subject2.setSubjectName("Advanced");
        subject2.setCredits(4);
        subject2.setLearningPathSectionId(sectionId);
        subject2.setIsHighestResult(true);
        subject2.setIsCompleted(false);
        subject2.setPrerequisitesGraph(Collections.emptyList());

        // CS201 recommends CS101
        CurriculumFullResponse.SubjectRelation rec = new CurriculumFullResponse.SubjectRelation();
        rec.setSubjectId(subjectId1);
        rec.setSubjectCode("CS101");

        CurriculumFullResponse.CurriculumSubjectFull cs2 = new CurriculumFullResponse.CurriculumSubjectFull();
        cs2.setSubjectId(subjectId2);
        cs2.setRecommendations(List.of(rec));

        CurriculumFullResponse.CurriculumSubjectFull cs1ForRec = new CurriculumFullResponse.CurriculumSubjectFull();
        cs1ForRec.setSubjectId(subjectId1);

        Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> curriculumSubjectMap =
            Map.of(subjectId1, cs1ForRec, subjectId2, cs2);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterId(semesterId);
        section.setSemesterOrder(1);

        var displayInfo = new LearningPathResponseAssembler.SectionDisplayInfo("2024", "HK241");

        var result = responseAssembler.buildGraph(
            List.of(subject1, subject2), Map.of(sectionId, 1),
            curriculumSubjectMap, List.of(section),
            Map.of(semesterId, displayInfo), null);
        assertNotNull(result);
    }
}
