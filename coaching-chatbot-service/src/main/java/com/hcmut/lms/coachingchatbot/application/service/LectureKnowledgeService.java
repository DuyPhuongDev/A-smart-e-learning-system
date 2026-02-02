package com.hcmut.lms.coachingchatbot.application.service;

import com.hcmut.lms.coachingchatbot.application.dto.request.CreateLectureKnowledgeRequest;
import com.hcmut.lms.coachingchatbot.application.dto.request.UpdateLectureKnowledgeRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeResponse;

import java.util.List;
import java.util.UUID;

public interface LectureKnowledgeService {

    LectureKnowledgeResponse createLectureKnowledge(CreateLectureKnowledgeRequest request);

    LectureKnowledgeResponse getLectureKnowledgeById(UUID id);

    LectureKnowledgeResponse getLectureKnowledgeByLectureId(UUID lectureId);

    List<LectureKnowledgeResponse> getAllLectureKnowledgeByLectureId(UUID lectureId);

    LectureKnowledgeResponse updateLectureKnowledge(UUID id, UpdateLectureKnowledgeRequest request);

    void deleteLectureKnowledge(UUID id);

    List<LectureKnowledgeResponse> getAllByStatus(String status);
}
