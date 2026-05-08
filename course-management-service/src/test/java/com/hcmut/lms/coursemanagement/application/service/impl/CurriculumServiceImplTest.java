package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.CurriculumMapper;
import com.hcmut.lms.coursemanagement.application.config.CurriculumFallbackConfig;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSectionRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectPriorityRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectRepository;
import com.hcmut.lms.coursemanagement.repository.SpecializationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurriculumServiceImplTest {

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private CurriculumMapper curriculumMapper;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private CurriculumSectionRepository curriculumSectionRepository;

    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;

    @Mock
    private CurriculumSubjectPriorityRepository curriculumSubjectPriorityRepository;

    @Mock
    private CurriculumFallbackConfig curriculumFallbackConfig;

    private final Random random = new Random();

    @InjectMocks
    private CurriculumServiceImpl curriculumService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(100 + random.nextInt(300));
    }

    void createCurriculum_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    void createCurriculum_shouldThrowException_whenDuplicateCode() {
        // TODO: implement
        assertTrue(true);
    }

    void updateCurriculum_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateCurriculum_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getCurriculumById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllCurriculums_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllCurriculumsPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumsBySpecializationId_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumsBySpecializationIdPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumsByIntakeYearId_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    void getCurriculumsByIntakeYearIdPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    void resolveCurriculumBySpecializationAndIntakeYear_shouldReturnResponse_whenFound() {
        // TODO: implement
        assertTrue(true);
    }

    void resolveCurriculumBySpecializationAndIntakeYear_shouldUseFallback_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void deleteCurriculum_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void deleteCurriculum_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumFull_shouldReturnFullResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumFull_shouldReturnEmptySections_whenNoSections() {
        // TODO: implement
        assertTrue(true);
    }
}
