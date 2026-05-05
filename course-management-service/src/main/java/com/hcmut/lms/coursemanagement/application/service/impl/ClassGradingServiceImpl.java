package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.ClassGradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.UpdateGradingWeightRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingWeightResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassGradingService;
import com.hcmut.lms.coursemanagement.client.AssessmentServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.AssessmentGrade;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSectionGrading;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSectionGradingId;
import com.hcmut.lms.coursemanagement.domain.entity.subject.*;
import com.hcmut.lms.coursemanagement.exception.DomainException;
import com.hcmut.lms.coursemanagement.repository.ClassSectionGradingRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectGradingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassGradingServiceImpl implements ClassGradingService {

    private static final float DEFAULT_TUTORIAL_WEIGHT    = 10.0f;
    private static final float DEFAULT_LABS_WEIGHT        = 0.0f;
    private static final float DEFAULT_ASSIGNMENT_WEIGHT  = 30.0f;
    private static final float DEFAULT_MIDTERM_WEIGHT     = 20.0f;
    private static final float DEFAULT_FINAL_WEIGHT       = 40.0f;
    private static final float TOTAL_WEIGHT               = 100.0f;
    private static final float WEIGHT_TOLERANCE           = 0.01f;

    private static final String SCHOOL_CLASS_WRITE_BLOCKED =
            "Grading for school classes is defined at the subject level and cannot be modified.";

    private final ClassSectionRepository classSectionRepository;
    private final GradingRepository gradingRepository;
    private final ClassSectionGradingRepository classSectionGradingRepository;
    private final SubjectGradingRepository subjectGradingRepository;
    private final AssessmentServiceClient assessmentServiceClient;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public List<ClassGradingResponse> getGradingsForClass(UUID classId) {
        log.info("Getting gradings for class: {}", classId);
        ClassSection classSection = verifyClassExists(classId);

        if (isSchoolClass(classSection)) {
            return getSubjectGradingsAsResponse(classSection);
        }

        List<ClassSectionGrading> classSectionGradings = classSectionGradingRepository.findByClassSectionId(classId);

        Map<GradingType, List<AssessmentGrade>> assessmentGrades =
                assessmentServiceClient.getGradesByClass(classId).stream()
                        .collect(Collectors.groupingBy(AssessmentGrade::getAssessmentType));

        return classSectionGradings.stream()
                .map(i -> {
                    ClassGradingResponse classGradingResponse = this.toClassGradingResponse(i);
                    classGradingResponse.setAssessmentGrade(assessmentGrades.getOrDefault(i.getGrading().getGradingType(), List.of()));
                    return classGradingResponse;
                })
                .toList();
    }



    @Override
    @Transactional(readOnly = true)
    public List<ClassGradingWeightResponse> getGradingWeightsForClass(UUID classId) {
        log.info("Getting grading weights for class: {}", classId);
        ClassSection classSection = verifyClassExists(classId);

        if (isSchoolClass(classSection)) {
            return getSubjectGradingWeights(classSection);
        }
        List<ClassSectionGrading> classSectionGradings = classSectionGradingRepository.findByClassSectionId(classId);

        return classSectionGradings.stream()
                .map(i -> ClassGradingWeightResponse.builder()
                        .gradingType(String.valueOf(i.getGrading().getGradingType()))
                        .weight(i.getWeight())
                        .build())
                .toList();
    }

    // -------------------------------------------------------------------------
    // WRITE
    // -------------------------------------------------------------------------

    @Override
    public ClassGradingResponse addGradingToClass(UUID classId, ClassGradingRequest request) {
        log.info("Adding grading '{}' to class: {}", request.getName(), classId);
        ClassSection classSection = verifyClassExists(classId);

        if (isSchoolClass(classSection)) {
            throw new DomainException(SCHOOL_CLASS_WRITE_BLOCKED);
        }

        float currentTotal = classSectionGradingRepository.sumWeightByClassSectionId(classId);
        if (currentTotal + request.getWeight() > TOTAL_WEIGHT + WEIGHT_TOLERANCE) {
            throw new DomainException(String.format(
                    "Adding weight %.1f%% would exceed 100%%. Current total: %.1f%%",
                    request.getWeight(), currentTotal));
        }

        Grading grading = Grading.builder()
                .name(request.getName())
                .description(request.getDescription())
                .gradingType(parseGradingType(request.getGradingType()))
                .build();
        grading = gradingRepository.save(grading);

        ClassSectionGrading link = ClassSectionGrading.builder()
                .id(new ClassSectionGradingId(classId, grading.getId()))
                .classSection(classSection)
                .grading(grading)
                .weight(request.getWeight())
                .build();
        classSectionGradingRepository.save(link);

        log.info("Grading '{}' added to class {} with weight {}%", grading.getName(), classId, request.getWeight());
        return toClassGradingResponse(link);
    }

    @Override
    public ClassGradingResponse updateGradingWeight(UUID classId, UUID gradingId, UpdateGradingWeightRequest request) {
        log.info("Updating grading {} weight to {}% in class {}", gradingId, request.getWeight(), classId);
        ClassSection classSection = verifyClassExists(classId);

        if (isSchoolClass(classSection)) {
            throw new DomainException(SCHOOL_CLASS_WRITE_BLOCKED);
        }

        ClassSectionGrading csg = classSectionGradingRepository
                .findByClassSectionIdAndGradingId(classId, gradingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Grading " + gradingId + " not found in class " + classId));

        float currentTotal = classSectionGradingRepository.sumWeightByClassSectionId(classId);
        float newTotal = currentTotal - csg.getWeight() + request.getWeight();
        if (newTotal > TOTAL_WEIGHT + WEIGHT_TOLERANCE) {
            throw new DomainException(String.format(
                    "New total weight %.1f%% would exceed 100%%. Current total: %.1f%%",
                    newTotal, currentTotal));
        }

        csg.setWeight(request.getWeight());
        classSectionGradingRepository.save(csg);

        log.info("Grading {} weight updated to {}% in class {}", gradingId, request.getWeight(), classId);
        return toClassGradingResponse(csg);
    }

    @Override
    public void removeGradingFromClass(UUID classId, UUID gradingId) {
        log.info("Removing grading {} from class {}", gradingId, classId);
        ClassSection classSection = verifyClassExists(classId);

        if (isSchoolClass(classSection)) {
            throw new DomainException(SCHOOL_CLASS_WRITE_BLOCKED);
        }

        ClassSectionGrading csg = classSectionGradingRepository
                .findByClassSectionIdAndGradingId(classId, gradingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Grading " + gradingId + " not found in class " + classId));

        classSectionGradingRepository.delete(csg);
        log.info("Grading {} removed from class {}", gradingId, classId);
    }

    // -------------------------------------------------------------------------
    // INIT (teacher classes only)
    // -------------------------------------------------------------------------

//    private void initDefaultGradings(UUID classId) {
//        log.info("Initializing default gradings for teacher class: {}", classId);
//        ClassSection classSection = verifyClassExists(classId);
//
//        createAndLinkGrading(classSection, "Tutorial",    null, GradingType.TUTORIAL,   DEFAULT_TUTORIAL_WEIGHT);
//        createAndLinkGrading(classSection, "Labs",        null, GradingType.LABS,        DEFAULT_LABS_WEIGHT);
//        createAndLinkGrading(classSection, "Assignment",  null, GradingType.ASSIGNMENT,  DEFAULT_ASSIGNMENT_WEIGHT);
//        createAndLinkGrading(classSection, "Midterm Exam", null, GradingType.MIDTERM,   DEFAULT_MIDTERM_WEIGHT);
//        createAndLinkGrading(classSection, "Final Exam",  null, GradingType.FINAL,       DEFAULT_FINAL_WEIGHT);
//
//        //
//
//        log.info("Default gradings initialized for teacher class: {}", classId);
//    }

    // -------------------------------------------------------------------------
    // PRIVATE HELPERS
    // -------------------------------------------------------------------------

    private boolean isSchoolClass(ClassSection classSection) {
        return Boolean.FALSE.equals(classSection.getIsOfficial());
    }

    // chac chan co bug
    private List<SubjectGrading> getOrCreateDefaultSubjectGradings(Subject subject) {
        log.info("Getting default subject gradings for subject {}", subject.getId());
        List<SubjectGrading> exists = subjectGradingRepository.findBySubjectId(subject.getId());
        if (!exists.isEmpty()) {
            return exists;
        }


        List<Grading> gradings = gradingRepository.findAll();

        List<SubjectGrading> defaults = gradings.stream()
                .map(grading -> {
                    SubjectGrading defaultGrading = SubjectGrading.builder()
                            .id(new SubjectGradingId(subject.getId(), grading.getId()))
                            .subject(subject)
                            .grading(grading)
                            .build();
                    switch (grading.getGradingType()) {
                        case TUTORIAL -> defaultGrading.setWeight(DEFAULT_TUTORIAL_WEIGHT);
                        case LABS ->  defaultGrading.setWeight(DEFAULT_LABS_WEIGHT);
                        case ASSIGNMENT ->  defaultGrading.setWeight(DEFAULT_ASSIGNMENT_WEIGHT);
                        case MIDTERM ->   defaultGrading.setWeight(DEFAULT_MIDTERM_WEIGHT);
                        case FINAL ->   defaultGrading.setWeight(DEFAULT_FINAL_WEIGHT);
                        case null, default -> defaultGrading.setWeight(0.0f);
                    }
                    return defaultGrading;
                }).toList();

        return  subjectGradingRepository.saveAll(defaults);
    }

    private List<ClassGradingResponse> getSubjectGradingsAsResponse(ClassSection classSection) {
        if (classSection.getSubject() == null) return List.of();

        Map<GradingType, List<AssessmentGrade>> assessmentGrades =
                assessmentServiceClient.getGradesByClass(classSection.getId()).stream()
                        .collect(Collectors.groupingBy(AssessmentGrade::getAssessmentType));

        List<SubjectGrading> subjectGradings = getOrCreateDefaultSubjectGradings(classSection.getSubject());

        return subjectGradings.stream()
                .map(sg -> {
                    ClassGradingResponse classGradingResponse = ClassGradingResponse.builder()
                            .gradingId(sg.getGrading().getId())
                            .name(sg.getGrading().getName())
                            .description(sg.getGrading().getDescription())
                            .gradingType(sg.getGrading().getGradingType() != null
                                    ? sg.getGrading().getGradingType().name() : null)
                            .weight(sg.getWeight())
                            .build();
                    classGradingResponse.setAssessmentGrade(assessmentGrades.getOrDefault(sg.getGrading().getGradingType(), List.of()));
                    return classGradingResponse;
                })
                .toList();
    }

    private List<ClassGradingWeightResponse> getSubjectGradingWeights(ClassSection classSection) {
        if (classSection.getSubject() == null) return List.of();
        return subjectGradingRepository.findBySubjectId(classSection.getSubject().getId()).stream()
                .map(sg -> ClassGradingWeightResponse.builder()
                        .gradingType(sg.getGrading().getGradingType() != null
                                ? sg.getGrading().getGradingType().name() : null)
                        .weight(sg.getWeight())
                        .build())
                .toList();
    }

    private ClassSection verifyClassExists(UUID classId) {
        return classSectionRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + classId));
    }

    private GradingType parseGradingType(String gradingType) {
        if (gradingType == null) return null;
        try {
            return GradingType.valueOf(gradingType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid grading type: " + gradingType);
        }
    }

    private ClassGradingResponse toClassGradingResponse(ClassSectionGrading csg) {
        Grading g = csg.getGrading();
        return ClassGradingResponse.builder()
                .gradingId(g.getId())
                .name(g.getName())
                .description(g.getDescription())
                .gradingType(g.getGradingType() != null ? g.getGradingType().name() : null)
                .weight(csg.getWeight())
                .build();
    }
}
