package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.SubjectLearningOutcomeResponse;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectLearningOutcome;
import com.hcmut.lms.coursemanagement.repository.SubjectLearningOutcomeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectLearningOutcomeServiceImplTest {

    @Mock
    private SubjectLearningOutcomeRepository sloRepository;

    @InjectMocks
    private SubjectLearningOutcomeServiceImpl subjectLearningOutcomeService;

    @Test
    void getBySubjectId_shouldReturnParentWithChildren_whenHierarchyExists() {
        UUID subjectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        SubjectLearningOutcome parent = createSlo(parentId, "SLO-1", "Parent description", null, 1);
        SubjectLearningOutcome child = createSlo(UUID.randomUUID(), "SLO-1.1", "Child description", null, 2);
        child.setParent(parent);
        // Important: the child's parent ID must match the parent's ID
        // parent ID is set via parent field, accessible via child.getParent().getId()

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(parent, child));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(parentId);
        assertThat(result.getFirst().getCode()).isEqualTo("SLO-1");
        // Description should merge parent + child descriptions
        assertThat(result.getFirst().getDescription()).contains("Parent description");
        assertThat(result.getFirst().getDescription()).contains("Child description");
        assertThat(result.getFirst().getDisplayOrder()).isEqualTo(1);
    }

    @Test
    void getBySubjectId_shouldReturnEmptyList_whenNoOutcomes() {
        UUID subjectId = UUID.randomUUID();
        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of());

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).isEmpty();
    }

    @Test
    void getBySubjectId_shouldReturnOnlyRoots_whenNoChildren() {
        UUID subjectId = UUID.randomUUID();
        SubjectLearningOutcome slo1 = createSlo(UUID.randomUUID(), "SLO-1", "Description 1", null, 1);
        SubjectLearningOutcome slo2 = createSlo(UUID.randomUUID(), "SLO-2", "Description 2", null, 2);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(slo1, slo2));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("SLO-1");
        assertThat(result.get(0).getDescription()).isEqualTo("Description 1");
        assertThat(result.get(1).getCode()).isEqualTo("SLO-2");
        assertThat(result.get(1).getDescription()).isEqualTo("Description 2");
    }

    @Test
    void getBySubjectId_shouldTreatOrphanChildAsRoot_whenParentNotInList() {
        UUID subjectId = UUID.randomUUID();
        UUID missingParentId = UUID.randomUUID(); // parent not in the list

        // Create a child whose parent is not in the list
        SubjectLearningOutcome missingParent = new SubjectLearningOutcome();
        missingParent.setId(missingParentId);

        SubjectLearningOutcome orphan = createSlo(UUID.randomUUID(), "SLO-ORPHAN", "Orphan description", null, 1);
        orphan.setParent(missingParent);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(orphan));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        // Orphan should be treated as root
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isEqualTo("SLO-ORPHAN");
        assertThat(result.getFirst().getDescription()).isEqualTo("Orphan description");
    }

    @Test
    void getBySubjectId_shouldIncludeDescriptionEn_whenEnglishTextExists() {
        UUID subjectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        SubjectLearningOutcome parent = createSlo(parentId, "SLO-1", null, "Parent EN description", 1);
        SubjectLearningOutcome child = createSlo(UUID.randomUUID(), "SLO-1.1", null, "Child EN description", 2);
        child.setParent(parent);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(parent, child));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDescriptionEn()).contains("Parent EN description");
        assertThat(result.getFirst().getDescriptionEn()).contains("Child EN description");
    }

    @Test
    void getBySubjectId_shouldHandleNullParent_whenParentFieldIsNull() {
        UUID subjectId = UUID.randomUUID();
        SubjectLearningOutcome root = createSlo(UUID.randomUUID(), "SLO-1", "Root description", null, 1);
        root.setParent(null);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(root));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isEqualTo("SLO-1");
    }

    @Test
    void getBySubjectId_shouldHandleNullParentId_whenParentExistsButIdIsNull() {
        UUID subjectId = UUID.randomUUID();

        SubjectLearningOutcome parentWithNullId = new SubjectLearningOutcome();
        parentWithNullId.setId(null);

        SubjectLearningOutcome child = createSlo(UUID.randomUUID(), "SLO-CHILD", "Child desc", null, 1);
        child.setParent(parentWithNullId);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(child));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        // Child with parent id null should be treated as root
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isEqualTo("SLO-CHILD");
    }

    @Test
    void getBySubjectId_shouldSkipBlankParentDescription_whenMerging() {
        UUID subjectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        SubjectLearningOutcome parent = createSlo(parentId, "SLO-1", "", null, 1); // blank description
        SubjectLearningOutcome child = createSlo(UUID.randomUUID(), "SLO-1.1", "Child description", null, 2);
        child.setParent(parent);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(parent, child));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(1);
        // Blank parent description should be skipped, only child description included
        assertThat(result.getFirst().getDescription()).isEqualTo("Child description");
    }

    @Test
    void getBySubjectId_shouldReturnNullDescription_whenAllDescriptionsBlank() {
        UUID subjectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        SubjectLearningOutcome parent = createSlo(parentId, "SLO-1", "   ", null, 1); // whitespace
        SubjectLearningOutcome child = createSlo(UUID.randomUUID(), "SLO-1.1", null, null, 2);
        child.setParent(parent);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(parent, child));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDescription()).isNull();
    }

    @Test
    void getBySubjectId_shouldUseEnglishFallbackForChild_whenDescriptionEnBlank() {
        UUID subjectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        SubjectLearningOutcome parent = createSlo(parentId, "SLO-1", "Parent desc", "Parent EN desc", 1);
        SubjectLearningOutcome child = createSlo(UUID.randomUUID(), "SLO-1.1", "Child desc", null, 2);
        child.setParent(parent);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(parent, child));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(1);
        // descriptionEn should use parent's EN + child's description (fallback since child's EN is null)
        assertThat(result.getFirst().getDescriptionEn()).contains("Parent EN desc");
        assertThat(result.getFirst().getDescriptionEn()).contains("Child desc");
    }

    @Test
    void getBySubjectId_shouldHandleMultipleIndependentRoots() {
        UUID subjectId = UUID.randomUUID();
        SubjectLearningOutcome root1 = createSlo(UUID.randomUUID(), "SLO-1", "Desc 1", null, 1);
        SubjectLearningOutcome root2 = createSlo(UUID.randomUUID(), "SLO-2", "Desc 2", null, 2);
        SubjectLearningOutcome root3 = createSlo(UUID.randomUUID(), "SLO-3", "Desc 3", null, 3);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(root1, root2, root3));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCode()).isEqualTo("SLO-1");
        assertThat(result.get(1).getCode()).isEqualTo("SLO-2");
        assertThat(result.get(2).getCode()).isEqualTo("SLO-3");
    }

    @Test
    void getBySubjectId_shouldMergeMultipleChildrenWithNewlines() {
        UUID subjectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        SubjectLearningOutcome parent = createSlo(parentId, "SLO-1", "Parent description", null, 1);
        SubjectLearningOutcome child1 = createSlo(UUID.randomUUID(), "SLO-1.1", "Child 1 description", null, 2);
        child1.setParent(parent);
        SubjectLearningOutcome child2 = createSlo(UUID.randomUUID(), "SLO-1.2", "Child 2 description", null, 3);
        child2.setParent(parent);

        when(sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId))
                .thenReturn(List.of(parent, child1, child2));

        List<SubjectLearningOutcomeResponse> result = subjectLearningOutcomeService.getBySubjectId(subjectId);

        assertThat(result).hasSize(1);
        String description = result.getFirst().getDescription();
        assertThat(description).contains("Parent description");
        assertThat(description).contains("Child 1 description");
        assertThat(description).contains("Child 2 description");
        // Should be separated by newlines
        assertThat(description).contains("\n");
        // Check order: parent first, then children
        String[] lines = description.split("\n");
        assertThat(lines[0]).isEqualTo("Parent description");
        assertThat(lines[1]).isEqualTo("Child 1 description");
        assertThat(lines[2]).isEqualTo("Child 2 description");
    }

    // --- helper methods ---

    private SubjectLearningOutcome createSlo(UUID id, String code, String description, String descriptionEn, int displayOrder) {
        SubjectLearningOutcome slo = new SubjectLearningOutcome();
        slo.setId(id);
        slo.setCode(code);
        slo.setDescription(description);
        slo.setDescriptionEn(descriptionEn);
        slo.setDisplayOrder(displayOrder);
        return slo;
    }
}
