package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.SubjectMapper;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectRepository;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectGradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private GradingRepository gradingRepository;

    @Mock
    private SubjectGradingRepository subjectGradingRepository;

    private final Random random = new Random();

    @InjectMocks
    private SubjectServiceImpl subjectService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(150));
    }

    @Test
    void createSubject_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    void createSubject_shouldThrowException_whenDuplicateCode() {
        // TODO: implement
        assertTrue(true);
    }

    void updateSubject_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateSubject_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getSubjectById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void getSubjectById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getSubjectByCode_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getSubjectByCode_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getAllSubjects_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    void getAllSubjectsPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    void getAllSubjectsPaginated_shouldFilterByKeyword() {
        // TODO: implement
        assertTrue(true);
    }

    void deleteSubject_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteSubject_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getGradingsForSubject_shouldReturnList_whenSubjectExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGradingsForSubject_shouldThrowException_whenSubjectNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void setGradingsForSubject_shouldSaveAndReturn_whenValidWeights() {
        // TODO: implement
        assertTrue(true);
    }

    void setGradingsForSubject_shouldThrowException_whenTotalWeightNot100() {
        // TODO: implement
        assertTrue(true);
    }

    void setGradingsForSubject_shouldThrowException_whenSubjectNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getPrerequisiteMapping_shouldReturnMappingList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllSubjectIds_shouldReturnIdList() {
        // TODO: implement
        assertTrue(true);
    }

    void searchSubjects_shouldReturnMatches_whenKeywordMatches() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void searchSubjects_shouldReturnEmptyList_whenNoMatches() {
        // TODO: implement
        assertTrue(true);
    }
}
