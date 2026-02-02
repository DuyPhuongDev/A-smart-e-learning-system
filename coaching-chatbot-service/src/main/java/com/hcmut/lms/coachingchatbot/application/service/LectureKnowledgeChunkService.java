package com.hcmut.lms.coachingchatbot.application.service;

import com.hcmut.lms.coachingchatbot.application.dto.request.CreateLectureKnowledgeChunkRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeChunkResponse;

import java.util.List;
import java.util.UUID;

public interface LectureKnowledgeChunkService {

    LectureKnowledgeChunkResponse createChunk(UUID lectureKnowledgeId, CreateLectureKnowledgeChunkRequest request);

    LectureKnowledgeChunkResponse getChunkById(UUID id);

    List<LectureKnowledgeChunkResponse> getChunksByLectureKnowledgeId(UUID lectureKnowledgeId);

    List<LectureKnowledgeChunkResponse> getChunksByLectureKnowledgeIdOrderByIndex(UUID lectureKnowledgeId);

    LectureKnowledgeChunkResponse updateChunk(UUID id, CreateLectureKnowledgeChunkRequest request);

    void deleteChunk(UUID id);

    void deleteAllChunksByLectureKnowledgeId(UUID lectureKnowledgeId);
}
