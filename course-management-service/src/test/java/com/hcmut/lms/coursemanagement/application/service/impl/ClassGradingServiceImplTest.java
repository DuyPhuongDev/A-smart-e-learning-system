package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.ClassGradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.UpdateGradingWeightRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingWeightResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassGradingServiceImplTest {

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private GradingRepository gradingRepository;

    @Mock
    private ClassSectionGradingRepository classSectionGradingRepository;

    @Mock
    private SubjectGradingRepository subjectGradingRepository;

    @Mock
    private AssessmentServiceClient assessmentServiceClient;

    @InjectMocks
    private ClassGradingServiceImpl classGradingService;

    private static final UUID CLASS_ID = UUID.randomUUID();
    private static final UUID GRADING_ID = UUID.randomUUID();
    private static final UUID SUBJECT_ID = UUID.randomUUID();

    // --- Helpers ---

    private ClassSection buildTeacherClass() {
        ClassSection cs = new ClassSection();
        cs.setId(CLASS_ID);
        cs.setCode("TC101");
        cs.setIsOfficial(true);
        return cs;
    }

    private ClassSection buildSchoolClass() {
        ClassSection cs = new ClassSection();
        cs.setId(CLASS_ID);
        cs.setCode("SC101");
        cs.setIsOfficial(false);
        Subject subject = new Subject();
        subject.setId(SUBJECT_ID);
        subject.setName("Calculus");
        cs.setSubject(subject);
        return cs;
    }

    private Grading buildGrading(GradingType type) {
        Grading g = new Grading();
        g.setId(GRADING_ID);
        g.setName(type.name());
        g.setGradingType(type);
        return g;
    }

    private ClassSectionGrading buildCsg(ClassSection cs, Grading g, float weight) {
        ClassSectionGrading csg = new ClassSectionGrading();
        csg.setId(new ClassSectionGradingId(cs.getId(), g.getId()));
        csg.setClassSection(cs);
        csg.setGrading(g);
        csg.setWeight(weight);
        return csg;
    }

    private SubjectGrading buildSubjectGrading(Subject s, Grading g, float weight) {
        SubjectGrading sg = new SubjectGrading();
        sg.setId(new SubjectGradingId(s.getId(), g.getId()));
        sg.setSubject(s);
        sg.setGrading(g);
        sg.setWeight(weight);
        return sg;
    }

    // ========================================================================
    // getGradingsForClass
    // ========================================================================

    @Test
    void getGradingsForClass_shouldReturnTeacherClassGradings_whenOfficialClass() {
        ClassSection teacherClass = buildTeacherClass();
        Grading grading = buildGrading(GradingType.ASSIGNMENT);
        ClassSectionGrading csg = buildCsg(teacherClass, grading, 20.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.findByClassSectionId(CLASS_ID)).thenReturn(List.of(csg));
        when(assessmentServiceClient.getGradesByClass(CLASS_ID)).thenReturn(List.of());

        List<ClassGradingResponse> result = classGradingService.getGradingsForClass(CLASS_ID);

        assertEquals(1, result.size());
        assertEquals("ASSIGNMENT", result.get(0).getGradingType());
        assertEquals(20.0f, result.get(0).getWeight());
    }

    @Test
    void getGradingsForClass_shouldReturnSchoolClassGradings_whenNonOfficialClass() {
        ClassSection schoolClass = buildSchoolClass();
        Grading grading = buildGrading(GradingType.MIDTERM);
        SubjectGrading sg = buildSubjectGrading(schoolClass.getSubject(), grading, 25.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(schoolClass));
        when(subjectGradingRepository.findBySubjectId(SUBJECT_ID)).thenReturn(List.of(sg));
        when(assessmentServiceClient.getGradesByClass(CLASS_ID)).thenReturn(List.of());

        List<ClassGradingResponse> result = classGradingService.getGradingsForClass(CLASS_ID);

        assertEquals(1, result.size());
        assertEquals("MIDTERM", result.get(0).getGradingType());
        assertEquals(25.0f, result.get(0).getWeight());
    }

    @Test
    void getGradingsForClass_shouldIncludeAssessmentGrades_whenTeacherClass() {
        ClassSection teacherClass = buildTeacherClass();
        Grading grading = buildGrading(GradingType.FINAL);
        ClassSectionGrading csg = buildCsg(teacherClass, grading, 40.0f);

        AssessmentGrade grade = AssessmentGrade.builder()
                .assessmentId(UUID.randomUUID())
                .title("Final Exam")
                .assessmentType(GradingType.FINAL)
                .build();

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.findByClassSectionId(CLASS_ID)).thenReturn(List.of(csg));
        when(assessmentServiceClient.getGradesByClass(CLASS_ID)).thenReturn(List.of(grade));

        List<ClassGradingResponse> result = classGradingService.getGradingsForClass(CLASS_ID);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getAssessmentGrade());
        assertEquals(1, result.get(0).getAssessmentGrade().size());
    }

    @Test
    void getGradingsForClass_shouldThrowException_whenClassNotFound() {
        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> classGradingService.getGradingsForClass(CLASS_ID));
    }

    // ========================================================================
    // getGradingWeightsForClass
    // ========================================================================

    @Test
    void getGradingWeightsForClass_shouldReturnWeights_whenTeacherClass() {
        ClassSection teacherClass = buildTeacherClass();
        Grading grading = buildGrading(GradingType.TUTORIAL);
        ClassSectionGrading csg = buildCsg(teacherClass, grading, 10.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.findByClassSectionId(CLASS_ID)).thenReturn(List.of(csg));

        List<ClassGradingWeightResponse> result = classGradingService.getGradingWeightsForClass(CLASS_ID);

        assertEquals(1, result.size());
        assertEquals("TUTORIAL", result.get(0).getGradingType());
        assertEquals(10.0f, result.get(0).getWeight());
    }

    @Test
    void getGradingWeightsForClass_shouldReturnSubjectWeights_whenSchoolClass() {
        ClassSection schoolClass = buildSchoolClass();
        Grading grading = buildGrading(GradingType.LABS);
        SubjectGrading sg = buildSubjectGrading(schoolClass.getSubject(), grading, 15.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(schoolClass));
        when(subjectGradingRepository.findBySubjectId(SUBJECT_ID)).thenReturn(List.of(sg));

        List<ClassGradingWeightResponse> result = classGradingService.getGradingWeightsForClass(CLASS_ID);

        assertEquals(1, result.size());
        assertEquals("LABS", result.get(0).getGradingType());
    }

    // ========================================================================
    // addGradingToClass
    // ========================================================================

    @Test
    void addGradingToClass_shouldReturnResponse_whenValidRequest() {
        ClassSection teacherClass = buildTeacherClass();
        ClassGradingRequest request = new ClassGradingRequest();
        request.setName("Project");
        request.setDescription("Final project");
        request.setGradingType("ASSIGNMENT");
        request.setWeight(30.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.sumWeightByClassSectionId(CLASS_ID)).thenReturn(60.0f);

        Grading savedGrading = buildGrading(GradingType.ASSIGNMENT);
        savedGrading.setName("Project");
        when(gradingRepository.save(any(Grading.class))).thenReturn(savedGrading);
        when(classSectionGradingRepository.save(any(ClassSectionGrading.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ClassGradingResponse result = classGradingService.addGradingToClass(CLASS_ID, request);

        assertNotNull(result);
        assertEquals("Project", result.getName());
        verify(gradingRepository).save(any(Grading.class));
        verify(classSectionGradingRepository).save(any(ClassSectionGrading.class));
    }

    @Test
    void addGradingToClass_shouldThrowException_whenSchoolClass() {
        ClassSection schoolClass = buildSchoolClass();
        ClassGradingRequest request = new ClassGradingRequest();
        request.setName("Test");
        request.setWeight(10.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(schoolClass));

        assertThrows(DomainException.class,
                () -> classGradingService.addGradingToClass(CLASS_ID, request));
    }

    @Test
    void addGradingToClass_shouldThrowException_whenWeightExceeds100() {
        ClassSection teacherClass = buildTeacherClass();
        ClassGradingRequest request = new ClassGradingRequest();
        request.setName("Too Heavy");
        request.setWeight(50.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.sumWeightByClassSectionId(CLASS_ID)).thenReturn(95.0f);  // 95 + 50 > 100

        assertThrows(DomainException.class,
                () -> classGradingService.addGradingToClass(CLASS_ID, request));
    }

    // ========================================================================
    // updateGradingWeight
    // ========================================================================

    @Test
    void updateGradingWeight_shouldReturnUpdatedResponse_whenValid() {
        ClassSection teacherClass = buildTeacherClass();
        Grading grading = buildGrading(GradingType.MIDTERM);
        ClassSectionGrading csg = buildCsg(teacherClass, grading, 20.0f);

        UpdateGradingWeightRequest request = new UpdateGradingWeightRequest();
        request.setWeight(25.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.findByClassSectionIdAndGradingId(CLASS_ID, GRADING_ID))
                .thenReturn(Optional.of(csg));
        when(classSectionGradingRepository.sumWeightByClassSectionId(CLASS_ID)).thenReturn(80.0f);
        // 80 - 20 + 25 = 85 <= 100, valid
        when(classSectionGradingRepository.save(any(ClassSectionGrading.class))).thenReturn(csg);

        ClassGradingResponse result = classGradingService.updateGradingWeight(CLASS_ID, GRADING_ID, request);

        assertNotNull(result);
        verify(classSectionGradingRepository).save(any(ClassSectionGrading.class));
    }

    @Test
    void updateGradingWeight_shouldThrowException_whenSchoolClass() {
        ClassSection schoolClass = buildSchoolClass();
        UpdateGradingWeightRequest request = new UpdateGradingWeightRequest();
        request.setWeight(30.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(schoolClass));

        assertThrows(DomainException.class,
                () -> classGradingService.updateGradingWeight(CLASS_ID, GRADING_ID, request));
    }

    @Test
    void updateGradingWeight_shouldThrowException_whenNewWeightExceeds100() {
        ClassSection teacherClass = buildTeacherClass();
        Grading grading = buildGrading(GradingType.FINAL);
        ClassSectionGrading csg = buildCsg(teacherClass, grading, 40.0f);

        UpdateGradingWeightRequest request = new UpdateGradingWeightRequest();
        request.setWeight(60.0f);  // new total: 90 - 40 + 60 = 110 > 100

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.findByClassSectionIdAndGradingId(CLASS_ID, GRADING_ID))
                .thenReturn(Optional.of(csg));
        when(classSectionGradingRepository.sumWeightByClassSectionId(CLASS_ID)).thenReturn(90.0f);

        assertThrows(DomainException.class,
                () -> classGradingService.updateGradingWeight(CLASS_ID, GRADING_ID, request));
    }

    @Test
    void updateGradingWeight_shouldThrowException_whenGradingNotFound() {
        ClassSection teacherClass = buildTeacherClass();
        UpdateGradingWeightRequest request = new UpdateGradingWeightRequest();
        request.setWeight(30.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.findByClassSectionIdAndGradingId(CLASS_ID, GRADING_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> classGradingService.updateGradingWeight(CLASS_ID, GRADING_ID, request));
    }

    // ========================================================================
    // removeGradingFromClass
    // ========================================================================

    @Test
    void removeGradingFromClass_shouldRemove_whenExists() {
        ClassSection teacherClass = buildTeacherClass();
        Grading grading = buildGrading(GradingType.TUTORIAL);
        ClassSectionGrading csg = buildCsg(teacherClass, grading, 10.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.findByClassSectionIdAndGradingId(CLASS_ID, GRADING_ID))
                .thenReturn(Optional.of(csg));
        doNothing().when(classSectionGradingRepository).delete(any(ClassSectionGrading.class));

        assertDoesNotThrow(() -> classGradingService.removeGradingFromClass(CLASS_ID, GRADING_ID));
        verify(classSectionGradingRepository).delete(any(ClassSectionGrading.class));
    }

    @Test
    void removeGradingFromClass_shouldThrowException_whenSchoolClass() {
        ClassSection schoolClass = buildSchoolClass();

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(schoolClass));

        assertThrows(DomainException.class,
                () -> classGradingService.removeGradingFromClass(CLASS_ID, GRADING_ID));
    }

    // ========================================================================
    // initDefaultGradings
    // ========================================================================

    @Test
    void initDefaultGradings_shouldCreateDefaults_whenValidClass() {
        ClassSection teacherClass = buildTeacherClass();

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(gradingRepository.save(any(Grading.class))).thenAnswer(inv -> inv.getArgument(0));
        when(classSectionGradingRepository.save(any(ClassSectionGrading.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> classGradingService.initDefaultGradings(CLASS_ID));

        // 5 default gradings created
        verify(gradingRepository, times(5)).save(any(Grading.class));
        verify(classSectionGradingRepository, times(5)).save(any(ClassSectionGrading.class));
    }

    // ========================================================================
    // parseGradingType (tested via addGradingToClass)
    // ========================================================================

    @Test
    void addGradingToClass_shouldThrowException_whenInvalidGradingType() {
        ClassSection teacherClass = buildTeacherClass();
        ClassGradingRequest request = new ClassGradingRequest();
        request.setName("Invalid Type Test");
        request.setGradingType("INVALID_TYPE");
        request.setWeight(10.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.sumWeightByClassSectionId(CLASS_ID)).thenReturn(0.0f);

        assertThrows(DomainException.class,
                () -> classGradingService.addGradingToClass(CLASS_ID, request));
    }

    @Test
    void addGradingToClass_shouldAcceptNullGradingType() {
        ClassSection teacherClass = buildTeacherClass();
        ClassGradingRequest request = new ClassGradingRequest();
        request.setName("No Type");
        request.setGradingType(null);
        request.setWeight(10.0f);

        when(classSectionRepository.findById(CLASS_ID)).thenReturn(Optional.of(teacherClass));
        when(classSectionGradingRepository.sumWeightByClassSectionId(CLASS_ID)).thenReturn(0.0f);

        Grading savedGrading = new Grading();
        savedGrading.setId(GRADING_ID);
        savedGrading.setName("No Type");
        savedGrading.setGradingType(null);
        when(gradingRepository.save(any(Grading.class))).thenReturn(savedGrading);
        when(classSectionGradingRepository.save(any(ClassSectionGrading.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ClassGradingResponse result = classGradingService.addGradingToClass(CLASS_ID, request);

        assertNotNull(result);
        assertNull(result.getGradingType());
    }
}
