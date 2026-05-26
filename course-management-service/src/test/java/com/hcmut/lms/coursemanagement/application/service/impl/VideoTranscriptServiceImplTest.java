package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;
import com.hcmut.lms.coursemanagement.application.mapper.VideoTranscriptMapper;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoTranscript;
import com.hcmut.lms.coursemanagement.repository.VideoLectureRepository;
import com.hcmut.lms.coursemanagement.repository.VideoTranscriptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoTranscriptServiceImplTest {

    @Mock
    private VideoTranscriptRepository videoTranscriptRepository;

    @Mock
    private VideoLectureRepository videoLectureRepository;

    @Mock
    private VideoTranscriptMapper videoTranscriptMapper;

    @InjectMocks
    private VideoTranscriptServiceImpl videoTranscriptService;

    // --- createTranscript ---

    @Test
    void createTranscript_shouldReturnResponse_whenValidRequest() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscriptRequest request = createRequest(videoLectureId, "Hello world", "en", 10, 5, 0, 10, 0);
        VideoTranscriptResponse expectedResponse = createResponse(UUID.randomUUID(), videoLectureId, "Hello world");

        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));
        when(videoTranscriptRepository.existsByVideoLectureId(videoLectureId)).thenReturn(false);
        when(videoTranscriptRepository.save(any(VideoTranscript.class))).thenAnswer(inv -> inv.getArgument(0));
        when(videoTranscriptMapper.toResponse(any(VideoTranscript.class))).thenReturn(expectedResponse);

        VideoTranscriptResponse result = videoTranscriptService.createTranscript(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(videoTranscriptRepository).save(any(VideoTranscript.class));
    }

    @Test
    void createTranscript_shouldUseDefaultLanguageCode_whenLanguageCodeNull() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscriptRequest request = createRequest(videoLectureId, "Hello", null, null, null, null, null, null);
        VideoTranscriptResponse expectedResponse = createResponse(UUID.randomUUID(), videoLectureId, "Hello");

        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));
        when(videoTranscriptRepository.existsByVideoLectureId(videoLectureId)).thenReturn(false);
        when(videoTranscriptRepository.save(any(VideoTranscript.class))).thenAnswer(inv -> inv.getArgument(0));
        when(videoTranscriptMapper.toResponse(any(VideoTranscript.class))).thenReturn(expectedResponse);

        VideoTranscriptResponse result = videoTranscriptService.createTranscript(request);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void createTranscript_shouldThrowException_whenVideoLectureNotFound() {
        UUID videoLectureId = UUID.randomUUID();
        VideoTranscriptRequest request = createRequest(videoLectureId, "Hello", "en", null, null, null, null, null);

        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoTranscriptService.createTranscript(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Video lecture not found");
        verify(videoTranscriptRepository, never()).save(any());
    }

    @Test
    void createTranscript_shouldThrowException_whenTranscriptAlreadyExists() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscriptRequest request = createRequest(videoLectureId, "Hello", "en", null, null, null, null, null);

        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));
        when(videoTranscriptRepository.existsByVideoLectureId(videoLectureId)).thenReturn(true);

        assertThatThrownBy(() -> videoTranscriptService.createTranscript(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transcript already exists");
        verify(videoTranscriptRepository, never()).save(any());
    }

    // --- createTranscripts ---

    @Test
    void createTranscripts_shouldReturnList_whenValidRequests() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscriptRequest req1 = createRequest(videoLectureId, "Segment 1", "en", 30, 10, 0, 30, 0);
        VideoTranscriptRequest req2 = createRequest(videoLectureId, "Segment 2", "en", 30, 10, 30, 60, 1);
        List<VideoTranscriptRequest> requests = List.of(req1, req2);

        VideoTranscriptResponse resp1 = createResponse(UUID.randomUUID(), videoLectureId, "Segment 1");
        VideoTranscriptResponse resp2 = createResponse(UUID.randomUUID(), videoLectureId, "Segment 2");

        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));
        when(videoTranscriptRepository.existsByVideoLectureId(videoLectureId)).thenReturn(false);
        when(videoTranscriptRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        when(videoTranscriptMapper.toResponse(any(VideoTranscript.class)))
                .thenReturn(resp1)
                .thenReturn(resp2);

        List<VideoTranscriptResponse> result = videoTranscriptService.createTranscripts(requests);

        assertThat(result).hasSize(2).containsExactly(resp1, resp2);
        verify(videoTranscriptRepository).saveAll(anyList());
    }

    @Test
    void createTranscripts_shouldDeleteExisting_whenTranscriptsAlreadyExist() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscriptRequest req = createRequest(videoLectureId, "New segment", "en", 30, 10, 0, 30, 0);
        List<VideoTranscriptRequest> requests = List.of(req);

        VideoTranscriptResponse resp = createResponse(UUID.randomUUID(), videoLectureId, "New segment");

        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));
        when(videoTranscriptRepository.existsByVideoLectureId(videoLectureId)).thenReturn(true);
        when(videoTranscriptRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        when(videoTranscriptMapper.toResponse(any(VideoTranscript.class))).thenReturn(resp);

        List<VideoTranscriptResponse> result = videoTranscriptService.createTranscripts(requests);

        assertThat(result).hasSize(1);
        verify(videoTranscriptRepository).deleteByVideoLectureId(videoLectureId);
        verify(videoTranscriptRepository).saveAll(anyList());
    }

    @Test
    void createTranscripts_shouldReturnEmptyList_whenNullInput() {
        List<VideoTranscriptResponse> result = videoTranscriptService.createTranscripts(null);
        assertThat(result).isEmpty();
        verify(videoTranscriptRepository, never()).saveAll(any());
    }

    @Test
    void createTranscripts_shouldReturnEmptyList_whenEmptyInput() {
        List<VideoTranscriptResponse> result = videoTranscriptService.createTranscripts(List.of());
        assertThat(result).isEmpty();
        verify(videoTranscriptRepository, never()).saveAll(any());
    }

    @Test
    void createTranscripts_shouldThrowException_whenVideoLectureNotFound() {
        UUID videoLectureId = UUID.randomUUID();
        VideoTranscriptRequest req = createRequest(videoLectureId, "Segment", "en", null, null, null, null, null);
        List<VideoTranscriptRequest> requests = List.of(req);

        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoTranscriptService.createTranscripts(requests))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Video lecture not found");
    }

    // --- updateTranscript ---

    @Test
    void updateTranscript_shouldReturnUpdatedResponse_whenAllFieldsSet() {
        UUID id = UUID.randomUUID();
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscript existing = createTranscriptEntity(id, videoLecture, "Old text", "en", 10, 5, 0, 10, 0);
        VideoTranscriptRequest request = createRequest(videoLectureId, "New text", "vi", 20, 10, 5, 25, 1);
        VideoTranscriptResponse expectedResponse = createResponse(id, videoLectureId, "New text");

        when(videoTranscriptRepository.findById(id)).thenReturn(Optional.of(existing));
        when(videoTranscriptRepository.save(existing)).thenReturn(existing);
        when(videoTranscriptMapper.toResponse(existing)).thenReturn(expectedResponse);

        VideoTranscriptResponse result = videoTranscriptService.updateTranscript(id, request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(existing.getTranscriptText()).isEqualTo("New text");
        assertThat(existing.getLanguageCode()).isEqualTo("vi");
        assertThat(existing.getAudioDuration()).isEqualTo(20);
        assertThat(existing.getWordCount()).isEqualTo(10);
        assertThat(existing.getStartTimeSeconds()).isEqualTo(5);
        assertThat(existing.getEndTimeSeconds()).isEqualTo(25);
        assertThat(existing.getSegmentIndex()).isEqualTo(1);
    }

    @Test
    void updateTranscript_shouldOnlyUpdateProvidedFields_whenPartialRequest() {
        UUID id = UUID.randomUUID();
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscript existing = createTranscriptEntity(id, videoLecture, "Old text", "en", 10, 5, 0, 10, 0);
        // Only update transcriptText, leave others null
        VideoTranscriptRequest request = VideoTranscriptRequest.builder()
                .videoLectureId(videoLectureId)
                .transcriptText("Only text updated")
                .languageCode(null)
                .audioDuration(null)
                .wordCount(null)
                .startTimeSeconds(null)
                .endTimeSeconds(null)
                .segmentIndex(null)
                .build();
        VideoTranscriptResponse expectedResponse = createResponse(id, videoLectureId, "Only text updated");

        when(videoTranscriptRepository.findById(id)).thenReturn(Optional.of(existing));
        when(videoTranscriptRepository.save(existing)).thenReturn(existing);
        when(videoTranscriptMapper.toResponse(existing)).thenReturn(expectedResponse);

        VideoTranscriptResponse result = videoTranscriptService.updateTranscript(id, request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(existing.getTranscriptText()).isEqualTo("Only text updated");
        assertThat(existing.getLanguageCode()).isEqualTo("en"); // unchanged
        assertThat(existing.getAudioDuration()).isEqualTo(10); // unchanged
    }

    @Test
    void updateTranscript_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        VideoTranscriptRequest request = createRequest(UUID.randomUUID(), "Text", "en", null, null, null, null, null);

        when(videoTranscriptRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoTranscriptService.updateTranscript(id, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transcript not found");
    }

    // --- getTranscriptByVideoLectureId ---

    @Test
    void getTranscriptByVideoLectureId_shouldReturnResponse_whenExists() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscript entity = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Text", "en", null, null, 0, 10, 0);
        VideoTranscriptResponse expectedResponse = createResponse(entity.getId(), videoLectureId, "Text");

        when(videoTranscriptRepository.findByVideoLectureId(videoLectureId)).thenReturn(Optional.of(entity));
        when(videoTranscriptMapper.toResponse(entity)).thenReturn(expectedResponse);

        Optional<VideoTranscriptResponse> result = videoTranscriptService.getTranscriptByVideoLectureId(videoLectureId);

        assertThat(result).isPresent().contains(expectedResponse);
    }

    @Test
    void getTranscriptByVideoLectureId_shouldReturnEmptyOptional_whenNotFound() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.findByVideoLectureId(videoLectureId)).thenReturn(Optional.empty());

        Optional<VideoTranscriptResponse> result = videoTranscriptService.getTranscriptByVideoLectureId(videoLectureId);

        assertThat(result).isEmpty();
    }

    // --- getTranscriptById ---

    @Test
    void getTranscriptById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscript entity = createTranscriptEntity(id, videoLecture, "Text", "en", null, null, 0, 10, 0);
        VideoTranscriptResponse expectedResponse = createResponse(id, videoLectureId, "Text");

        when(videoTranscriptRepository.findById(id)).thenReturn(Optional.of(entity));
        when(videoTranscriptMapper.toResponse(entity)).thenReturn(expectedResponse);

        Optional<VideoTranscriptResponse> result = videoTranscriptService.getTranscriptById(id);

        assertThat(result).isPresent().contains(expectedResponse);
    }

    @Test
    void getTranscriptById_shouldReturnEmptyOptional_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(videoTranscriptRepository.findById(id)).thenReturn(Optional.empty());

        Optional<VideoTranscriptResponse> result = videoTranscriptService.getTranscriptById(id);

        assertThat(result).isEmpty();
    }

    // --- deleteTranscript ---

    @Test
    void deleteTranscript_shouldDelete() {
        UUID id = UUID.randomUUID();
        videoTranscriptService.deleteTranscript(id);
        verify(videoTranscriptRepository).deleteById(id);
    }

    // --- deleteTranscriptByVideoLectureId ---

    @Test
    void deleteTranscriptByVideoLectureId_shouldDelete_whenExists() {
        UUID videoLectureId = UUID.randomUUID();
        VideoLecture videoLecture = createVideoLecture(videoLectureId);
        VideoTranscript entity = createTranscriptEntity(UUID.randomUUID(), videoLecture, "Text", "en", null, null, 0, 10, 0);

        when(videoTranscriptRepository.findByVideoLectureId(videoLectureId)).thenReturn(Optional.of(entity));

        videoTranscriptService.deleteTranscriptByVideoLectureId(videoLectureId);

        verify(videoTranscriptRepository).delete(entity);
    }

    @Test
    void deleteTranscriptByVideoLectureId_shouldDoNothing_whenNotFound() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.findByVideoLectureId(videoLectureId)).thenReturn(Optional.empty());

        videoTranscriptService.deleteTranscriptByVideoLectureId(videoLectureId);

        verify(videoTranscriptRepository, never()).delete(any());
    }

    // --- existsByVideoLectureId ---

    @Test
    void existsByVideoLectureId_shouldReturnTrue_whenExists() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.existsByVideoLectureId(videoLectureId)).thenReturn(true);

        boolean result = videoTranscriptService.existsByVideoLectureId(videoLectureId);

        assertThat(result).isTrue();
    }

    @Test
    void existsByVideoLectureId_shouldReturnFalse_whenNotExists() {
        UUID videoLectureId = UUID.randomUUID();
        when(videoTranscriptRepository.existsByVideoLectureId(videoLectureId)).thenReturn(false);

        boolean result = videoTranscriptService.existsByVideoLectureId(videoLectureId);

        assertThat(result).isFalse();
    }

    // --- helper methods ---

    private VideoLecture createVideoLecture(UUID id) {
        VideoLecture vl = new VideoLecture();
        vl.setId(id);
        vl.setVideoUrl("https://example.com/video.mp4");
        vl.setDuration(300);
        return vl;
    }

    private VideoTranscriptRequest createRequest(UUID videoLectureId, String text, String lang,
                                                  Integer audioDuration, Integer wordCount,
                                                  Integer startTime, Integer endTime, Integer segmentIndex) {
        return VideoTranscriptRequest.builder()
                .videoLectureId(videoLectureId)
                .transcriptText(text)
                .languageCode(lang)
                .audioDuration(audioDuration)
                .wordCount(wordCount)
                .startTimeSeconds(startTime != null ? startTime : 0)
                .endTimeSeconds(endTime)
                .segmentIndex(segmentIndex)
                .build();
    }

    private VideoTranscript createTranscriptEntity(UUID id, VideoLecture videoLecture, String text, String lang,
                                                    Integer audioDuration, Integer wordCount,
                                                    Integer startTime, Integer endTime, Integer segmentIndex) {
        VideoTranscript vt = new VideoTranscript();
        vt.setId(id);
        vt.setVideoLecture(videoLecture);
        vt.setTranscriptText(text);
        vt.setLanguageCode(lang);
        vt.setAudioDuration(audioDuration);
        vt.setWordCount(wordCount);
        vt.setStartTimeSeconds(startTime);
        vt.setEndTimeSeconds(endTime);
        vt.setSegmentIndex(segmentIndex);
        return vt;
    }

    private VideoTranscriptResponse createResponse(UUID id, UUID videoLectureId, String text) {
        return VideoTranscriptResponse.builder()
                .id(id)
                .videoLectureId(videoLectureId)
                .transcriptText(text)
                .build();
    }
}
