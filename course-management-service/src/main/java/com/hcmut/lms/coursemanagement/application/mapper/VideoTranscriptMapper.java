package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoTranscript;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VideoTranscriptMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "videoLecture", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VideoTranscript toEntity(VideoTranscriptRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "videoLecture", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(VideoTranscriptRequest request, @MappingTarget VideoTranscript entity);
}

