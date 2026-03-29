package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoTranscript;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for VideoTranscript entity and DTOs
 * Handles conversion between request/response DTOs and domain entity
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VideoTranscriptMapper {
    
    /**
     * Convert VideoTranscriptRequest to VideoTranscript entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "videoLecture", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VideoTranscript toEntity(VideoTranscriptRequest request);
    
    /**
     * Update VideoTranscript entity from VideoTranscriptRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "videoLecture", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(VideoTranscriptRequest request, @MappingTarget VideoTranscript entity);

    /**
     * Convert VideoTranscript entity to VideoTranscriptResponse
     */
    @Mapping(source = "videoLecture.id", target = "videoLectureId")
    VideoTranscriptResponse toResponse(VideoTranscript entity);
}
