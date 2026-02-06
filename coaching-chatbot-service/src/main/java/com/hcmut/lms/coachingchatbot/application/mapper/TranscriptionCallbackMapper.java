package com.hcmut.lms.coachingchatbot.application.mapper;

import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackBatchRequest;
import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackRequest;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for converting transcription callback payloads to VideoTranscriptRequest
 * Mirrors the MapStruct style used in course-management-service.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TranscriptionCallbackMapper {

    /**
     * Map single transcription callback to VideoTranscriptRequest.
     */
    @Mapping(target = "videoLectureId", source = "videoLectureId")
    @Mapping(target = "transcriptText", source = "transcriptText")
    @Mapping(target = "languageCode", source = "languageCode")
    @Mapping(target = "audioDuration", source = "audioDuration")
    @Mapping(target = "wordCount", source = "wordCount")
    @Mapping(target = "startTimeSeconds", expression = "java(request.getStartTimeSeconds() != null ? request.getStartTimeSeconds() : 0)")
    @Mapping(target = "endTimeSeconds", source = "endTimeSeconds")
    @Mapping(target = "segmentIndex", expression = "java(request.getSegmentIndex() != null ? request.getSegmentIndex() : 0)")
    VideoTranscriptRequest toVideoTranscriptRequest(TranscriptionCallbackRequest request);

    /**
     * Map batch transcription segment + common batch info to VideoTranscriptRequest.
     */
    @Mapping(target = "videoLectureId", source = "batchRequest.videoLectureId")
    @Mapping(target = "transcriptText", source = "segment.transcriptText")
    @Mapping(target = "languageCode", source = "batchRequest.languageCode")
    @Mapping(target = "audioDuration", source = "batchRequest.audioDuration")
    @Mapping(target = "wordCount", source = "segment.wordCount")
    @Mapping(target = "startTimeSeconds", expression = "java(segment.getStartTimeSeconds() != null ? segment.getStartTimeSeconds() : 0)")
    @Mapping(target = "endTimeSeconds", source = "segment.endTimeSeconds")
    @Mapping(target = "segmentIndex", expression = "java(segment.getSegmentIndex() != null ? segment.getSegmentIndex() : 0)")
    VideoTranscriptRequest toVideoTranscriptRequest(TranscriptionCallbackBatchRequest batchRequest,
                                                   TranscriptionCallbackBatchRequest.TranscriptSegment segment);
}
