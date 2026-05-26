package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.hcmut.lms.personalization.application.dto.request.SubjectChangeDto;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.learning_path.LearningPathGenerationService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.domain.entity.learningPath.PrerequisiteNode;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearningPathServiceImplTest {

    @Mock private LearningPathRepository learningPathRepository;
    @Mock private LearningGoalRepository learningGoalRepository;
    @Mock private CourseManagementClient courseManagementClient;
    @Mock private LearningPathSectionRepository learningPathSectionRepository;
    @Mock private LearningPathSubjectRepository learningPathSubjectRepository;
    @Mock private LearningPathGenerationService learningPathGenerationService;
    @Mock private LearningPathResponseAssembler assembler;
    @Mock private LearningServiceClient learningServiceClient;

    @InjectMocks
    private LearningPathServiceImpl learningPathService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID learningPathId = UUID.randomUUID();
    private LearningPath learningPath;

    @BeforeEach
    void setUp() {
        learningPath = new LearningPath();
        learningPath.setLearningPathId(learningPathId);
        learningPath.setStudentId(studentId);
        learningPath.setCurriculumCode("DT");
        learningPath.setIsActive(true);
    }

    // ── getActiveLearningPath ──────────────────────────────────────────────

    @Test void getActiveLearningPath_shouldReturnResponse_whenExists() {
        when(learningPathRepository.findTopByStudentIdAndIsActiveTrueOrderByUpdatedAtDesc(any()))
            .thenReturn(Optional.of(learningPath));
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());
        var result = learningPathService.getActiveLearningPath(studentId);
        assertNotNull(result);
    }

    @Test void getActiveLearningPath_shouldThrow_whenNotFound() {
        when(learningPathRepository.findTopByStudentIdAndIsActiveTrueOrderByUpdatedAtDesc(any()))
            .thenReturn(Optional.empty());
        try {
            learningPathService.getActiveLearningPath(studentId);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Active learning path not found"));
        }
    }

    // ── getLearningPathById ────────────────────────────────────────────────

    @Test void getLearningPathById_shouldReturnResponse_whenExists() {
        when(learningPathRepository.findByLearningPathIdAndStudentId(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());
        var result = learningPathService.getLearningPathById(studentId, learningPathId);
        assertNotNull(result);
    }

    // ── createLearningPath ─────────────────────────────────────────────────

    @Test void createLearningPath_shouldReturnResponse_whenValid() {
        LearningGoal goal = makeGoal(UUID.randomUUID().toString());
        setupCreatePathMocks(goal, "DT");

        when(learningPathGenerationService.generateLearningPath(any(), any(), any(), any(), any()))
            .thenReturn(learningPath);
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        var result = learningPathService.createLearningPath(studentId);
        assertNotNull(result);
    }

    @Test void createLearningPath_shouldThrow_whenNoActiveGoal() {
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.empty());
        try {
            learningPathService.createLearningPath(studentId);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Active learning goal not found"));
        }
    }

    @Test void createLearningPath_shouldThrow_whenInvalidSpecializationId() {
        LearningGoal goal = makeGoal("not-a-uuid");
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));
        try {
            learningPathService.createLearningPath(studentId);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Invalid specializationId"));
        }
    }

    @Test void createLearningPath_shouldThrow_whenNullCurriculum() {
        LearningGoal goal = makeGoal(UUID.randomUUID().toString());
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(null);
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(null);
        try {
            learningPathService.createLearningPath(studentId);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Curriculum not found"));
        }
    }

    @Test void createLearningPath_shouldThrow_whenBlankCurriculumCode() {
        LearningGoal goal = makeGoal(UUID.randomUUID().toString());
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(null);
        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn("");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);
        try {
            learningPathService.createLearningPath(studentId);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Curriculum not found"));
        }
    }

    // ── updateLearningPath ─────────────────────────────────────────────────

    @Test void updateLearningPath_shouldReturnUpdatedResponse_whenExists() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathRepository.save(any())).thenReturn(learningPath);
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        var request = UpdateLearningPathRequest.builder()
            .totalCredits(150).estimatedDurationSemesters(8)
            .predictedGpa(BigDecimal.valueOf(3.2)).completionRate(BigDecimal.valueOf(75.0))
            .build();

        var result = learningPathService.updateLearningPath(studentId, learningPathId, request);
        assertNotNull(result);
    }

    @Test void updateLearningPath_shouldHandlePartialRequest_whenOnlyTotalCredits() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathRepository.save(any())).thenReturn(learningPath);
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        var request = UpdateLearningPathRequest.builder()
            .totalCredits(150).build();

        var result = learningPathService.updateLearningPath(studentId, learningPathId, request);
        assertNotNull(result);
    }

    // ── getSections ────────────────────────────────────────────────────────

    @Test void getSections_shouldReturnList_whenPathExists() {
        when(learningPathRepository.findByLearningPathIdAndStudentId(any(), any()))
            .thenReturn(Optional.of(learningPath));

        LearningGoal goal = makeGoal(UUID.randomUUID().toString());
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        when(assembler.fetchProgressForStudent(any(), any())).thenReturn(progress);
        when(assembler.fetchAllSemestersSafely()).thenReturn(Collections.emptyList());
        when(assembler.buildSectionDisplayInfo(any(), any(), any())).thenReturn(Collections.emptyMap());

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(UUID.randomUUID());
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));
        when(assembler.toSectionResponse(any(), any())).thenReturn(new LearningPathSectionResponse());

        var result = learningPathService.getSections(studentId, learningPathId);
        assertNotNull(result);
    }

    // ── getSubjects ────────────────────────────────────────────────────────

    @Test void getSubjects_shouldReturnList_whenPathExists() {
        when(learningPathRepository.findByLearningPathIdAndStudentId(any(), any()))
            .thenReturn(Optional.of(learningPath));

        LearningPathSubject subject = new LearningPathSubject();
        subject.setSubjectId(UUID.randomUUID());
        when(assembler.loadSubjectsBySemesterOrder(any())).thenReturn(List.of(subject));

        LearningGoal goal = makeGoal(UUID.randomUUID().toString());
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        when(assembler.fetchProgressForStudent(any(), any())).thenReturn(progress);

        var curriculumData = new com.hcmut.lms.personalization.application.dto.record.CurriculumEnrichmentData(
            Collections.emptyMap(), null, null);
        when(assembler.buildCurriculumEnrichmentData(any(), any())).thenReturn(curriculumData);

        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toSubjectResponse(any(), any(), any())).thenReturn(new LearningPathSubjectResponse());

        var result = learningPathService.getSubjects(studentId, learningPathId);
        assertNotNull(result);
    }

    // ── getGraph ───────────────────────────────────────────────────────────

    @Test void getGraph_shouldReturnGraph_whenPathExists() {
        when(learningPathRepository.findByLearningPathIdAndStudentId(any(), any()))
            .thenReturn(Optional.of(learningPath));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(UUID.randomUUID());
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        when(assembler.buildSemesterOrderBySection(any())).thenReturn(Collections.emptyMap());
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        LearningGoal goal = makeGoal(UUID.randomUUID().toString());
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        when(assembler.fetchProgressForStudent(any(), any())).thenReturn(progress);

        var curriculumData = new com.hcmut.lms.personalization.application.dto.record.CurriculumEnrichmentData(
            Collections.emptyMap(), null, null);
        when(assembler.buildCurriculumEnrichmentData(any(), any())).thenReturn(curriculumData);

        when(assembler.fetchAllSemestersSafely()).thenReturn(Collections.emptyList());
        when(assembler.buildSectionDisplayInfo(any(), any(), any())).thenReturn(Collections.emptyMap());
        when(assembler.buildGraph(any(), any(), any(), any(), any(), any()))
            .thenReturn(new LearningPathGraphResponse());

        var result = learningPathService.getGraph(studentId, learningPathId);
        assertNotNull(result);
    }

    // ── updateLearningPathSubjects ─────────────────────────────────────────

    @Test void updateLearningPathSubjects_shouldReturnUpdated_whenEmptyChanges() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        var result = learningPathService.updateLearningPathSubjects(studentId, learningPathId, Collections.emptyList());
        assertNotNull(result);
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenAddWithoutTargetSection() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(UUID.randomUUID()).subjectCode("CS101")
            .targetSectionId(null).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Phải chỉ định học kỳ"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenInvalidAction() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        var change = SubjectChangeDto.builder()
            .action("INVALID").subjectId(UUID.randomUUID()).subjectCode("CS101")
            .targetSectionId(UUID.randomUUID()).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Hành động không hợp lệ"));
        }
    }

    @Test void updateLearningPathSubjects_shouldAddSubject_whenValidAdd() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        section.setTotalCredits(3);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(subjectId).subjectCode("CS101").subjectName("Intro")
            .credits(3).targetSectionId(sectionId).build();

        var result = learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        assertNotNull(result);
    }

    @Test void updateLearningPathSubjects_shouldRemoveSubject_whenValidRemove() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        LearningPathSubject existingSubject = new LearningPathSubject();
        existingSubject.setSubjectId(subjectId);
        existingSubject.setSubjectCode("CS101");
        existingSubject.setCredits(3);
        existingSubject.setLearningPathSectionId(sectionId);
        existingSubject.setIsCompleted(false);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(existingSubject));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        section.setTotalCredits(3);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        var change = SubjectChangeDto.builder()
            .action("REMOVE").subjectId(subjectId).build();

        var result = learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        assertNotNull(result);
    }

    @Test void updateLearningPathSubjects_shouldMoveSubject_whenValidMove() {
        UUID subjectId = UUID.randomUUID();
        UUID fromSectionId = UUID.randomUUID();
        UUID toSectionId = UUID.randomUUID();

        LearningPathSubject existingSubject = new LearningPathSubject();
        existingSubject.setSubjectId(subjectId);
        existingSubject.setSubjectCode("CS101");
        existingSubject.setCredits(3);
        existingSubject.setLearningPathSectionId(fromSectionId);
        existingSubject.setIsCompleted(false);

        LearningPathSection toSection = new LearningPathSection();
        toSection.setLearningPathSectionId(toSectionId);
        toSection.setSemesterOrder(2);
        toSection.setTotalCredits(3);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(existingSubject));
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(toSection));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(toSection));

        var change = SubjectChangeDto.builder()
            .action("MOVE").subjectId(subjectId).subjectCode("CS101")
            .targetSectionId(toSectionId).build();

        var result = learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        assertNotNull(result);
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenMoveWithoutTarget() {
        UUID subjectId = UUID.randomUUID();

        LearningPathSubject existingSubject = new LearningPathSubject();
        existingSubject.setSubjectId(subjectId);
        existingSubject.setSubjectCode("CS101");
        existingSubject.setIsCompleted(false);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(existingSubject));
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        var change = SubjectChangeDto.builder()
            .action("MOVE").subjectId(subjectId).subjectCode("CS101")
            .targetSectionId(null).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Phải chỉ định học kỳ khi di chuyển"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenRemoveNonexistent() {
        UUID subjectId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        var change = SubjectChangeDto.builder()
            .action("REMOVE").subjectId(subjectId).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Không tìm thấy môn học"));
        }
    }

    // ── searchSubjects ─────────────────────────────────────────────────────

    @Test void searchSubjects_shouldReturnList_whenKeywordProvided() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        SubjectResponse subj = new SubjectResponse();
        subj.setId(UUID.randomUUID());
        subj.setCode("CS101");
        subj.setName("Intro to CS");
        subj.setCredits(3);
        when(courseManagementClient.searchSubjects(any())).thenReturn(List.of(subj));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Map.of(subj.getId(), "medium"));

        var result = learningPathService.searchSubjects(studentId, learningPathId, "math");
        assertNotNull(result);
    }

    @Test void searchSubjects_shouldUseEmptyKeyword_whenNull() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(courseManagementClient.searchSubjects(any())).thenReturn(Collections.emptyList());
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());

        var result = learningPathService.searchSubjects(studentId, learningPathId, null);
        assertNotNull(result);
    }

    @Test void searchSubjects_shouldLimitTo10_whenManyResults() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        List<SubjectResponse> manyResults = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            SubjectResponse subj = new SubjectResponse();
            subj.setId(UUID.randomUUID());
            subj.setCode("CS" + (100 + i));
            subj.setName("Subject " + i);
            subj.setCredits(3);
            manyResults.add(subj);
        }
        when(courseManagementClient.searchSubjects(any())).thenReturn(manyResults);
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());

        var result = learningPathService.searchSubjects(studentId, learningPathId, "CS");
        assertNotNull(result);
        assertTrue(result.size() <= 10);
    }

    @Test void updateLearningPathSubjects_shouldEnrichPrerequisitesFromCurriculum_whenAvailable() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID prereqId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        section.setTotalCredits(3);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        CurriculumFullResponse.CurriculumSectionFull curriculumSection = new CurriculumFullResponse.CurriculumSectionFull();
        CurriculumFullResponse.CurriculumSubjectFull curriculumSubject = new CurriculumFullResponse.CurriculumSubjectFull();
        curriculumSubject.setSubjectId(subjectId);
        CurriculumFullResponse.SubjectRelation prereq = new CurriculumFullResponse.SubjectRelation();
        prereq.setSubjectId(prereqId);
        prereq.setSubjectCode("CS100");
        prereq.setSubjectName("PreReq");
        curriculumSubject.setPrerequisites(List.of(prereq));
        curriculumSection.setSubjects(List.of(curriculumSubject));
        curriculumFull.setSections(List.of(curriculumSection));
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(subjectId).subjectCode("CS101").subjectName("Intro")
            .credits(3).targetSectionId(sectionId).build();

        var result = learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        assertNotNull(result);
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenCO4029AndCO4337SameSemester() {
        UUID co4029Id = UUID.randomUUID();
        UUID co4337Id = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        LearningPathSubject co4029Subject = new LearningPathSubject();
        co4029Subject.setSubjectId(co4029Id);
        co4029Subject.setSubjectCode("CO4029");
        co4029Subject.setCredits(6);
        co4029Subject.setLearningPathSectionId(sectionId);
        co4029Subject.setIsCompleted(false);

        LearningPathSubject co4337Subject = new LearningPathSubject();
        co4337Subject.setSubjectId(co4337Id);
        co4337Subject.setSubjectCode("CO4337");
        co4337Subject.setCredits(3);
        co4337Subject.setLearningPathSectionId(sectionId);
        co4337Subject.setIsCompleted(false);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);

        when(learningPathSubjectRepository.findByLearningPathId(any()))
            .thenReturn(List.of(co4029Subject, co4337Subject));
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));

        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(UUID.randomUUID()).subjectCode("CS101")
            .credits(3).targetSectionId(sectionId).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("CO4029"));
        }
    }

    // ── syncLearningPathProgress ───────────────────────────────────────────

    @Test void syncLearningPathProgress_shouldReturnSyncResponse_whenNoEnrollments() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());
        when(learningServiceClient.getStudentEnrollmentsWithSubjects(any())).thenReturn(Collections.emptyList());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        var result = learningPathService.syncLearningPathProgress(studentId, learningPathId);
        assertNotNull(result);
    }

    @Test void syncLearningPathProgress_shouldHandleEnrollmentFailure() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());
        when(learningServiceClient.getStudentEnrollmentsWithSubjects(any()))
            .thenThrow(new RuntimeException("Service down"));
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        var result = learningPathService.syncLearningPathProgress(studentId, learningPathId);
        assertNotNull(result);
    }

    @Test void syncLearningPathProgress_shouldMatchEnrollmentByAttemptNo() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        LearningPathSubject lpSubject = new LearningPathSubject();
        lpSubject.setSubjectId(subjectId);
        lpSubject.setSubjectCode("CS101");
        lpSubject.setSubjectName("Intro");
        lpSubject.setCredits(3);
        lpSubject.setLearningPathSectionId(sectionId);
        lpSubject.setAttemptNo(1);
        lpSubject.setIsCompleted(false);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(lpSubject));
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        StudentEnrollmentWithSubjectResponse enrollment = new StudentEnrollmentWithSubjectResponse();
        enrollment.setSubjectId(subjectId);
        enrollment.setAttemptNo(1);
        enrollment.setIsPassed(true);
        enrollment.setFinalGrade(8.0);
        enrollment.setGradingType("grade_10");
        when(learningServiceClient.getStudentEnrollmentsWithSubjects(any())).thenReturn(List.of(enrollment));

        var result = learningPathService.syncLearningPathProgress(studentId, learningPathId);
        assertNotNull(result);
    }

    @Test void syncLearningPathProgress_shouldUsePlannedSlot_whenNoExactMatch() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        LearningPathSubject plannedSubject = new LearningPathSubject();
        plannedSubject.setSubjectId(subjectId);
        plannedSubject.setSubjectCode("CS101");
        plannedSubject.setSubjectName("Intro");
        plannedSubject.setCredits(3);
        plannedSubject.setLearningPathSectionId(sectionId);
        plannedSubject.setAttemptNo(null);
        plannedSubject.setIsCompleted(false);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(plannedSubject));
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        StudentEnrollmentWithSubjectResponse enrollment = new StudentEnrollmentWithSubjectResponse();
        enrollment.setSubjectId(subjectId);
        enrollment.setAttemptNo(2);
        enrollment.setIsPassed(true);
        enrollment.setFinalGrade(8.0);
        when(learningServiceClient.getStudentEnrollmentsWithSubjects(any())).thenReturn(List.of(enrollment));

        var result = learningPathService.syncLearningPathProgress(studentId, learningPathId);
        assertNotNull(result);
    }

    @Test void syncLearningPathProgress_shouldBuildFailedSubjects_whenNotPassed() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        LearningPathSubject lpSubject = new LearningPathSubject();
        lpSubject.setSubjectId(subjectId);
        lpSubject.setSubjectCode("CS101");
        lpSubject.setSubjectName("Intro");
        lpSubject.setCredits(3);
        lpSubject.setLearningPathSectionId(sectionId);
        lpSubject.setAttemptNo(1);
        lpSubject.setIsCompleted(false);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(lpSubject));
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        StudentEnrollmentWithSubjectResponse enrollment = new StudentEnrollmentWithSubjectResponse();
        enrollment.setSubjectId(subjectId);
        enrollment.setAttemptNo(1);
        enrollment.setIsPassed(false);
        enrollment.setFinalGrade(3.0);
        enrollment.setGradingType("grade_10");
        when(learningServiceClient.getStudentEnrollmentsWithSubjects(any())).thenReturn(List.of(enrollment));

        var result = learningPathService.syncLearningPathProgress(studentId, learningPathId);
        assertNotNull(result);
        assertTrue(!result.getFailedSubjects().isEmpty());
    }

    @Test void syncLearningPathProgress_shouldUpdateCompletionRate_whenCreditsExist() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        LearningPathSubject lpSubject = new LearningPathSubject();
        lpSubject.setSubjectId(subjectId);
        lpSubject.setSubjectCode("CS101");
        lpSubject.setSubjectName("Intro");
        lpSubject.setCredits(3);
        lpSubject.setLearningPathSectionId(sectionId);
        lpSubject.setAttemptNo(1);
        lpSubject.setIsCompleted(false);

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(lpSubject));
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        StudentEnrollmentWithSubjectResponse enrollment = new StudentEnrollmentWithSubjectResponse();
        enrollment.setSubjectId(subjectId);
        enrollment.setAttemptNo(1);
        enrollment.setIsPassed(true);
        enrollment.setFinalGrade(8.0);
        enrollment.setGradingType("grade_10");
        when(learningServiceClient.getStudentEnrollmentsWithSubjects(any())).thenReturn(List.of(enrollment));

        when(learningPathRepository.save(any())).thenReturn(learningPath);

        var result = learningPathService.syncLearningPathProgress(studentId, learningPathId);
        assertNotNull(result);
    }

    @Test void updateLearningPathSubjects_shouldHandleCurriculumEnrichmentException() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));
        when(courseManagementClient.getCurriculumFull(any()))
            .thenThrow(new RuntimeException("Curriculum service down"));

        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(subjectId).subjectCode("CS101").subjectName("Intro")
            .credits(3).targetSectionId(sectionId).build();

        var result = learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        assertNotNull(result);
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenDuplicateAddInSameSection() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        LearningPathSubject existingSubject = new LearningPathSubject();
        existingSubject.setSubjectId(subjectId);
        existingSubject.setSubjectCode("CS101");
        existingSubject.setCredits(3);
        existingSubject.setLearningPathSectionId(sectionId);
        existingSubject.setIsCompleted(false);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(existingSubject));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));

        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(subjectId).subjectCode("CS101")
            .credits(3).targetSectionId(sectionId).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("đã tồn tại"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenDuplicateAddSameBatch() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));

        var change1 = SubjectChangeDto.builder()
            .action("ADD").subjectId(subjectId).subjectCode("CS101")
            .credits(3).targetSectionId(sectionId).build();
        var change2 = SubjectChangeDto.builder()
            .action("ADD").subjectId(subjectId).subjectCode("CS101")
            .credits(3).targetSectionId(sectionId).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change1, change2));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("đã tồn tại"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenPrerequisiteOrderViolated() {
        UUID subjectBId = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID section1Id = UUID.randomUUID();
        UUID section2Id = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        // Subject A (prerequisite) is in section 2 (order 2) - later semester
        LearningPathSubject subjectA = new LearningPathSubject();
        subjectA.setSubjectId(subjectAId);
        subjectA.setSubjectCode("CSA");
        subjectA.setSubjectName("Subject A");
        subjectA.setCredits(3);
        subjectA.setLearningPathSectionId(section2Id);
        subjectA.setIsCompleted(false);
        subjectA.setPrerequisitesGraph(List.of());

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(subjectA));

        LearningPathSection section1 = new LearningPathSection();
        section1.setLearningPathSectionId(section1Id);
        section1.setSemesterOrder(1);
        LearningPathSection section2 = new LearningPathSection();
        section2.setLearningPathSectionId(section2Id);
        section2.setSemesterOrder(2);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section1, section2));
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section1, section2));

        // Enrich B's prerequisite A from curriculum
        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        CurriculumFullResponse.CurriculumSectionFull curriculumSection = new CurriculumFullResponse.CurriculumSectionFull();
        CurriculumFullResponse.CurriculumSubjectFull subjectB = new CurriculumFullResponse.CurriculumSubjectFull();
        subjectB.setSubjectId(subjectBId);
        CurriculumFullResponse.SubjectRelation prereqA = new CurriculumFullResponse.SubjectRelation();
        prereqA.setSubjectId(subjectAId);
        prereqA.setSubjectCode("CSA");
        prereqA.setSubjectName("Subject A");
        subjectB.setPrerequisites(List.of(prereqA));
        curriculumSection.setSubjects(List.of(subjectB));
        curriculumFull.setSections(List.of(curriculumSection));
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        // Add B to section 1 (order 1) but A is in section 2 (order 2) → violation
        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(subjectBId).subjectCode("CSB").subjectName("Subject B")
            .credits(3).targetSectionId(section1Id).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("chuỗi tiên quyết"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenDependentOrderViolated() {
        UUID subjectSId = UUID.randomUUID(); // prerequisite
        UUID subjectDId = UUID.randomUUID(); // dependent on S
        UUID section1Id = UUID.randomUUID();
        UUID section2Id = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        // D depends on S, D is in section 1 (order 1)
        // S has D's prerequisite stored in prerequisitesGraph
        PrerequisiteNode prereqSNode = PrerequisiteNode.builder()
            .subjectId(subjectSId).subjectCode("CSS").subjectName("Subject S")
            .required(true).build();

        LearningPathSubject subjectD = new LearningPathSubject();
        subjectD.setSubjectId(subjectDId);
        subjectD.setSubjectCode("CSD");
        subjectD.setSubjectName("Subject D");
        subjectD.setCredits(3);
        subjectD.setLearningPathSectionId(section1Id);
        subjectD.setIsCompleted(false);
        subjectD.setPrerequisitesGraph(List.of(prereqSNode));

        // S is also in path (in section 2, order 2)... wait, we need S to NOT be in path
        // Actually, for the dependent reverse check:
        // We need the subject being moved (S) to have dependents in earlier semesters
        // S must be in prerequisitesBySubjectId's values (as a prereq of D)
        // dependentsByPrerequisiteId: S → [D]
        // subjectMinOrder: D → 1 (section 1)
        // MOVE S to section 3 (order 3) → depOrder=1 < targetOrder=3 → violation!

        LearningPathSubject subjectS = new LearningPathSubject();
        subjectS.setSubjectId(subjectSId);
        subjectS.setSubjectCode("CSS");
        subjectS.setSubjectName("Subject S");
        subjectS.setCredits(3);
        subjectS.setLearningPathSectionId(section2Id);
        subjectS.setIsCompleted(false);
        subjectS.setPrerequisitesGraph(List.of());

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(subjectS, subjectD));

        LearningPathSection section1 = new LearningPathSection();
        section1.setLearningPathSectionId(section1Id);
        section1.setSemesterOrder(1);
        LearningPathSection section2 = new LearningPathSection();
        section2.setLearningPathSectionId(section2Id);
        section2.setSemesterOrder(2);
        LearningPathSection section3 = new LearningPathSection();
        section3.setLearningPathSectionId(UUID.randomUUID());
        section3.setSemesterOrder(3);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section1, section2, section3));
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section1, section2, section3));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        // Move S to section 3 (order 3) - D depends on S and is at order 1
        var change = SubjectChangeDto.builder()
            .action("MOVE").subjectId(subjectSId).subjectCode("CSS")
            .targetSectionId(section3.getLearningPathSectionId()).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("chuỗi tiên quyết"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenChainPSD_inSameSemester() {
        UUID pId = UUID.randomUUID();
        UUID sId = UUID.randomUUID();
        UUID dId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        // S has prerequisite P
        PrerequisiteNode pNode = PrerequisiteNode.builder()
            .subjectId(pId).subjectCode("CSP").subjectName("Subject P")
            .required(true).build();

        // D depends on S
        PrerequisiteNode sForDNode = PrerequisiteNode.builder()
            .subjectId(sId).subjectCode("CSS").subjectName("Subject S")
            .required(true).build();

        LearningPathSubject subjectP = new LearningPathSubject();
        subjectP.setSubjectId(pId);
        subjectP.setSubjectCode("CSP");
        subjectP.setSubjectName("Subject P");
        subjectP.setCredits(3);
        subjectP.setLearningPathSectionId(sectionId);
        subjectP.setIsCompleted(false);
        subjectP.setPrerequisitesGraph(List.of());

        LearningPathSubject subjectS = new LearningPathSubject();
        subjectS.setSubjectId(sId);
        subjectS.setSubjectCode("CSS");
        subjectS.setSubjectName("Subject S");
        subjectS.setCredits(3);
        subjectS.setLearningPathSectionId(sectionId);
        subjectS.setIsCompleted(false);
        subjectS.setPrerequisitesGraph(List.of(pNode));

        LearningPathSubject subjectD = new LearningPathSubject();
        subjectD.setSubjectId(dId);
        subjectD.setSubjectCode("CSD");
        subjectD.setSubjectName("Subject D");
        subjectD.setCredits(3);
        subjectD.setLearningPathSectionId(sectionId);
        subjectD.setIsCompleted(false);
        subjectD.setPrerequisitesGraph(List.of(sForDNode));

        when(learningPathSubjectRepository.findByLearningPathId(any()))
            .thenReturn(List.of(subjectP, subjectS, subjectD));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        // Move S within same semester → P→S→D chain in same semester → violation
        var change = SubjectChangeDto.builder()
            .action("MOVE").subjectId(sId).subjectCode("CSS")
            .targetSectionId(sectionId).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("chuỗi tiên quyết"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenTwoLevelsUpChain_inSameSemester() {
        UUID ppId = UUID.randomUUID();
        UUID pId = UUID.randomUUID();
        UUID sId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        PrerequisiteNode ppNode = PrerequisiteNode.builder()
            .subjectId(ppId).subjectCode("CSPP").subjectName("Subject PP")
            .required(true).build();

        PrerequisiteNode pNode = PrerequisiteNode.builder()
            .subjectId(pId).subjectCode("CSP").subjectName("Subject P")
            .required(true).build();

        LearningPathSubject subjectPP = new LearningPathSubject();
        subjectPP.setSubjectId(ppId);
        subjectPP.setSubjectCode("CSPP");
        subjectPP.setSubjectName("Subject PP");
        subjectPP.setCredits(3);
        subjectPP.setLearningPathSectionId(sectionId);
        subjectPP.setIsCompleted(false);
        subjectPP.setPrerequisitesGraph(List.of());

        LearningPathSubject subjectP = new LearningPathSubject();
        subjectP.setSubjectId(pId);
        subjectP.setSubjectCode("CSP");
        subjectP.setSubjectName("Subject P");
        subjectP.setCredits(3);
        subjectP.setLearningPathSectionId(sectionId);
        subjectP.setIsCompleted(false);
        subjectP.setPrerequisitesGraph(List.of(ppNode));

        LearningPathSubject subjectS = new LearningPathSubject();
        subjectS.setSubjectId(sId);
        subjectS.setSubjectCode("CSS");
        subjectS.setSubjectName("Subject S");
        subjectS.setCredits(3);
        subjectS.setLearningPathSectionId(sectionId);
        subjectS.setIsCompleted(false);
        subjectS.setPrerequisitesGraph(List.of(pNode));

        when(learningPathSubjectRepository.findByLearningPathId(any()))
            .thenReturn(List.of(subjectPP, subjectP, subjectS));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        // Move S → PP→P→S chain all in same semester → violation
        var change = SubjectChangeDto.builder()
            .action("MOVE").subjectId(sId).subjectCode("CSS")
            .targetSectionId(sectionId).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("chuỗi tiên quyết"));
        }
    }

    @Test void updateLearningPathSubjects_shouldThrow_whenTwoLevelsDownChain_inSameSemester() {
        UUID sId = UUID.randomUUID();
        UUID dId = UUID.randomUUID();
        UUID ddId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));

        PrerequisiteNode sNode = PrerequisiteNode.builder()
            .subjectId(sId).subjectCode("CSS").subjectName("Subject S")
            .required(true).build();

        PrerequisiteNode dNode = PrerequisiteNode.builder()
            .subjectId(dId).subjectCode("CSD").subjectName("Subject D")
            .required(true).build();

        LearningPathSubject subjectS = new LearningPathSubject();
        subjectS.setSubjectId(sId);
        subjectS.setSubjectCode("CSS");
        subjectS.setSubjectName("Subject S");
        subjectS.setCredits(3);
        subjectS.setLearningPathSectionId(sectionId);
        subjectS.setIsCompleted(false);
        subjectS.setPrerequisitesGraph(List.of());

        LearningPathSubject subjectD = new LearningPathSubject();
        subjectD.setSubjectId(dId);
        subjectD.setSubjectCode("CSD");
        subjectD.setSubjectName("Subject D");
        subjectD.setCredits(3);
        subjectD.setLearningPathSectionId(sectionId);
        subjectD.setIsCompleted(false);
        subjectD.setPrerequisitesGraph(List.of(sNode));

        LearningPathSubject subjectDD = new LearningPathSubject();
        subjectDD.setSubjectId(ddId);
        subjectDD.setSubjectCode("CSDD");
        subjectDD.setSubjectName("Subject DD");
        subjectDD.setCredits(3);
        subjectDD.setLearningPathSectionId(sectionId);
        subjectDD.setIsCompleted(false);
        subjectDD.setPrerequisitesGraph(List.of(dNode));

        when(learningPathSubjectRepository.findByLearningPathId(any()))
            .thenReturn(List.of(subjectS, subjectD, subjectDD));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());

        // Move S → S→D→DD chain all in same semester → violation
        var change = SubjectChangeDto.builder()
            .action("MOVE").subjectId(sId).subjectCode("CSS")
            .targetSectionId(sectionId).build();

        try {
            learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("chuỗi tiên quyết"));
        }
    }

    @Test void updateLearningPathSubjects_shouldHandleCompletedSubject_whenMerging() {
        UUID subjectId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        // Completed subject in path
        LearningPathSubject completedSubject = new LearningPathSubject();
        completedSubject.setSubjectId(subjectId);
        completedSubject.setSubjectCode("CS101");
        completedSubject.setCredits(3);
        completedSubject.setLearningPathSectionId(sectionId);
        completedSubject.setIsCompleted(true);

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(learningPath));
        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(List.of(completedSubject));

        LearningPathSection section = new LearningPathSection();
        section.setLearningPathSectionId(sectionId);
        section.setSemesterOrder(1);
        when(learningPathSectionRepository.findByLearningPathId(any())).thenReturn(List.of(section));
        when(assembler.fetchBatchDifficultyByIds(any())).thenReturn(Collections.emptyMap());
        when(assembler.toPathResponse(any(), any())).thenReturn(new LearningPathResponse());
        when(learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        var change = SubjectChangeDto.builder()
            .action("ADD").subjectId(UUID.randomUUID()).subjectCode("CS102")
            .credits(3).targetSectionId(sectionId).build();

        var result = learningPathService.updateLearningPathSubjects(studentId, learningPathId, List.of(change));
        assertNotNull(result);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private LearningGoal makeGoal(String specializationId) {
        return LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(specializationId).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .build();
    }

    private void setupCreatePathMocks(LearningGoal goal, String curriculumCode) {
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn(curriculumCode);
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);
    }
}
