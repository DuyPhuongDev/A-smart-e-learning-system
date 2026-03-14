package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.SubjectLearningOutcomeResponse;
import com.hcmut.lms.coursemanagement.application.service.SubjectLearningOutcomeService;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectLearningOutcome;
import com.hcmut.lms.coursemanagement.repository.SubjectLearningOutcomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubjectLearningOutcomeServiceImpl implements SubjectLearningOutcomeService {

    private final SubjectLearningOutcomeRepository sloRepository;

    @Override
    public List<SubjectLearningOutcomeResponse> getBySubjectId(UUID subjectId) {
        log.info("Fetching SLOs for subject {}", subjectId);
        List<SubjectLearningOutcome> entities = sloRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId);

        Map<UUID, List<SubjectLearningOutcome>> childrenByParentId = entities.stream()
                .filter(e -> e.getParent() != null && e.getParent().getId() != null)
                .collect(Collectors.groupingBy(e -> e.getParent().getId()));
        HashSet<UUID> allIds = entities.stream().map(SubjectLearningOutcome::getId).collect(Collectors.toCollection(HashSet::new));

        return entities.stream()
                // Keep roots only; orphan children are treated as roots to avoid losing data.
                .filter(e -> e.getParent() == null || e.getParent().getId() == null || !allIds.contains(e.getParent().getId()))
                .map(parent -> {
                    List<SubjectLearningOutcome> children = childrenByParentId.getOrDefault(parent.getId(), List.of());
                    return SubjectLearningOutcomeResponse.builder()
                            .id(parent.getId())
                            .code(parent.getCode())
                            .description(mergeText(parent.getDescription(), children, false))
                            .descriptionEn(mergeText(parent.getDescriptionEn(), children, true))
                            .displayOrder(parent.getDisplayOrder())
                            .build();
                })
                .toList();
    }

    private String mergeText(String parentText, List<SubjectLearningOutcome> children, boolean english) {
        List<String> parts = new ArrayList<>();
        if (parentText != null && !parentText.isBlank()) {
            parts.add(parentText.trim());
        }

        for (SubjectLearningOutcome child : children) {
            String childText = english
                    ? firstNonBlank(child.getDescriptionEn(), child.getDescription())
                    : child.getDescription();
            if (childText != null && !childText.isBlank()) {
                parts.add(childText.trim());
            }
        }

        return parts.isEmpty() ? null : String.join("\n", parts);
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}
