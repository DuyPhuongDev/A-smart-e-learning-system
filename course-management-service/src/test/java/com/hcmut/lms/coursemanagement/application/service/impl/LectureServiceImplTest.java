package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.VideoLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.application.mapper.LectureMapper;
import com.hcmut.lms.coursemanagement.application.mapper.LectureMapperHelper;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategy;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategyProvider;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactoryProvider;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.LectureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Mock
    private LectureUpdateStrategy strategy;

    @InjectMocks
    private LectureServiceImpl lectureService;

    private static final UUID LECTURE_ID = UUID.randomUUID();
    private static final UUID CHAPTER_ID = UUID.randomUUID();
    private static final UUID NEW_CHAPTER_ID = UUID.randomUUID();

    // --- Helpers ---

    private VideoLectureRequest buildVideoRequest() {
        VideoLectureRequest req = new VideoLectureRequest();
        req.setTitle("Test Video");
        req.setDescription("Test Description");
        req.setLectureType(LectureType.VIDEO);
        req.setChapterId(CHAPTER_ID);
        req.setVideoUrl("https://video.example.com/test.mp4");
        req.setDuration(300);
        return req;
    }

    private VideoLecture buildVideoLecture() {
        VideoLecture lecture = VideoLecture.builder()
                .videoUrl("https://video.example.com/test.mp4")
                .duration(300)
                .build();
        lecture.setId(LECTURE_ID);
        lecture.setTitle("Test Video");
        lecture.setDescription("Test Description");
        lecture.setLectureType(LectureType.VIDEO);
        lecture.setOrderIndex(0);
        lecture.setViewCount(0);
        lecture.setCompletionRate(0.0f);
        lecture.setAllowPreview(false);
        lecture.setIsDownloadable(false);
        return lecture;
    }

    private Chapter buildChapter(UUID chapterId) {
        Chapter chapter = Chapter.builder()
                .title("Test Chapter")
                .orderIndex(0)
                .build();
        chapter.setId(chapterId);
        chapter.setLectures(new ArrayList<>());
        return chapter;
    }

    private LectureResponse buildLectureResponse() {
        return LectureResponse.builder()
                .id(LECTURE_ID)
                .title("Test Video")
                .lectureType(LectureType.VIDEO)
                .videoUrl("https://video.example.com/test.mp4")
                .duration(300)
                .chapterId(CHAPTER_ID)
                .build();
    }

    // ========================================================================
    // createLecture
    // ========================================================================

    @Test
    void createLecture_shouldReturnResponse_whenValidRequest() {
        VideoLectureRequest request = buildVideoRequest();
        Chapter chapter = buildChapter(CHAPTER_ID);
        VideoLecture lecture = buildVideoLecture();
        lecture.setChapter(chapter);
        LectureResponse expectedResponse = buildLectureResponse();

        when(chapterRepository.findById(CHAPTER_ID)).thenReturn(Optional.of(chapter));
        when(lectureFactoryProvider.createLecture(request)).thenReturn(lecture);
        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);
        when(lectureMapperHelper.toResponseDTO(lecture)).thenReturn(expectedResponse);

        LectureResponse result = lectureService.createLecture(request);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        verify(lectureRepository).save(any(Lecture.class));
    }

    @Test
    void createLecture_shouldAutoAssignOrderIndex_whenNotProvided() {
        VideoLectureRequest request = buildVideoRequest();
        request.setOrderIndex(null);  // not provided
        Chapter chapter = buildChapter(CHAPTER_ID);
        VideoLecture lecture = buildVideoLecture();
        lecture.setOrderIndex(null);
        lecture.setChapter(chapter);

        // Existing lecture with orderIndex 5
        VideoLecture existingLecture = VideoLecture.builder().build();
        existingLecture.setOrderIndex(5);

        when(chapterRepository.findById(CHAPTER_ID)).thenReturn(Optional.of(chapter));
        when(lectureFactoryProvider.createLecture(request)).thenReturn(lecture);
        when(lectureRepository.findByChapterIdOrderByOrderIndex(CHAPTER_ID))
                .thenReturn(List.of(existingLecture));
        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);
        when(lectureMapperHelper.toResponseDTO(any())).thenReturn(buildLectureResponse());

        lectureService.createLecture(request);

        assertEquals(6, lecture.getOrderIndex());  // max(5) + 1
    }

    @Test
    void createLecture_shouldThrowException_whenChapterNotFound() {
        VideoLectureRequest request = buildVideoRequest();
        when(chapterRepository.findById(CHAPTER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lectureService.createLecture(request));
        verify(lectureRepository, never()).save(any());
    }

    // ========================================================================
    // getLectureById
    // ========================================================================

    @Test
    void getLectureById_shouldReturnResponse_whenExists() {
        VideoLecture lecture = buildVideoLecture();
        LectureResponse expectedResponse = buildLectureResponse();

        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.of(lecture));
        when(lectureMapperHelper.toResponseDTO(lecture)).thenReturn(expectedResponse);

        LectureResponse result = lectureService.getLectureById(LECTURE_ID);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
    }

    @Test
    void getLectureById_shouldThrowException_whenNotFound() {
        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lectureService.getLectureById(LECTURE_ID));
    }

    // ========================================================================
    // getLecturesByChapterId
    // ========================================================================

    @Test
    void getLecturesByChapterId_shouldReturnList() {
        VideoLecture lecture1 = buildVideoLecture();
        VideoLecture lecture2 = VideoLecture.builder().build();
        lecture2.setId(UUID.randomUUID());
        lecture2.setLectureType(LectureType.VIDEO);

        LectureResponse resp1 = buildLectureResponse();
        LectureResponse resp2 = LectureResponse.builder().id(lecture2.getId()).build();

        when(lectureRepository.findByChapterIdOrderByOrderIndex(CHAPTER_ID))
                .thenReturn(List.of(lecture1, lecture2));
        when(lectureMapperHelper.toResponseDTO(lecture1)).thenReturn(resp1);
        when(lectureMapperHelper.toResponseDTO(lecture2)).thenReturn(resp2);

        List<LectureResponse> result = lectureService.getLecturesByChapterId(CHAPTER_ID);

        assertEquals(2, result.size());
    }

    @Test
    void getLecturesByChapterId_shouldReturnEmptyList_whenNoLectures() {
        when(lectureRepository.findByChapterIdOrderByOrderIndex(CHAPTER_ID))
                .thenReturn(List.of());

        List<LectureResponse> result = lectureService.getLecturesByChapterId(CHAPTER_ID);

        assertTrue(result.isEmpty());
    }

    // ========================================================================
    // updateLecture
    // ========================================================================

    @Test
    void updateLecture_shouldReturnUpdatedResponse_whenValid() {
        VideoLectureRequest request = buildVideoRequest();
        VideoLecture lecture = buildVideoLecture();
        Chapter chapter = buildChapter(CHAPTER_ID);
        lecture.setChapter(chapter);
        LectureResponse expectedResponse = buildLectureResponse();

        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.of(lecture));
        when(strategyProvider.getStrategy(LectureType.VIDEO)).thenReturn(strategy);
        doNothing().when(strategy).validate(any());
        doNothing().when(strategy).updateSpecificFields(any(), any());
        doNothing().when(lectureMapper).updateEntityFromDTO(any(), any());
        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);
        when(lectureMapperHelper.toResponseDTO(lecture)).thenReturn(expectedResponse);

        LectureResponse result = lectureService.updateLecture(LECTURE_ID, request);

        assertNotNull(result);
        verify(strategy).validate(request);
        verify(strategy).updateSpecificFields(lecture, request);
    }

    @Test
    void updateLecture_shouldThrowException_whenNotFound() {
        VideoLectureRequest request = buildVideoRequest();
        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> lectureService.updateLecture(LECTURE_ID, request));
    }

    @Test
    void updateLecture_shouldThrowException_whenTypeMismatch() {
        VideoLectureRequest request = buildVideoRequest();
        request.setLectureType(LectureType.VIDEO);

        VideoLecture lecture = buildVideoLecture();
        lecture.setLectureType(LectureType.DOCUMENT);  // mismatch!

        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.of(lecture));

        assertThrows(IllegalArgumentException.class,
                () -> lectureService.updateLecture(LECTURE_ID, request));
    }

    @Test
    void updateLecture_shouldUpdateChapter_whenChapterChanged() {
        VideoLectureRequest request = buildVideoRequest();
        request.setChapterId(NEW_CHAPTER_ID);  // new chapter
        Chapter oldChapter = buildChapter(CHAPTER_ID);
        Chapter newChapter = buildChapter(NEW_CHAPTER_ID);
        VideoLecture lecture = buildVideoLecture();
        lecture.setChapter(oldChapter);

        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.of(lecture));
        when(strategyProvider.getStrategy(LectureType.VIDEO)).thenReturn(strategy);
        when(chapterRepository.findById(NEW_CHAPTER_ID)).thenReturn(Optional.of(newChapter));
        doNothing().when(strategy).validate(any());
        doNothing().when(strategy).updateSpecificFields(any(), any());
        doNothing().when(lectureMapper).updateEntityFromDTO(any(), any());
        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);
        when(lectureMapperHelper.toResponseDTO(any())).thenReturn(buildLectureResponse());

        lectureService.updateLecture(LECTURE_ID, request);

        assertEquals(newChapter, lecture.getChapter());
        verify(chapterRepository).findById(NEW_CHAPTER_ID);
    }

    // ========================================================================
    // deleteLecture
    // ========================================================================

    @Test
    void deleteLecture_shouldDelete_whenExistsWithStrategy() {
        VideoLecture lecture = buildVideoLecture();

        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.of(lecture));
        when(strategyProvider.getStrategy(LectureType.VIDEO)).thenReturn(strategy);
        doNothing().when(strategy).beforeDelete(any());
        doNothing().when(strategy).afterDelete(any());
        doNothing().when(lectureRepository).deleteById(LECTURE_ID);

        lectureService.deleteLecture(LECTURE_ID);

        verify(strategy).beforeDelete(lecture);
        verify(lectureRepository).deleteById(LECTURE_ID);
        verify(strategy).afterDelete(LECTURE_ID);
    }

    @Test
    void deleteLecture_shouldThrowException_whenNotFound() {
        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lectureService.deleteLecture(LECTURE_ID));
        verify(lectureRepository, never()).deleteById(any());
    }

    @Test
    void deleteLecture_shouldStillDelete_whenNoStrategyFound() {
        VideoLecture lecture = buildVideoLecture();

        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.of(lecture));
        when(strategyProvider.getStrategy(LectureType.VIDEO))
                .thenThrow(new IllegalArgumentException("No strategy found"));
        doNothing().when(lectureRepository).deleteById(LECTURE_ID);

        assertDoesNotThrow(() -> lectureService.deleteLecture(LECTURE_ID));
        verify(lectureRepository).deleteById(LECTURE_ID);
    }

    // ========================================================================
    // reorderLectures
    // ========================================================================

    private List<Lecture> buildLectureList(int count, Chapter chapter) {
        List<Lecture> lectures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            VideoLecture lec = VideoLecture.builder()
                    .videoUrl("https://example.com/video" + i + ".mp4")
                    .duration(100)
                    .build();
            lec.setId(UUID.randomUUID());
            lec.setTitle("Lecture " + i);
            lec.setLectureType(LectureType.VIDEO);
            lec.setOrderIndex(i);
            lec.setChapter(chapter);
            lectures.add(lec);
        }
        return lectures;
    }

    @Test
    void reorderLectures_shouldMoveDown_whenNewOrderGreaterThanOldOrder() {
        Chapter chapter = buildChapter(CHAPTER_ID);
        List<Lecture> lectures = buildLectureList(5, chapter);
        chapter.setLectures(new ArrayList<>(lectures));

        VideoLecture target = (VideoLecture) lectures.get(2);  // orderIndex=2
        ReorderRequest request = ReorderRequest.builder()
                .id(target.getId())
                .newOrderIndex(4)  // move from 2 to 4 (down)
                .build();

        when(lectureRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(lectureRepository.saveAndFlush(any(Lecture.class))).thenReturn(target);
        when(lectureRepository.saveAllAndFlush(anyList())).thenReturn(lectures);
        when(lectureMapper.toResponseDTO(any(Lecture.class)))
                .thenAnswer(inv -> {
                    Lecture l = inv.getArgument(0);
                    return LectureResponse.builder()
                            .id(l.getId())
                            .title(l.getTitle())
                            .orderIndex(l.getOrderIndex())
                            .build();
                });

        List<LectureResponse> result = lectureService.reorderLectures(request);

        assertEquals(5, result.size());
        // After move down from 2 to 4: target should be at order 4
        verify(lectureRepository, times(2)).saveAndFlush(any(Lecture.class));
        verify(lectureRepository).saveAllAndFlush(anyList());
    }

    @Test
    void reorderLectures_shouldMoveUp_whenNewOrderLessThanOldOrder() {
        Chapter chapter = buildChapter(CHAPTER_ID);
        List<Lecture> lectures = buildLectureList(5, chapter);
        chapter.setLectures(new ArrayList<>(lectures));

        VideoLecture target = (VideoLecture) lectures.get(3);  // orderIndex=3
        ReorderRequest request = ReorderRequest.builder()
                .id(target.getId())
                .newOrderIndex(1)  // move from 3 to 1 (up)
                .build();

        when(lectureRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(lectureRepository.saveAndFlush(any(Lecture.class))).thenReturn(target);
        when(lectureRepository.saveAllAndFlush(anyList())).thenReturn(lectures);
        when(lectureMapper.toResponseDTO(any(Lecture.class)))
                .thenAnswer(inv -> {
                    Lecture l = inv.getArgument(0);
                    return LectureResponse.builder()
                            .id(l.getId())
                            .title(l.getTitle())
                            .orderIndex(l.getOrderIndex())
                            .build();
                });

        List<LectureResponse> result = lectureService.reorderLectures(request);

        assertEquals(5, result.size());
        verify(lectureRepository, times(2)).saveAndFlush(any(Lecture.class));
        verify(lectureRepository).saveAllAndFlush(anyList());
    }

    @Test
    void reorderLectures_shouldReturnCurrentState_whenOrderUnchanged() {
        Chapter chapter = buildChapter(CHAPTER_ID);
        List<Lecture> lectures = buildLectureList(3, chapter);
        chapter.setLectures(new ArrayList<>(lectures));

        VideoLecture target = (VideoLecture) lectures.get(1);  // orderIndex=1
        ReorderRequest request = ReorderRequest.builder()
                .id(target.getId())
                .newOrderIndex(1)  // same as current
                .build();

        when(lectureRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(lectureMapper.toResponseDTO(any(Lecture.class)))
                .thenAnswer(inv -> {
                    Lecture l = inv.getArgument(0);
                    return LectureResponse.builder().id(l.getId()).orderIndex(l.getOrderIndex()).build();
                });

        List<LectureResponse> result = lectureService.reorderLectures(request);

        assertEquals(3, result.size());
        verify(lectureRepository, never()).saveAndFlush(any());
        verify(lectureRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    void reorderLectures_shouldThrowException_whenLectureNotFound() {
        ReorderRequest request = ReorderRequest.builder()
                .id(LECTURE_ID)
                .newOrderIndex(0)
                .build();

        when(lectureRepository.findById(LECTURE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lectureService.reorderLectures(request));
    }

    @Test
    void reorderLectures_shouldClamp_whenNewOrderExceedsSize() {
        Chapter chapter = buildChapter(CHAPTER_ID);
        List<Lecture> lectures = buildLectureList(3, chapter);
        chapter.setLectures(new ArrayList<>(lectures));

        VideoLecture target = (VideoLecture) lectures.get(0);  // orderIndex=0
        ReorderRequest request = ReorderRequest.builder()
                .id(target.getId())
                .newOrderIndex(10)  // exceeds size 3, should clamp to 2
                .build();

        when(lectureRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(lectureRepository.saveAndFlush(any(Lecture.class))).thenReturn(target);
        when(lectureRepository.saveAllAndFlush(anyList())).thenReturn(lectures);
        when(lectureMapper.toResponseDTO(any(Lecture.class)))
                .thenAnswer(inv -> {
                    Lecture l = inv.getArgument(0);
                    return LectureResponse.builder().id(l.getId()).orderIndex(l.getOrderIndex()).build();
                });

        List<LectureResponse> result = lectureService.reorderLectures(request);

        assertEquals(3, result.size());
        verify(lectureRepository, times(2)).saveAndFlush(any(Lecture.class));
    }

    @Test
    void reorderLectures_shouldClampToZero_whenNewOrderNegative() {
        Chapter chapter = buildChapter(CHAPTER_ID);
        List<Lecture> lectures = buildLectureList(3, chapter);
        chapter.setLectures(new ArrayList<>(lectures));

        VideoLecture target = (VideoLecture) lectures.get(2);  // orderIndex=2
        ReorderRequest request = ReorderRequest.builder()
                .id(target.getId())
                .newOrderIndex(-5)  // negative, should clamp to 0
                .build();

        when(lectureRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(lectureRepository.saveAndFlush(any(Lecture.class))).thenReturn(target);
        when(lectureRepository.saveAllAndFlush(anyList())).thenReturn(lectures);
        when(lectureMapper.toResponseDTO(any(Lecture.class)))
                .thenAnswer(inv -> {
                    Lecture l = inv.getArgument(0);
                    return LectureResponse.builder().id(l.getId()).orderIndex(l.getOrderIndex()).build();
                });

        List<LectureResponse> result = lectureService.reorderLectures(request);

        assertEquals(3, result.size());
        verify(lectureRepository, times(2)).saveAndFlush(any(Lecture.class));
    }
}
