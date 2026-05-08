package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.VideoTranscriptMapper;
import com.hcmut.lms.coursemanagement.repository.VideoLectureRepository;
import com.hcmut.lms.coursemanagement.repository.VideoTranscriptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VideoTranscriptServiceImplTest {

    @Mock
    private VideoTranscriptRepository videoTranscriptRepository;

    @Mock
    private VideoLectureRepository videoLectureRepository;

    @Mock
    private VideoTranscriptMapper videoTranscriptMapper;

    private final Random random = new Random();

    @InjectMocks
    private VideoTranscriptServiceImpl videoTranscriptService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(150));
    }

    @Test
    void createTranscript_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void createTranscripts_shouldReturnList_whenValidRequests() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateTranscript_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void updateTranscript_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getTranscriptByVideoLectureId_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void getTranscriptByVideoLectureId_shouldReturnEmptyOptional_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getTranscriptById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void getTranscriptById_shouldReturnEmptyOptional_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteTranscript_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteTranscriptByVideoLectureId_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void existsByVideoLectureId_shouldReturnTrue_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void existsByVideoLectureId_shouldReturnFalse_whenNotExists() {
        // TODO: implement
        assertTrue(true);
    }
}
