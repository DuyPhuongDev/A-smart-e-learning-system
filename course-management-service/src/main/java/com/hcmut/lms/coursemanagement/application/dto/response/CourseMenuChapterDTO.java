package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseMenuChapterDTO {
    @JsonProperty("chapter_id")
    private UUID chapterId;
    private String title;
    private Integer order;
    private List<CourseMenuLectureDTO> lectures;
}
