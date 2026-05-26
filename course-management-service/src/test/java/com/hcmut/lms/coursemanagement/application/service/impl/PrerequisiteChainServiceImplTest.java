package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.PrerequisiteChainRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.PrerequisiteChainResponse;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrerequisiteChainServiceImplTest {

    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;

    @InjectMocks
    private PrerequisiteChainServiceImpl prerequisiteChainService;

    private static final UUID SPEC_ID = UUID.randomUUID();
    private static final UUID SUBJECT_A = UUID.randomUUID();
    private static final UUID SUBJECT_B = UUID.randomUUID();
    private static final UUID SUBJECT_C = UUID.randomUUID();
    private static final UUID SUBJECT_D = UUID.randomUUID();

    // --- Helpers ---

    private CurriculumSubject buildCs(UUID subjectId, String code) {
        Subject s = new Subject();
        s.setId(subjectId);
        s.setCode(code);
        s.setName("Subject " + code);

        CurriculumSubject cs = new CurriculumSubject();
        cs.setSubject(s);
        cs.setPrerequisites(new java.util.ArrayList<>());
        return cs;
    }

    private void addPrerequisite(CurriculumSubject cs, CurriculumSubject prereqCs) {
        SubjectPrerequisite sp = new SubjectPrerequisite();
        sp.setCurriculumSubject(cs);
        sp.setPrerequisiteCurriculumSubject(prereqCs);
        cs.getPrerequisites().add(sp);
    }

    // --- Tests with specialization ---

    @Test
    void calculatePrerequisiteChain_shouldReturnZero_whenEmptySubjects() {
        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of())
                .remainingSubjectIds(List.of())
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of());

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        assertEquals(0, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldReturnOne_whenSingleRemainingSubjectNoPrereqs() {
        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of())
                .remainingSubjectIds(List.of(SUBJECT_A))
                .build();

        CurriculumSubject csA = buildCs(SUBJECT_A, "A");
        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        assertEquals(1, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldReturnCorrectLength_whenChainOfPrerequisites() {
        // A -> B -> C (B depends on A, C depends on B)
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");
        CurriculumSubject csB = buildCs(SUBJECT_B, "B");
        CurriculumSubject csC = buildCs(SUBJECT_C, "C");
        addPrerequisite(csB, csA);
        addPrerequisite(csC, csB);

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of())
                .remainingSubjectIds(List.of(SUBJECT_A, SUBJECT_B, SUBJECT_C))
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA, csB, csC));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        // Longest chain starting from A: C depends on B depends on A -> 3
        assertEquals(3, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldExcludeCompletedSubjects() {
        // A -> B -> C, but A is completed. So chain is B -> C = 2
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");
        CurriculumSubject csB = buildCs(SUBJECT_B, "B");
        CurriculumSubject csC = buildCs(SUBJECT_C, "C");
        addPrerequisite(csB, csA);
        addPrerequisite(csC, csB);

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of(SUBJECT_A))
                .remainingSubjectIds(List.of(SUBJECT_B, SUBJECT_C))
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA, csB, csC));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        // B -> C, since A is completed B has no remaining prerequisites -> chain length 2
        assertEquals(2, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldHandleAllCompleted() {
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");
        CurriculumSubject csB = buildCs(SUBJECT_B, "B");
        addPrerequisite(csB, csA);

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of(SUBJECT_A, SUBJECT_B))
                .remainingSubjectIds(List.of())
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA, csB));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        assertEquals(0, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldDetectCircularDependency() {
        // A -> B -> A (circular)
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");
        CurriculumSubject csB = buildCs(SUBJECT_B, "B");
        addPrerequisite(csA, csB);
        addPrerequisite(csB, csA);

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of())
                .remainingSubjectIds(List.of(SUBJECT_A, SUBJECT_B))
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA, csB));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        // Circular dependency detected; each subject should only be counted once
        assertEquals(2, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldHandleMultiplePaths() {
        // D depends on B and C, B depends on A, C depends on A
        // Longest path to D: A -> B -> D or A -> C -> D, both length 3
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");
        CurriculumSubject csB = buildCs(SUBJECT_B, "B");
        CurriculumSubject csC = buildCs(SUBJECT_C, "C");
        CurriculumSubject csD = buildCs(SUBJECT_D, "D");
        addPrerequisite(csB, csA);
        addPrerequisite(csC, csA);
        addPrerequisite(csD, csB);
        addPrerequisite(csD, csC);

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of())
                .remainingSubjectIds(List.of(SUBJECT_A, SUBJECT_B, SUBJECT_C, SUBJECT_D))
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA, csB, csC, csD));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        assertEquals(3, response.getLongestChainLength());
    }

    // --- Tests without remainingSubjectIds (backward-compatible fallback) ---

    @Test
    void calculatePrerequisiteChain_shouldInferRemainingSubjects_whenNotProvided() {
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");
        CurriculumSubject csB = buildCs(SUBJECT_B, "B");

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of(SUBJECT_A))
                .remainingSubjectIds(null)
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA, csB));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        // B is remaining (not completed), no prereqs -> 1
        assertEquals(1, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldHandleNullCompletedSubjectIds() {
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(null)
                .remainingSubjectIds(List.of(SUBJECT_A))
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        assertEquals(1, response.getLongestChainLength());
    }

    @Test
    void calculatePrerequisiteChain_shouldHandleEmptyRemainingSubjectIds() {
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of())
                .remainingSubjectIds(List.of())  // empty but not null - fallback infers A as remaining
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        assertEquals(1, response.getLongestChainLength()); // inferred from curriculum minus completed
    }

    // --- Tests without specialization ---

    @Test
    void calculatePrerequisiteChain_shouldUseAllSubjects_whenNoSpecialization() {
        CurriculumSubject csA = buildCs(SUBJECT_A, "A");

        PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
                .specializationId(SPEC_ID.toString())
                .completedSubjectIds(List.of())
                .remainingSubjectIds(List.of(SUBJECT_A))
                .build();

        when(curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(SPEC_ID))
                .thenReturn(List.of(csA));

        PrerequisiteChainResponse response = prerequisiteChainService.calculatePrerequisiteChain(request);

        assertEquals(1, response.getLongestChainLength());
    }
}
