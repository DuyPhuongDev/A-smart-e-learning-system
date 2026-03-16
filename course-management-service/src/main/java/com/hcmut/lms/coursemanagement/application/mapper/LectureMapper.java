package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LectureMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "completionRate", ignore = true)
    void updateEntityFromDTO(BaseLectureRequest dto, @MappingTarget Lecture entity);
    
    @Mapping(target = "chapterId", source = "chapter.id")
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "fileFormat", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "formatType", ignore = true)
    @Mapping(target = "numPages", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "wordCount", ignore = true)
    LectureResponse toResponseDTO(Lecture entity);
}

