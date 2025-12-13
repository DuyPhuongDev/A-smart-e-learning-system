package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.ChapterRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderListRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ChapterResponse;

import java.util.List;
import java.util.UUID;

public interface ChapterService {
    ChapterResponse createChapter(ChapterRequest requestDTO);
    ChapterResponse updateChapter(UUID id, ChapterRequest requestDTO);
    ChapterResponse getChapterById(UUID id);
    List<ChapterResponse> getChaptersByClassSectionId(UUID classSectionId);
    void deleteChapter(UUID id);
    List<ChapterResponse> reorderChapters(ReorderListRequest request);
}

