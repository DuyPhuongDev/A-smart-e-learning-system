package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.client.UserManagementClient;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SpecializationRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SqlGenerationServiceImplTest {

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private UserManagementClient userManagementClient;

    private final Random random = new Random();

    @InjectMocks
    private SqlGenerationServiceImpl sqlGenerationService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(80 + random.nextInt(270));
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldReturnSql_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenMissingFields() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenSpecializationNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenAcademicYearNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenSemesterNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenSubjectNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
