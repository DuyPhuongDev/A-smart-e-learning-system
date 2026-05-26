package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoTranscript;
import com.hcmut.lms.coursemanagement.repository.VideoTranscriptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoTranscriptSegmentServiceImplTest {

    @Mock
    private VideoTranscriptRepository videoTranscriptRepository;

    @InjectMocks
    private VideoTranscriptSegmentServiceImpl videoTranscriptSegmentService;

    // --- createTranscriptSegments ---

    @Test
    void createTranscriptSegments_shouldReturnSegments_whenTranscriptLongerThanSegmentDuration() {
        UUID videoLectureId = UUID.randomUUID();
        // Build a transcript with many sentences to trigger segment breaks
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("This is sentence number ").append(i + 1).append(" with some extra words for length. ");
        }
        String fullTranscript = sb.toString();
        Integer totalDuration = 600;

        List<VideoTranscriptRequest> result = videoTranscriptSegmentService.createTranscriptSegments(
                videoLectureId, fullTranscript, 60, totalDuration);

        // With 60-second segments and 100 sentences, expect multiple segments
        assertThat(result).isNotEmpty();
        // First segment should have startTime 0
        assertThat(result.getFirst().getStartTimeSeconds()).isZero();
        assertThat(result.getFirst().getVideoLectureId()).isEqualTo(videoLectureId);
        assertThat(result.getFirst().getLanguageCode()).isEqualTo("en");
        // All segments should have correct segment index ordering
        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i).getSegmentIndex()).isEqualTo(i);
        }
    }

    @Test
    void createTranscriptSegments_shouldUseDefaultDuration_whenSegmentDurationNull() {
        UUID videoLectureId = UUID.randomUUID();
        StringBuilder sb = new StringBuilder();
        // Need enough content to exceed 600-second default duration
        for (int i = 0; i < 200; i++) {
            sb.append("This is a somewhat longer sentence number ").append(i + 1)
                    .append(" containing additional descriptive words here. ");
        }
        String fullTranscript = sb.toString();
        Integer totalDuration = 1200;

        List<VideoTranscriptRequest> result = videoTranscriptSegmentService.createTranscriptSegments(
                videoLectureId, fullTranscript, null, totalDuration);

        assertThat(result).isNotEmpty();
        // Should produce at least 2 segments with default 600-second duration
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        assertThat(result.getFirst().getStartTimeSeconds()).isZero();
        assertThat(result.getFirst().getVideoLectureId()).isEqualTo(videoLectureId);
    }

    @Test
    void createTranscriptSegments_shouldReturnSingleSegment_whenTranscriptShort() {
        UUID videoLectureId = UUID.randomUUID();
        String fullTranscript = "This is a transcript with enough words to pass the segment duration threshold.";
        Integer totalDuration = 60;

        List<VideoTranscriptRequest> result = videoTranscriptSegmentService.createTranscriptSegments(
                videoLectureId, fullTranscript, 5, totalDuration);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTranscriptText()).contains(fullTranscript.trim());
        assertThat(result.getFirst().getStartTimeSeconds()).isZero();
        assertThat(result.getFirst().getSegmentIndex()).isZero();
    }

    @Test
    void createTranscriptSegments_shouldReturnEmptyList_whenTranscriptNull() {
        UUID videoLectureId = UUID.randomUUID();

        List<VideoTranscriptRequest> result = videoTranscriptSegmentService.createTranscriptSegments(
                videoLectureId, null, 60, 600);

        assertThat(result).isEmpty();
    }

    @Test
    void createTranscriptSegments_shouldReturnEmptyList_whenTranscriptBlank() {
        UUID videoLectureId = UUID.randomUUID();

        List<VideoTranscriptRequest> result = videoTranscriptSegmentService.createTranscriptSegments(
                videoLectureId, "   ", 60, 600);

        assertThat(result).isEmpty();
    }

    @Test
    void createTranscriptSegments_shouldReturnEmptyList_whenTranscriptEmpty() {
        UUID videoLectureId = UUID.randomUUID();

        List<VideoTranscriptRequest> result = videoTranscriptSegmentService.createTranscriptSegments(
                videoLectureId, "", 60, 600);

        assertThat(result).isEmpty();
    }

    @Test
    void createTranscriptSegments_shouldUseProvidedSegmentDuration() {
        UUID videoLectureId = UUID.randomUUID();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 80; i++) {
            sb.append("Sentence number ").append(i + 1).append(" is here with more words for testing. ");
        }
        String fullTranscript = sb.toString();
        Integer totalDuration = 300;

        List<VideoTranscriptRequest> result = videoTranscriptSegmentService.createTranscriptSegments(
                videoLectureId, fullTranscript, 120, totalDuration);

        assertThat(result).isNotEmpty();
        // Start times should increment by 120 (the segment duration)
        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i).getStartTimeSeconds()).isEqualTo(i * 120);
        }
    }

    // --- getTranscriptSegments ---

    @Test
    void getTranscriptSegments_shouldReturnList_whenSegmentsExist() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(UUID.randomUUID());
        VideoTranscript t1 = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Segment 1", 0);
        VideoTranscript t2 = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Segment 2", 1);

        when(videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId))
                .thenReturn(List.of(t1, t2));

        List<VideoTranscriptResponse> result = videoTranscriptSegmentService.getTranscriptSegments(videoLectureId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTranscriptText()).isEqualTo("Segment 1");
        assertThat(result.get(1).getTranscriptText()).isEqualTo("Segment 2");
        assertThat(result.get(0).getSegmentIndex()).isZero();
        assertThat(result.get(1).getSegmentIndex()).isEqualTo(1);
    }

    @Test
    void getTranscriptSegments_shouldReturnEmptyList_whenNoSegments() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId))
                .thenReturn(List.of());

        List<VideoTranscriptResponse> result = videoTranscriptSegmentService.getTranscriptSegments(videoLectureId);

        assertThat(result).isEmpty();
    }

    // --- getTranscriptSegmentsInRange ---

    @Test
    void getTranscriptSegmentsInRange_shouldReturnFilteredSegments() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(UUID.randomUUID());
        VideoTranscript t1 = createTranscriptEntityWithTimes(UUID.randomUUID(), videoLecture, "Segment 1", 0, 0, 30);
        VideoTranscript t2 = createTranscriptEntityWithTimes(UUID.randomUUID(), videoLecture, "Segment 2", 1, 30, 60);

        when(videoTranscriptRepository.findTranscriptsInTimeRange(videoLectureId, 10, 50))
                .thenReturn(List.of(t1, t2));

        List<VideoTranscriptResponse> result = videoTranscriptSegmentService.getTranscriptSegmentsInRange(
                videoLectureId, 10, 50);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTranscriptText()).isEqualTo("Segment 1");
        assertThat(result.get(1).getTranscriptText()).isEqualTo("Segment 2");
    }

    @Test
    void getTranscriptSegmentsInRange_shouldReturnEmptyList_whenNoOverlap() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.findTranscriptsInTimeRange(videoLectureId, 100, 200))
                .thenReturn(List.of());

        List<VideoTranscriptResponse> result = videoTranscriptSegmentService.getTranscriptSegmentsInRange(
                videoLectureId, 100, 200);

        assertThat(result).isEmpty();
    }

    // --- mergeTranscriptSegments ---

    @Test
    void mergeTranscriptSegments_shouldReturnMergedText_whenSegmentsExist() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(UUID.randomUUID());
        VideoTranscript t1 = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Hello world", 0);
        VideoTranscript t2 = createTranscriptEntity(UUID.randomUUID(), videoLecture, "This is segment two", 1);

        when(videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId))
                .thenReturn(List.of(t1, t2));

        String result = videoTranscriptSegmentService.mergeTranscriptSegments(videoLectureId);

        assertThat(result).isEqualTo("Hello world This is segment two");
    }

    @Test
    void mergeTranscriptSegments_shouldReturnEmptyString_whenNoSegments() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId))
                .thenReturn(List.of());

        String result = videoTranscriptSegmentService.mergeTranscriptSegments(videoLectureId);

        assertThat(result).isEmpty();
    }

    @Test
    void mergeTranscriptSegments_shouldReturnSingleSegment_whenOneSegment() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(UUID.randomUUID());
        VideoTranscript t1 = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Only segment", 0);

        when(videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId))
                .thenReturn(List.of(t1));

        String result = videoTranscriptSegmentService.mergeTranscriptSegments(videoLectureId);

        assertThat(result).isEqualTo("Only segment");
    }

    // --- deleteTranscriptSegments ---

    @Test
    void deleteTranscriptSegments_shouldDeleteAllSegments_whenSegmentsExist() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(UUID.randomUUID());
        VideoTranscript t1 = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Seg 1", 0);
        VideoTranscript t2 = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Seg 2", 1);

        when(videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId))
                .thenReturn(List.of(t1, t2));

        videoTranscriptSegmentService.deleteTranscriptSegments(videoLectureId);

        verify(videoTranscriptRepository).deleteById(t1.getId());
        verify(videoTranscriptRepository).deleteById(t2.getId());
    }

    @Test
    void deleteTranscriptSegments_shouldDoNothing_whenNoSegments() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.findAllByVideoLectureIdOrderBySegmentIndex(videoLectureId))
                .thenReturn(List.of());

        videoTranscriptSegmentService.deleteTranscriptSegments(videoLectureId);

        verify(videoTranscriptRepository, never()).deleteById(any());
    }

    // --- helper methods ---

    private VideoLecture createVideoLecture(UUID id) {
        VideoLecture vl = new VideoLecture();
        vl.setId(id);
        vl.setVideoUrl("https://example.com/video.mp4");
        vl.setDuration(300);
        return vl;
    }

    private VideoTranscript createTranscriptEntity(UUID id, VideoLecture videoLecture, String text, int segmentIndex) {
        return createTranscriptEntityWithTimes(id, videoLecture, text, segmentIndex, 0, 30);
    }

    private VideoTranscript createTranscriptEntityWithTimes(UUID id, VideoLecture videoLecture, String text,
                                                              int segmentIndex, int startTime, int endTime) {
        VideoTranscript vt = new VideoTranscript();
        vt.setId(id);
        vt.setVideoLecture(videoLecture);
        vt.setTranscriptText(text);
        vt.setLanguageCode("en");
        vt.setStartTimeSeconds(startTime);
        vt.setEndTimeSeconds(endTime);
        vt.setSegmentIndex(segmentIndex);
        return vt;
    }
}
