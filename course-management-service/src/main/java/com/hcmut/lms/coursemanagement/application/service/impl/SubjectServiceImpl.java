package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectGradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectGradingWeightResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;
import com.hcmut.lms.coursemanagement.application.mapper.SubjectMapper;
import com.hcmut.lms.coursemanagement.application.service.SubjectService;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Grading;
import com.hcmut.lms.coursemanagement.domain.entity.subject.GradingType;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGrading;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingId;
import com.hcmut.lms.coursemanagement.exception.DomainException;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectRepository;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectGradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private static final float TOTAL_WEIGHT    = 100.0f;
    private static final float WEIGHT_TOLERANCE = 0.01f;

    private final SubjectRepository subjectRepository;
    private final CurriculumSubjectRepository curriculumSubjectRepository;
    private final SubjectMapper subjectMapper;
    private final GradingRepository gradingRepository;
    private final SubjectGradingRepository subjectGradingRepository;

    @Override
    public SubjectResponse createSubject(SubjectRequest request) {
        log.info("Creating subject: {}", request.getName());

        if (request.getCode() != null && subjectRepository.existsByCode(request.getCode())) {
            throw new EntityExistsException("Subject with code " + request.getCode() + " already exists");
        }

        Subject subject = subjectMapper.toEntity(request);
        Subject savedSubject = subjectRepository.save(subject);

        if (request.getGradings() != null && !request.getGradings().isEmpty()) {
            validateAndSaveSubjectGradings(savedSubject, request.getGradings());
        }

        log.info("Subject created successfully with id: {}", savedSubject.getId());
        return toResponseWithGradings(savedSubject);
    }

    @Override
    public SubjectResponse updateSubject(UUID id, SubjectRequest request) {
        log.info("Updating subject with id: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        if (request.getCode() != null && !request.getCode().equals(subject.getCode())) {
            if (subjectRepository.existsByCode(request.getCode())) {
                throw new EntityExistsException("Subject with code " + request.getCode() + " already exists");
            }
        }

        subjectMapper.updateEntityFromRequest(request, subject);
        Subject updatedSubject = subjectRepository.save(subject);

        if (request.getGradings() != null && !request.getGradings().isEmpty()) {
            subjectGradingRepository.deleteAll(subjectGradingRepository.findBySubjectId(id));
            validateAndSaveSubjectGradings(updatedSubject, request.getGradings());
        }

        log.info("Subject updated successfully with id: {}", id);
        return toResponseWithGradings(updatedSubject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(UUID id) {
        log.info("Getting subject with id: {}", id);
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
        return toResponseWithGradings(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectByCode(String code) {
        log.info("Getting subject with code: {}", code);
        Subject subject = subjectRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with code: " + code));
        return toResponseWithGradings(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        log.info("Getting all subjects");
        return subjectRepository.findAll().stream()
                .map(this::toResponseWithGradings)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubjectResponse> getAllSubjects(int page, int size, String keyword) {
        log.info("Getting subjects - page: {}, size: {}, keyword: {}", page, size, keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<Subject> subjectPage;

        if (keyword == null || keyword.isBlank()) {
            subjectPage = subjectRepository.findAll(pageable);
        } else {
            Optional<Subject> subject = subjectRepository.findByCode(keyword);
            if (subject.isPresent()) {
                subjectPage = new PageImpl<>(List.of(subject.get()), pageable, 1);
            } else {
                subjectPage = subjectRepository.findByNameContainingIgnoreCase(keyword, pageable);
            }
        }

        Page<SubjectResponse> responsePage = subjectPage.map(this::toResponseWithGradings);
        return PageResponse.fromPage(responsePage);
    }

    @Override
    public void deleteSubject(UUID id) {
        log.info("Deleting subject with id: {}", id);
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
        log.info("Subject deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectGradingWeightResponse> getGradingsForSubject(UUID subjectId) {
        log.info("Getting gradings for subject: {}", subjectId);
        if (!subjectRepository.existsById(subjectId)) {
            throw new EntityNotFoundException("Subject not found with id: " + subjectId);
        }
        return subjectGradingRepository.findBySubjectId(subjectId).stream()
                .map(this::toSubjectGradingWeightResponse)
                .toList();
    }

    @Override
    public List<SubjectGradingWeightResponse> setGradingsForSubject(UUID subjectId,
                                                                     List<SubjectGradingRequest> gradings) {
        log.info("Setting gradings for subject: {}", subjectId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + subjectId));

        subjectGradingRepository.deleteAll(subjectGradingRepository.findBySubjectId(subjectId));
        List<SubjectGrading> saved = validateAndSaveSubjectGradings(subject, gradings);

        log.info("Gradings set for subject {}: {} entries", subjectId, saved.size());
        return saved.stream().map(this::toSubjectGradingWeightResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectPrerequisiteMapResponse> getPrerequisiteMapping() {
        log.info("Loading prerequisite + recommendation mapping for all subjects");

        List<CurriculumSubject> allCS = curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations();
        Map<UUID, Set<UUID>> mapping = new HashMap<>();

        for (CurriculumSubject cs : allCS) {
            UUID subjectId = cs.getSubject() != null ? cs.getSubject().getId() : null;
            if (subjectId == null) continue;

            try {
                if (cs.getPrerequisites() != null) {
                    for (var prereq : cs.getPrerequisites()) {
                        if (prereq.getPrerequisiteCurriculumSubject() != null
                                && prereq.getPrerequisiteCurriculumSubject().getSubject() != null) {
                            mapping.computeIfAbsent(subjectId, k -> new HashSet<>())
                                    .add(prereq.getPrerequisiteCurriculumSubject().getSubject().getId());
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Could not load prerequisites for subject {}: {}", subjectId, e.getMessage());
            }

            try {
                if (cs.getRecommendations() != null) {
                    for (var rec : cs.getRecommendations()) {
                        if (rec.getRecommendedCurriculumSubject() != null
                                && rec.getRecommendedCurriculumSubject().getSubject() != null) {
                            mapping.computeIfAbsent(subjectId, k -> new HashSet<>())
                                    .add(rec.getRecommendedCurriculumSubject().getSubject().getId());
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Could not load recommendations for subject {}: {}", subjectId, e.getMessage());
            }
        }

        log.info("Built prerequisite mapping for {} subjects", mapping.size());
        return mapping.entrySet().stream()
                .map(e -> SubjectPrerequisiteMapResponse.builder()
                        .subjectId(e.getKey())
                        .relatedSubjectIds(new ArrayList<>(e.getValue()))
                        .build())
                .toList();
    }

    // -------------------------------------------------------------------------
    // PRIVATE HELPERS
    // -------------------------------------------------------------------------

    private List<SubjectGrading> validateAndSaveSubjectGradings(Subject subject,
                                                                  List<SubjectGradingRequest> requests) {
        float total = 0.0f;
        for (SubjectGradingRequest r : requests) {
            total += r.getWeight() != null ? r.getWeight() : 0.0f;
        }
        if (Math.abs(total - TOTAL_WEIGHT) > WEIGHT_TOLERANCE) {
            throw new DomainException(String.format(
                    "Grading weights must sum to 100%%. Current sum: %.2f%%", total));
        }

        List<SubjectGrading> saved = new ArrayList<>();
        for (SubjectGradingRequest r : requests) {
            GradingType type = parseGradingType(r.getGradingType());

            String name = (r.getName() != null && !r.getName().isBlank())
                    ? r.getName()
                    : capitalize(type.name().toLowerCase());

            Grading grading = Grading.builder()
                    .name(name)
                    .description(r.getDescription())
                    .gradingType(type)
                    .build();
            grading = gradingRepository.save(grading);

            SubjectGrading sg = SubjectGrading.builder()
                    .id(new SubjectGradingId(subject.getId(), grading.getId()))
                    .subject(subject)
                    .grading(grading)
                    .weight(r.getWeight())
                    .build();
            saved.add(subjectGradingRepository.save(sg));
        }
        return saved;
    }

    private SubjectResponse toResponseWithGradings(Subject subject) {
        SubjectResponse response = subjectMapper.toResponse(subject);
        List<SubjectGradingWeightResponse> gradingResponses =
                subjectGradingRepository.findBySubjectId(subject.getId()).stream()
                        .map(this::toSubjectGradingWeightResponse)
                        .toList();
        response.setGradings(gradingResponses.isEmpty() ? null : gradingResponses);
        return response;
    }

    private SubjectGradingWeightResponse toSubjectGradingWeightResponse(SubjectGrading sg) {
        Grading g = sg.getGrading();
        return SubjectGradingWeightResponse.builder()
                .gradingId(g.getId())
                .gradingType(g.getGradingType() != null ? g.getGradingType().name() : null)
                .weight(sg.getWeight())
                .name(g.getName())
                .description(g.getDescription())
                .build();
    }

    private GradingType parseGradingType(String value) {
        if (value == null) throw new DomainException("Grading type must not be null");
        try {
            return GradingType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid grading type: " + value +
                    ". Allowed values: TUTORIAL, LABS, ASSIGNMENT, MIDTERM, FINAL");
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
