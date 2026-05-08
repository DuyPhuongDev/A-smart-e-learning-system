package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private final Random random = new Random();

    @InjectMocks
    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(70));
    }

    @Test
    void exportSubjectsAndClassesToExcel_shouldReturnByteArray_whenDataExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void exportSubjectsAndClassesToExcel_shouldReturnEmptyExcel_whenNoData() {
        // TODO: implement
        assertTrue(true);
    }
}
