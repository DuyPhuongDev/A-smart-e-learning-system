package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.request.LectureNoteRequest;
import com.hcmut.lms.learning.dto.response.LectureNoteResponse;
import com.hcmut.lms.learning.entity.lecturenote.LectureNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LectureNoteMapper {

    @Mapping(target = "id", ignore = true)
    LectureNote toEntity(LectureNoteRequest request);

    LectureNoteResponse toResponse(LectureNote entity);

    void updateEntityFromRequest(
            LectureNoteRequest request,
            @MappingTarget LectureNote lectureNote
    );
}
