package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.ChapterRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderListRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ChapterResponse;
import com.hcmut.lms.coursemanagement.application.mapper.ChapterMapper;
import com.hcmut.lms.coursemanagement.application.service.ChapterService;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChapterServiceImpl implements ChapterService {
    
    private final ChapterRepository chapterRepository;
    private final ClassSectionRepository classSectionRepository;
    private final ChapterMapper chapterMapper;
    
    @Override
    public ChapterResponse createChapter(ChapterRequest requestDTO) {
        log.info("Creating chapter: {}", requestDTO.getTitle());
        
        ClassSection classSection = classSectionRepository.findById(requestDTO.getClassSectionId())
                .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + requestDTO.getClassSectionId()));
        
        Chapter chapter = chapterMapper.toEntity(requestDTO);
        chapter.setClassSection(classSection);
        
        // Tự động gán order_index nếu không được cung cấp
        if (chapter.getOrderIndex() == null) {
            List<Chapter> existingChapters = chapterRepository.findByClassSectionIdOrderByOrderIndex(
                    requestDTO.getClassSectionId());
            int maxOrderIndex = existingChapters.stream()
                    .mapToInt(Chapter::getOrderIndex)
                    .max()
                    .orElse(-1);
            chapter.setOrderIndex(maxOrderIndex + 1);
        }
        
        Chapter savedChapter = chapterRepository.save(chapter);
        
        log.info("Chapter created successfully with id: {}", savedChapter.getId());
        return chapterMapper.toResponseDTO(savedChapter);
    }
    
    @Override
    public ChapterResponse updateChapter(UUID id, ChapterRequest requestDTO) {
        log.info("Updating chapter with id: {}", id);
        
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
        
        chapterMapper.updateEntityFromDTO(requestDTO, chapter);
        
        if (requestDTO.getClassSectionId() != null) {
            ClassSection classSection = classSectionRepository.findById(requestDTO.getClassSectionId())
                    .orElseThrow(() -> new RuntimeException("Class section not found with id: " + requestDTO.getClassSectionId()));
            chapter.setClassSection(classSection);
        }
        
        Chapter updatedChapter = chapterRepository.save(chapter);
        
        log.info("Chapter updated successfully with id: {}", id);
        return chapterMapper.toResponseDTO(updatedChapter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ChapterResponse getChapterById(UUID id) {
        log.info("Getting chapter with id: {}", id);
        
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
        
        return chapterMapper.toResponseDTO(chapter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponse> getChaptersByClassSectionId(UUID classSectionId) {
        log.info("Getting chapters by class section id: {}", classSectionId);
        
        return chapterRepository.findByClassSectionIdOrderByOrderIndex(classSectionId).stream()
                .map(chapterMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    public void deleteChapter(UUID id) {
        log.info("Deleting chapter with id: {}", id);
        
        if (!chapterRepository.existsById(id)) {
            throw new EntityNotFoundException("Chapter not found with id: " + id);
        }
        
        chapterRepository.deleteById(id);
        log.info("Chapter deleted successfully with id: {}", id);
    }
    
    @Override
    public List<ChapterResponse> reorderChapters(ReorderListRequest request) {
        log.info("Reordering chapters");
        
        Map<UUID, Integer> orderMap = request.getItems().stream()
                .collect(Collectors.toMap(ReorderRequest::getId, ReorderRequest::getNewOrderIndex));
        
        List<Chapter> chapters = chapterRepository.findAllById(orderMap.keySet());
        
        if (chapters.size() != orderMap.size()) {
            throw new EntityNotFoundException("Some chapters not found");
        }
        
        chapters.forEach(chapter -> {
            Integer newOrderIndex = orderMap.get(chapter.getId());
            chapter.setOrderIndex(newOrderIndex);
        });
        
        List<Chapter> savedChapters = chapterRepository.saveAll(chapters);
        
        log.info("Chapters reordered successfully");
        return savedChapters.stream()
                .sorted((c1, c2) -> c1.getOrderIndex().compareTo(c2.getOrderIndex()))
                .map(chapterMapper::toResponseDTO)
                .toList();
    }
}

