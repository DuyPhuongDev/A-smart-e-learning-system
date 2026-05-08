package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.LectureMapper;
import com.hcmut.lms.coursemanagement.application.mapper.LectureMapperHelper;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactoryProvider;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategyProvider;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.LectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LectureServiceImplTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private LectureFactoryProvider lectureFactoryProvider;

    @Mock
    private LectureMapper lectureMapper;

    @Mock
    private LectureMapperHelper lectureMapperHelper;

    @Mock
    private LectureUpdateStrategyProvider strategyProvider;

    private final Random random = new Random();

    @InjectMocks
    private LectureServiceImpl lectureService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(150));
    }

    @Test
    void createLecture_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void createLecture_shouldThrowException_whenChapterNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void updateLecture_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateLecture_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getLectureById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getLectureById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getLecturesByChapterId_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteLecture_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteLecture_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void reorderLectures_shouldReturnReorderedList_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void reorderLectures_shouldThrowException_whenLectureNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
