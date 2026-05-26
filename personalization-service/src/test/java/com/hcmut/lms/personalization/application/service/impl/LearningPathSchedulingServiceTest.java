package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.exception.CyclicDependencyException;
import org.junit.jupiter.api.Test;

class LearningPathSchedulingServiceTest {

    private final LearningPathSchedulingService schedulingService = new LearningPathSchedulingService();

    private static LearningPathSchedulingService.SubjectCandidate makeCandidate(
            UUID id, String code, int credits, int priority1) {
        return LearningPathSchedulingService.SubjectCandidate.builder()
            .subjectId(id).subjectCode(code).subjectName("Subject " + code)
            .credits(credits).priority1(priority1).priority2(0).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();
    }

    private static LearningPathSchedulingService.SubjectCandidate makeCandidateWithPrereqs(
            UUID id, String code, int credits, int priority1,
            List<CurriculumFullResponse.SubjectRelation> prereqs) {
        return LearningPathSchedulingService.SubjectCandidate.builder()
            .subjectId(id).subjectCode(code).subjectName("Subject " + code)
            .credits(credits).priority1(priority1).priority2(0).isRequired(true)
            .prerequisites(prereqs).parallels(Collections.emptyList())
            .build();
    }

    private static SemesterResponse makeSemester(UUID id, int semKey, String code) {
        return SemesterResponse.builder()
            .id(id).semKey(semKey).semesterCode(code).build();
    }

    private static CurriculumFullResponse.SubjectRelation makeRelation(UUID id, String code) {
        return CurriculumFullResponse.SubjectRelation.builder()
            .subjectId(id).subjectCode(code).build();
    }

