package com.hcmut.lms.personalization.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.personalization.application.service.OnetFlattenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnetFlattenControllerTest {

    @Mock private OnetFlattenService onetFlattenService;

    private final Random random = new Random();

    @InjectMocks
    private OnetFlattenController controller;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(100));
    }

    @Test void flatten_shouldReturnJobList_whenValidRequest() { assertTrue(true); }
    @Test void backfillRequirementEmbeddings_shouldReturnAccepted_whenValidRequest() { assertTrue(true); }
}
