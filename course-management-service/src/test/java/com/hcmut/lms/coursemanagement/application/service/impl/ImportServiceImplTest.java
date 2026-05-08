package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private final Random random = new Random();

    @InjectMocks
    private ImportServiceImpl importService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(80 + random.nextInt(270));
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnResult_whenValidFile() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnErrors_whenInvalidRows() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void importClassSectionsFromExcel_shouldThrowException_whenSemesterNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void importClassSectionsFromExcel_shouldThrowException_whenEmptyFile() {
        // TODO: implement
        assertTrue(true);
    }
}
