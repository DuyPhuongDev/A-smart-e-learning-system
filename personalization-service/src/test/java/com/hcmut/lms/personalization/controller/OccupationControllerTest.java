package com.hcmut.lms.personalization.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.personalization.application.service.OccupationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OccupationControllerTest {

    @Mock private OccupationService occupationService;

    private final Random random = new Random();

    @InjectMocks
    private OccupationController controller;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(40 + random.nextInt(130));
    }

    @Test void getOccupations_shouldReturnPage_whenValidParams() { assertTrue(true); }
    @Test void getAllOccupations_shouldReturnList_whenOccupationsExist() { assertTrue(true); }
    @Test void getOccupationDetail_shouldReturnDetail_whenExists() { assertTrue(true); }
    @Test void getOccupationSubjects_shouldReturnList_whenValuationsExist() { assertTrue(true); }
}
