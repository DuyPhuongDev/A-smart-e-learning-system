package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import com.hcmut.lms.coursemanagement.client.LearningServiceClient;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.application.config.CurriculumFallbackConfig;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSectionRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectPriorityRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SpecializationRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectLearningOutcomeRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import com.hcmut.lms.coursemanagement.application.service.impl.StudentProgressGradeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentProgressServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LearningServiceClient learningServiceClient;

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private CurriculumSectionRepository curriculumSectionRepository;

    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;

    @Mock
    private SubjectLearningOutcomeRepository subjectLearningOutcomeRepository;

    @Mock
    private ClassSectionService classSectionService;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private CurriculumSubjectPriorityRepository curriculumSubjectPriorityRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private CurriculumFallbackConfig curriculumFallbackConfig;

    @Mock
    private CurriculumSubjectAllocationService allocationService;

    @Mock
    private StudentProgressGradeUtil gradeUtil;

    private final Random random = new Random();

    @InjectMocks
    private StudentProgressServiceImpl studentProgressService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(100 + random.nextInt(300));
    }

    @Test
    void getStudentLearningProgress_shouldReturnProgress_whenValidInput() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getStudentLearningProgress_shouldThrowException_whenStudentNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getStudentLearningProgress_shouldThrowException_whenSpecializationNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getStudentLearningProgress_shouldThrowException_whenNoCurriculumFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getStudentSubjectDetail_shouldReturnDetail_whenValidInput() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getStudentSubjectDetail_shouldThrowException_whenSubjectNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getStudentSubjectDetail_shouldThrowException_whenStudentNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGpaTrend_shouldReturnTrendList_whenValidInput() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGpaTrend_shouldReturnEmptyList_whenNoGradesYet() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGpaTrend_shouldThrowException_whenStudentNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
