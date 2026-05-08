package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.CurriculumSectionMapper;
import com.hcmut.lms.coursemanagement.repository.CurriculumRepository;
import com.hcmut.lms.coursemanagement.repository.CurriculumSectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurriculumSectionServiceImplTest {

    @Mock
    private CurriculumSectionRepository curriculumSectionRepository;

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private CurriculumSectionMapper curriculumSectionMapper;

    private final Random random = new Random();

    @InjectMocks
    private CurriculumSectionServiceImpl curriculumSectionService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(150));
    }

    @Test
    void createCurriculumSection_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void createCurriculumSection_shouldThrowException_whenCurriculumNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateCurriculumSection_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateCurriculumSection_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumSectionById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumSectionById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllCurriculumSections_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurriculumSectionsByCurriculumId_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteCurriculumSection_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteCurriculumSection_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
