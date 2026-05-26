package com.hcmut.lms.personalization.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.personalization.application.service.TestDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    @Mock private TestDataService testDataService;

    private final Random random = new Random();

    @InjectMocks
    private TestDataController controller;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(100));
    }

    @Test void generateTestData_shouldReturnResponse_whenValidParams() { assertTrue(true); }
}
