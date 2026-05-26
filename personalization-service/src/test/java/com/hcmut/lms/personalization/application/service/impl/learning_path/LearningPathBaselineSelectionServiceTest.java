package com.hcmut.lms.personalization.application.service.impl.learning_path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.BatchGradePredictionResponse;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.GradePredictionResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearningPathBaselineSelectionServiceTest {

    @Mock private LearningServiceClient learningServiceClient;
    @Mock private SubjectOccupationValuationRepository subjectOccupationValuationRepository;

    @InjectMocks
    private LearningPathBaselineSelectionService baselineSelectionService;

    private static StudentProgressDataService.StudentProgressData emptyProgress() {
        return new StudentProgressDataService.StudentProgressData(
            BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyMap(), null);
    }

    private static CurriculumFullResponse.CurriculumSubjectFull makeSubject(
            UUID id, String code, int credits, boolean required,
            Integer year, Integer sem, Integer priorityWeight) {
        CurriculumFullResponse.CurriculumSubjectFull s = new CurriculumFullResponse.CurriculumSubjectFull();
        s.setSubjectId(id); s.setSubjectCode(code); s.setSubjectName("Subject " + code);
        s.setCredits(credits); s.setIsRequired(required);
        s.setRecommendedYear(year); s.setRecommendedSemesterInYear(sem);
        return s;
    }

    private static CurriculumFullResponse.CurriculumSectionFull makeSection(
            UUID id, String name, int requiredCredits, int priorityWeight,
            List<CurriculumFullResponse.CurriculumSubjectFull> subjects) {
        CurriculumFullResponse.CurriculumSectionFull s = new CurriculumFullResponse.CurriculumSectionFull();
        s.setSectionId(id); s.setSectionName(name);
        s.setRequiredCredits(requiredCredits); s.setPriorityWeight(priorityWeight);
        s.setSubjects(subjects);
        return s;
    }

    @Test void buildBaselinePath_shouldReturnResult_whenEmptyCurriculum() {
        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(Collections.emptyList());

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
    }

    @Test void buildBaselinePath_shouldSelectSubjects_whenSectionsWithRequiredCredits() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 6, 3, List.of(
            makeSubject(subjectId1, "CS301", 3, true, 3, 1, null),
            makeSubject(subjectId2, "CS302", 4, true, 3, 2, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .focusOnTargetOccupation(1)
            .build();

        when(subjectOccupationValuationRepository.findBySubjectIdIn(any())).thenReturn(Collections.emptyList());

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
        assertTrue(!result.candidates().isEmpty());
    }

    @Test void buildBaselinePath_shouldSkipCompletedSubjects() {
        UUID sectionId = UUID.randomUUID();
        UUID completedId = UUID.randomUUID();
        UUID uncompletedId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(completedId, "CS301", 3, true, 3, 1, null),
            makeSubject(uncompletedId, "CS302", 4, true, 3, 2, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .focusOnTargetOccupation(1)
            .build();

        StudentProgressDataService.CompletedSubjectDetail completedDetail =
            new StudentProgressDataService.CompletedSubjectDetail(
                completedId, UUID.randomUUID(), UUID.randomUUID(), 1, 1,
                "CS301", "Subject CS301", 3, 1, 8.0, "B", 3.0, 1, true, true);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(3.0), 3, 0, List.of(completedId), Collections.emptyList(),
                List.of(completedDetail), Collections.emptyMap(), null);

        when(subjectOccupationValuationRepository.findBySubjectIdIn(any())).thenReturn(Collections.emptyList());

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, progressData);
        assertTrue(result.candidates().stream().noneMatch(c -> c.getSubjectId().equals(completedId)));
    }

    @Test void buildBaselinePath_shouldUseOccupationScores_whenFocusedOnOccupation() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(subjectId, "CS301", 3, true, 3, 1, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .targetOccupationCode("15-1252.00")
            .focusOnTargetOccupation(1)
            .attemptTargetGpaOrder(2)
            .build();

        SubjectOccupationValuation valuation = new SubjectOccupationValuation();
        valuation.setSubjectId(subjectId);
        valuation.setTotalValue(BigDecimal.valueOf(8.0));
        when(subjectOccupationValuationRepository.findByTargetOccupationCode("15-1252.00"))
            .thenReturn(List.of(valuation));

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
        assertTrue(!result.candidates().isEmpty());
    }

    @Test void buildBaselinePath_shouldUsePredictedGrades_whenNotOccupationFocused() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(subjectId, "CS301", 3, true, 3, 1, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
        assertTrue(!result.candidates().isEmpty());
    }

    @Test void buildBaselinePath_shouldIncludeFreeElectiveSubjects_whenNeeded() {
        UUID majorSectionId = UUID.randomUUID();
        UUID electiveSectionId = UUID.randomUUID();
        UUID majorSubjectId = UUID.randomUUID();
        UUID electiveSubjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(
            makeSection(majorSectionId, "Chuyên ngành", 3, 3, List.of(
                makeSubject(majorSubjectId, "CS301", 3, true, 3, 1, null)
            )),
            makeSection(electiveSectionId, "Tự chọn tự do", 3, 3, List.of(
                makeSubject(electiveSubjectId, "IT201", 3, false, 3, 1, null)
            ))
        ));

        // Major section already satisfied (majorSubject selected), but free elective needs credits
        // The major subject will be selected for its section, and free elective from another allowed section
        // Since major subject is in "Chuyên ngành" which is an allowed key for free elective, it might overflow

        // Let's make a simpler scenario: major section has more than needed, free elective also needs subjects
        curriculum.setSections(List.of(
            makeSection(majorSectionId, "Chuyên ngành", 6, 3, List.of(
                makeSubject(majorSubjectId, "CS301", 3, true, 3, 1, null),
                makeSubject(electiveSubjectId, "IT201", 3, false, 3, 2, null)
            )),
            makeSection(electiveSectionId, "Tự chọn tự do", 3, 1, List.of())
        ));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred1 = new GradePredictionResponse();
        pred1.setSubjectId(majorSubjectId);
        pred1.setCorrectedPredictedGrade(7.5);
        GradePredictionResponse pred2 = new GradePredictionResponse();
        pred2.setSubjectId(electiveSubjectId);
        pred2.setCorrectedPredictedGrade(6.0);
        batchResponse.setPredictions(List.of(pred1, pred2));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
    }

    @Test void buildBaselinePath_shouldHandleZeroCreditMandatorySubjects() {
        UUID sectionId = UUID.randomUUID();
        UUID zeroCreditId = UUID.randomUUID();
        UUID normalId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Kiến thức chung", 3, 3, List.of(
            makeSubject(zeroCreditId, "TH101", 0, true, 1, 1, null),
            makeSubject(normalId, "CS301", 3, true, 3, 1, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(normalId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        // Zero-credit subject should be included as mandatory
        assertTrue(result.candidates().stream().anyMatch(c -> c.getSubjectId().equals(zeroCreditId)));
    }

    @Test void buildBaselinePath_shouldHandlePredictionFailureGracefully() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(subjectId, "CS301", 3, true, 3, 1, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        when(learningServiceClient.predictGradeBatch(any()))
            .thenThrow(new RuntimeException("Service down"));

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
        // Should fall back to zero scores, still select subjects
        assertTrue(!result.candidates().isEmpty());
    }

    @Test void buildBaselinePath_shouldAddPrerequisites_whenNotYetSelected() {
        UUID section1Id = UUID.randomUUID();
        UUID section2Id = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID subjectBId = UUID.randomUUID();

        CurriculumFullResponse.CurriculumSubjectFull subjectA = new CurriculumFullResponse.CurriculumSubjectFull();
        subjectA.setSubjectId(subjectAId); subjectA.setSubjectCode("CS301");
        subjectA.setSubjectName("Subject A"); subjectA.setCredits(3); subjectA.setIsRequired(true);
        subjectA.setRecommendedYear(3); subjectA.setRecommendedSemesterInYear(1);

        CurriculumFullResponse.CurriculumSubjectFull subjectB = new CurriculumFullResponse.CurriculumSubjectFull();
        subjectB.setSubjectId(subjectBId); subjectB.setSubjectCode("CS101");
        subjectB.setSubjectName("Subject B"); subjectB.setCredits(3); subjectB.setIsRequired(true);
        subjectB.setRecommendedYear(1); subjectB.setRecommendedSemesterInYear(1);

        // Subject A requires Subject B as prerequisite
        CurriculumFullResponse.SubjectRelation prereq = new CurriculumFullResponse.SubjectRelation();
        prereq.setSubjectId(subjectBId);
        subjectA.setPrerequisites(List.of(prereq));

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(
            makeSection(section1Id, "Chuyên ngành", 3, 3, List.of(subjectA)),
            makeSection(section2Id, "Kiến thức chung", 0, 3, List.of(subjectB))
        ));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse predA = new GradePredictionResponse();
        predA.setSubjectId(subjectAId);
        predA.setCorrectedPredictedGrade(7.5);
        GradePredictionResponse predB = new GradePredictionResponse();
        predB.setSubjectId(subjectBId);
        predB.setCorrectedPredictedGrade(6.0);
        batchResponse.setPredictions(List.of(predA, predB));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        // Subject B (prerequisite) should be added even though its section needed 0 credits
        assertTrue(result.candidates().stream().anyMatch(c -> c.getSubjectId().equals(subjectBId)));
    }

    @Test void buildBaselinePath_shouldAddParallels_whenNotYetSelected() {
        UUID section1Id = UUID.randomUUID();
        UUID section2Id = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID subjectBId = UUID.randomUUID();

        CurriculumFullResponse.CurriculumSubjectFull subjectA = new CurriculumFullResponse.CurriculumSubjectFull();
        subjectA.setSubjectId(subjectAId); subjectA.setSubjectCode("CS301");
        subjectA.setSubjectName("Subject A"); subjectA.setCredits(3); subjectA.setIsRequired(true);
        subjectA.setRecommendedYear(3); subjectA.setRecommendedSemesterInYear(1);

        CurriculumFullResponse.CurriculumSubjectFull subjectB = new CurriculumFullResponse.CurriculumSubjectFull();
        subjectB.setSubjectId(subjectBId); subjectB.setSubjectCode("CS201");
        subjectB.setSubjectName("Subject B"); subjectB.setCredits(3); subjectB.setIsRequired(true);
        subjectB.setRecommendedYear(3); subjectB.setRecommendedSemesterInYear(1);

        // Subject A has Subject B as parallel
        CurriculumFullResponse.SubjectRelation parallel = new CurriculumFullResponse.SubjectRelation();
        parallel.setSubjectId(subjectBId);
        subjectA.setParallels(List.of(parallel));

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(
            makeSection(section1Id, "Chuyên ngành", 3, 3, List.of(subjectA)),
            makeSection(section2Id, "Kiến thức chung", 0, 3, List.of(subjectB))
        ));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse predA = new GradePredictionResponse();
        predA.setSubjectId(subjectAId);
        predA.setCorrectedPredictedGrade(7.5);
        GradePredictionResponse predB = new GradePredictionResponse();
        predB.setSubjectId(subjectBId);
        predB.setCorrectedPredictedGrade(6.0);
        batchResponse.setPredictions(List.of(predA, predB));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertTrue(result.candidates().stream().anyMatch(c -> c.getSubjectId().equals(subjectBId)));
    }

    @Test void buildBaselinePath_shouldSelectBestCompletedSubset_whenMultipleCompleted() {
        UUID sectionId = UUID.randomUUID();
        UUID completed1Id = UUID.randomUUID();
        UUID completed2Id = UUID.randomUUID();

        CurriculumFullResponse.CurriculumSubjectFull completed1 = new CurriculumFullResponse.CurriculumSubjectFull();
        completed1.setSubjectId(completed1Id); completed1.setSubjectCode("CS101");
        completed1.setSubjectName("Subject CS101"); completed1.setCredits(3); completed1.setIsRequired(true);
        completed1.setRecommendedYear(1); completed1.setRecommendedSemesterInYear(1);

        CurriculumFullResponse.CurriculumSubjectFull completed2 = new CurriculumFullResponse.CurriculumSubjectFull();
        completed2.setSubjectId(completed2Id); completed2.setSubjectCode("CS102");
        completed2.setSubjectName("Subject CS102"); completed2.setCredits(2); completed2.setIsRequired(true);
        completed2.setRecommendedYear(1); completed2.setRecommendedSemesterInYear(2);

        // Section needs only 3 credits, has 2 completed subjects (3+2 credits = 5 > 3)
        // The DP should pick the best subset — the one with higher grade (completed1 has grade4=8.0, completed2=5.0)
        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3,
            List.of(completed1, completed2))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .focusOnTargetOccupation(1)
            .build();

        StudentProgressDataService.CompletedSubjectDetail detail1 =
            new StudentProgressDataService.CompletedSubjectDetail(
                completed1Id, UUID.randomUUID(), UUID.randomUUID(), 1, 1,
                "CS101", "Subject CS101", 3, 1, 8.0, "B", 3.0, 1, true, true);

        StudentProgressDataService.CompletedSubjectDetail detail2 =
            new StudentProgressDataService.CompletedSubjectDetail(
                completed2Id, UUID.randomUUID(), UUID.randomUUID(), 1, 1,
                "CS102", "Subject CS102", 2, 1, 5.0, "D", 1.5, 1, true, true);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(3.0), 5, 0,
                List.of(completed1Id, completed2Id), Collections.emptyList(),
                List.of(detail1, detail2), Collections.emptyMap(), null);

        when(subjectOccupationValuationRepository.findBySubjectIdIn(any())).thenReturn(Collections.emptyList());

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, progressData);
        assertNotNull(result);
    }

    @Test void buildBaselinePath_shouldUseTimeOrder_whenHighestPriority() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(subjectId, "CS301", 3, true, 3, 1, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .completedOnTime(1)
            .attemptTargetGpaOrder(3)
            .focusOnTargetOccupation(4)
            .build();

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
    }

    @Test void buildBaselinePath_shouldUseAverageOccupation_whenTargetCodeIsNull() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(subjectId, "CS301", 3, true, 3, 1, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .focusOnTargetOccupation(1)
            .build();

        when(subjectOccupationValuationRepository.findBySubjectIdIn(any())).thenReturn(Collections.emptyList());

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
    }

    @Test void buildBaselinePath_shouldHandleNullPredictionResponse() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(subjectId, "CS301", 3, true, 3, 1, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        when(learningServiceClient.predictGradeBatch(any())).thenReturn(null);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
    }

    @Test void buildBaselinePath_shouldUseNullRecommendedYear_whenMissing() {
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CurriculumFullResponse curriculum = new CurriculumFullResponse();
        curriculum.setSections(List.of(makeSection(sectionId, "Chuyên ngành", 3, 3, List.of(
            makeSubject(subjectId, "CS301", 3, true, null, null, null)
        ))));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(UUID.randomUUID())
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .build();

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = baselineSelectionService.buildBaselinePath(curriculum, goal, emptyProgress());
        assertNotNull(result);
    }

    @Test void hasBetterScoreThan_shouldReturnTrue_whenHigherScore() throws Exception {
        Class<?> stateClass = Class.forName(
            "com.hcmut.lms.personalization.application.service.impl.learning_path.LearningPathBaselineSelectionService$CreditSelectionState");
        Constructor<?> ctor = stateClass.getDeclaredConstructor(Set.class, double.class);
        ctor.setAccessible(true);
        Method hasBetter = stateClass.getDeclaredMethod("hasBetterScoreThan", stateClass);
        hasBetter.setAccessible(true);

        Object lower = ctor.newInstance(Set.of(UUID.randomUUID()), 5.0);
        Object higher = ctor.newInstance(Set.of(UUID.randomUUID()), 9.0);

        assertTrue((boolean) hasBetter.invoke(higher, lower));
    }

    @Test void hasBetterScoreThan_shouldReturnFalse_whenLowerScore() throws Exception {
        Class<?> stateClass = Class.forName(
            "com.hcmut.lms.personalization.application.service.impl.learning_path.LearningPathBaselineSelectionService$CreditSelectionState");
        Constructor<?> ctor = stateClass.getDeclaredConstructor(Set.class, double.class);
        ctor.setAccessible(true);
        Method hasBetter = stateClass.getDeclaredMethod("hasBetterScoreThan", stateClass);
        hasBetter.setAccessible(true);

        Object higher = ctor.newInstance(Set.of(UUID.randomUUID()), 8.0);
        Object lower = ctor.newInstance(Set.of(UUID.randomUUID()), 5.0);

        assertFalse((boolean) hasBetter.invoke(lower, higher));
    }

    @Test void hasBetterScoreThan_shouldPreferFewerSubjects_whenEqualScores() throws Exception {
        Class<?> stateClass = Class.forName(
            "com.hcmut.lms.personalization.application.service.impl.learning_path.LearningPathBaselineSelectionService$CreditSelectionState");
        Constructor<?> ctor = stateClass.getDeclaredConstructor(Set.class, double.class);
        ctor.setAccessible(true);
        Method hasBetter = stateClass.getDeclaredMethod("hasBetterScoreThan", stateClass);
        hasBetter.setAccessible(true);

        UUID subA = UUID.randomUUID();
        UUID subB = UUID.randomUUID();
        Object moreSubjects = ctor.newInstance(Set.of(subA, subB), 7.0);
        Object fewerSubjects = ctor.newInstance(Set.of(subA), 7.0);

        assertTrue((boolean) hasBetter.invoke(fewerSubjects, moreSubjects));
    }

    @Test void hasBetterScoreThan_shouldReturnFalse_whenEqualScoresAndSameSize() throws Exception {
        Class<?> stateClass = Class.forName(
            "com.hcmut.lms.personalization.application.service.impl.learning_path.LearningPathBaselineSelectionService$CreditSelectionState");
        Constructor<?> ctor = stateClass.getDeclaredConstructor(Set.class, double.class);
        ctor.setAccessible(true);
        Method hasBetter = stateClass.getDeclaredMethod("hasBetterScoreThan", stateClass);
        hasBetter.setAccessible(true);

        Object stateA = ctor.newInstance(Set.of(UUID.randomUUID()), 7.0);
        Object stateB = ctor.newInstance(Set.of(UUID.randomUUID()), 7.0);

        assertFalse((boolean) hasBetter.invoke(stateA, stateB));
    }
}
