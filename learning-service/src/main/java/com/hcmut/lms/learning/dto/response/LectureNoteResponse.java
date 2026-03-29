package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.learning.client.dto.LectureType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureNoteResponse {
    private UUID id;
    private UUID lectureId;
    private UUID studentId;
    private LectureType lectureType;
    private String content;
    private Integer contentPosition;
    private Instant createdAt;
    private Instant updatedAt;
}