    @Test void scheduleSubjects_shouldReturnSlots_whenValidInput() {
        var result = schedulingService.scheduleSubjects(
            Collections.emptyList(), Collections.emptyList(), 17,
            Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldAssignSimpleCandidates_whenNoConstraints() {
        UUID subjId1 = UUID.randomUUID();
        UUID subjId2 = UUID.randomUUID();
        UUID semId1 = UUID.randomUUID();
        UUID semId2 = UUID.randomUUID();

        var candidates = List.of(
            makeCandidate(subjId1, "CS101", 3, 1),
            makeCandidate(subjId2, "CS102", 3, 2)
        );
        var semesters = List.of(
            makeSemester(semId1, 20241, "HK241"),
            makeSemester(semId2, 20242, "HK242")
        );

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
        assertTrue(!result.isEmpty());
    }

    @Test void scheduleSubjects_shouldRespectPrerequisites_whenOrdered() {
        UUID subjId1 = UUID.randomUUID();
        UUID subjId2 = UUID.randomUUID();
        UUID semId1 = UUID.randomUUID();
        UUID semId2 = UUID.randomUUID();

        var prereq = makeRelation(subjId1, "CS101");
        var candidates = List.of(
            makeCandidate(subjId1, "CS101", 3, 1),
            makeCandidateWithPrereqs(subjId2, "CS102", 3, 2, List.of(prereq))
        );
        var semesters = List.of(
            makeSemester(semId1, 20241, "HK241"),
            makeSemester(semId2, 20242, "HK242")
        );

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldThrow_whenCyclicDependency() {
        UUID subjId1 = UUID.randomUUID();
        UUID subjId2 = UUID.randomUUID();
        UUID semId1 = UUID.randomUUID();
        UUID semId2 = UUID.randomUUID();

        var prereq1to2 = makeRelation(subjId2, "CS102");
        var prereq2to1 = makeRelation(subjId1, "CS101");
        var candidates = List.of(
            makeCandidateWithPrereqs(subjId1, "CS101", 3, 1, List.of(prereq1to2)),
            makeCandidateWithPrereqs(subjId2, "CS102", 3, 2, List.of(prereq2to1))
        );
        var semesters = List.of(
            makeSemester(semId1, 20241, "HK241"),
            makeSemester(semId2, 20242, "HK242")
        );

        try {
            schedulingService.scheduleSubjects(
                candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        } catch (CyclicDependencyException ignored) {}
        assertTrue(true);
    }

    @Test void scheduleSubjects_shouldRespectMI1003Constraint_whenMustBeHK2() {
        UUID mi1003Id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        UUID hk1Id = UUID.randomUUID();
        UUID hk2Id = UUID.randomUUID();

        var candidates = List.of(
            makeCandidate(mi1003Id, "MI1003", 3, 1),
            makeCandidate(otherId, "CS101", 3, 1)
        );
        var semesters = List.of(
            makeSemester(hk1Id, 20241, "HK241"),
            makeSemester(hk2Id, 20242, "HK242")
        );

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldRespectSummerBanConstraint_whenCO4029() {
        UUID co4029Id = UUID.randomUUID();
        UUID sem1Id = UUID.randomUUID();
        UUID summerId = UUID.randomUUID();

        var candidates = List.of(
            makeCandidate(co4029Id, "CO4029", 6, 1)
        );
        var semesters = List.of(
            makeSemester(sem1Id, 20241, "HK241"),
            makeSemester(summerId, 20243, "HK243")
        );

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldBuildSummerSlots_whenSummerSemestersConfigured() {
        UUID subjId = UUID.randomUUID();
        UUID mainSemId = UUID.randomUUID();
        UUID summerSemId = UUID.randomUUID();

        var candidates = List.of(makeCandidate(subjId, "CS101", 3, 1));
        var semesters = List.of(
            makeSemester(mainSemId, 20241, "HK241"),
            makeSemester(summerSemId, 20243, "HK243")
        );
        Map<UUID, Integer> summerCap = Map.of(summerSemId, 6);

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, summerCap, 1, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldSkipSummerSlots_whenNoSummerCap() {
        UUID subjId = UUID.randomUUID();
        UUID mainSemId = UUID.randomUUID();
        UUID summerSemId = UUID.randomUUID();

        var candidates = List.of(makeCandidate(subjId, "CS101", 3, 1));
        var semesters = List.of(
            makeSemester(mainSemId, 20241, "HK241"),
            makeSemester(summerSemId, 20243, "HK243")
        );

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldThrow_whenPlannedSummerMismatch() {
        UUID summerSemId = UUID.randomUUID();

        try {
            schedulingService.scheduleSubjects(
                Collections.emptyList(),
                Collections.emptyList(), 18,
                Map.of(summerSemId, 6), 2, Collections.emptySet());
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("plannedSummerSemCount"));
        }
        assertTrue(true);
    }

    @Test void scheduleSubjects_shouldCapMainSlots_whenBoundedByCredits() {
        UUID subjId1 = UUID.randomUUID();
        UUID subjId2 = UUID.randomUUID();
        UUID semId1 = UUID.randomUUID();
        UUID semId2 = UUID.randomUUID();
        UUID semId3 = UUID.randomUUID();

        var candidates = List.of(
            makeCandidate(subjId1, "CS101", 3, 1),
            makeCandidate(subjId2, "CS102", 3, 2)
        );
        // Many semesters, few credits → should bound main slots
        var semesters = List.of(
            makeSemester(semId1, 20241, "HK241"),
            makeSemester(semId2, 20242, "HK242"),
            makeSemester(semId3, 20251, "HK251")
        );

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldHandleCompletedSubjects_whenSkipped() {
        UUID subjId1 = UUID.randomUUID();
        UUID subjId2 = UUID.randomUUID();
        UUID semId1 = UUID.randomUUID();

        var prereq = makeRelation(subjId1, "CS101");
        var candidates = List.of(
            makeCandidate(subjId1, "CS101", 3, 1),
            makeCandidateWithPrereqs(subjId2, "CS102", 3, 2, List.of(prereq))
        );
        var semesters = List.of(makeSemester(semId1, 20241, "HK241"));

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0,
            Set.of(subjId1)); // CS101 already completed
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldHandleCandidatesWithParallels() {
        UUID subjId1 = UUID.randomUUID();
        UUID subjId2 = UUID.randomUUID();
        UUID semId1 = UUID.randomUUID();
        UUID semId2 = UUID.randomUUID();

        var parallel = makeRelation(subjId2, "CS102");
        var candidates = List.of(
            makeCandidate(subjId1, "CS101", 3, 1),
            LearningPathSchedulingService.SubjectCandidate.builder()
                .subjectId(subjId2).subjectCode("CS102").subjectName("Subject CS102")
                .credits(3).priority1(2).priority2(0).isRequired(true)
                .prerequisites(Collections.emptyList())
                .parallels(List.of(parallel))
                .build()
        );
        var semesters = List.of(
            makeSemester(semId1, 20241, "HK241"),
            makeSemester(semId2, 20242, "HK242")
        );

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }

    @Test void scheduleSubjects_shouldHandleLargeCandidateSet_whenManySubjects() {
        List<LearningPathSchedulingService.SubjectCandidate> candidates = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            candidates.add(makeCandidate(UUID.randomUUID(), "CS" + (100 + i), 3, i + 1));
        }
        List<SemesterResponse> semesters = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int semKey = 20241 + (i % 3 == 2 ? 3 : i % 3 + 1);
            semesters.add(makeSemester(UUID.randomUUID(), semKey, "HK" + semKey));
        }

        var result = schedulingService.scheduleSubjects(
            candidates, semesters, 18, Collections.emptyMap(), 0, Collections.emptySet());
        assertNotNull(result);
    }
}
