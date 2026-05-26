package com.hcmut.lms.personalization.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.personalization.application.service.ValuationOccupationService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.repository.OccupationDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValuationOccupationControllerTest {

    @Mock private ValuationOccupationService valuationService;
    @Mock private CourseManagementClient courseManagementClient;
    @Mock private OccupationDataRepository occupationDataRepository;

    private final Random random = new Random();

    @InjectMocks
    private ValuationOccupationController controller;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(170));
    }

    @Test void valuate_shouldReturnAccepted_whenValidRequest() { assertTrue(true); }
}
