package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coursemanagement.repository.DocumentLectureRepository;
import com.hcmut.lms.coursemanagement.repository.LectureRepository;
import com.hcmut.lms.coursemanagement.repository.TextLectureRepository;
import com.hcmut.lms.coursemanagement.repository.VideoLectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;

@ExtendWith(MockitoExtension.class)
class LectureEnrichmentQueueServiceImplTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private VideoLectureRepository videoLectureRepository;

    @Mock
    private DocumentLectureRepository documentLectureRepository;

    @Mock
    private TextLectureRepository textLectureRepository;

    @Mock
    private ObjectMapper objectMapper;

    private final Random random = new Random();

    @InjectMocks
    private LectureEnrichmentQueueServiceImpl lectureEnrichmentQueueService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(100 + random.nextInt(300));
    }

    @Test
    void queueEnrichmentJobs_shouldQueueVideoLectures_whenVideoType() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void queueEnrichmentJobs_shouldQueueDocumentLectures_whenDocumentType() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void queueEnrichmentJobs_shouldSkipTextLectures() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void queueEnrichmentJobs_shouldReportFailedJobs_whenSqsFails() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void queueEnrichmentJobs_shouldHandleMixedLectureTypes() {
        // TODO: implement
        assertTrue(true);
    }
}
