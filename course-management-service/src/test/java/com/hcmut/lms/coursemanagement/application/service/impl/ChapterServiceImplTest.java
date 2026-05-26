package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.ChapterRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ChapterResponse;
import com.hcmut.lms.coursemanagement.application.mapper.ChapterMapper;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapterServiceImplTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private ChapterMapper chapterMapper;

    @InjectMocks
    private ChapterServiceImpl chapterService;

    // --- createChapter ---

    @Test
    void createChapter_shouldReturnResponseWithAutoOrderIndex_whenValidRequestNoOrderIndex() {
        UUID classSectionId = UUID.randomUUID();
        ChapterRequest request = createRequest("Chapter 1", "desc", null, classSectionId);
        ClassSection classSection = createClassSection(classSectionId);
        Chapter entity = createEntity("Chapter 1", null);
        Chapter savedEntity = createEntityWithId(UUID.randomUUID(), "Chapter 1", 2, classSection);
        ChapterResponse expectedResponse = createResponse(savedEntity.getId(), "Chapter 1", 2);

        Chapter existingChapter = createEntityWithId(UUID.randomUUID(), "Existing", 1, classSection);

        when(classSectionRepository.findById(classSectionId)).thenReturn(Optional.of(classSection));
        when(chapterMapper.toEntity(request)).thenReturn(entity);
        when(chapterRepository.findByClassSectionIdOrderByOrderIndex(classSectionId))
                .thenReturn(List.of(existingChapter));
        when(chapterRepository.save(entity)).thenReturn(savedEntity);
        when(chapterMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        ChapterResponse result = chapterService.createChapter(request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(entity.getOrderIndex()).isEqualTo(2); // max order is 1, so auto = 2
        verify(chapterRepository).save(entity);
    }

    @Test
    void createChapter_shouldReturnResponseWithAutoOrderIndex_whenNoExistingChapters() {
        UUID classSectionId = UUID.randomUUID();
        ChapterRequest request = createRequest("Chapter 1", null, null, classSectionId);
        ClassSection classSection = createClassSection(classSectionId);
        Chapter entity = createEntity("Chapter 1", null);
        Chapter savedEntity = createEntityWithId(UUID.randomUUID(), "Chapter 1", 0, classSection);
        ChapterResponse expectedResponse = createResponse(savedEntity.getId(), "Chapter 1", 0);

        when(classSectionRepository.findById(classSectionId)).thenReturn(Optional.of(classSection));
        when(chapterMapper.toEntity(request)).thenReturn(entity);
        when(chapterRepository.findByClassSectionIdOrderByOrderIndex(classSectionId))
                .thenReturn(List.of());
        when(chapterRepository.save(entity)).thenReturn(savedEntity);
        when(chapterMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        ChapterResponse result = chapterService.createChapter(request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(entity.getOrderIndex()).isEqualTo(0); // max -1 -> 0
    }

    @Test
    void createChapter_shouldUseProvidedOrderIndex_whenOrderIndexGiven() {
        UUID classSectionId = UUID.randomUUID();
        ChapterRequest request = createRequest("Chapter 1", null, 5, classSectionId);
        ClassSection classSection = createClassSection(classSectionId);
        Chapter entity = createEntity("Chapter 1", 5);
        Chapter savedEntity = createEntityWithId(UUID.randomUUID(), "Chapter 1", 5, classSection);
        ChapterResponse expectedResponse = createResponse(savedEntity.getId(), "Chapter 1", 5);

        when(classSectionRepository.findById(classSectionId)).thenReturn(Optional.of(classSection));
        when(chapterMapper.toEntity(request)).thenReturn(entity);
        when(chapterRepository.save(entity)).thenReturn(savedEntity);
        when(chapterMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        ChapterResponse result = chapterService.createChapter(request);

        assertThat(result).isEqualTo(expectedResponse);
        // Should not call findByClassSectionIdOrderByOrderIndex when orderIndex is provided
        verify(chapterRepository, never()).findByClassSectionIdOrderByOrderIndex(any());
    }

    @Test
    void createChapter_shouldThrowException_whenClassSectionNotFound() {
        UUID classSectionId = UUID.randomUUID();
        ChapterRequest request = createRequest("Chapter 1", null, null, classSectionId);

        when(classSectionRepository.findById(classSectionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chapterService.createChapter(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Class section not found");
        verify(chapterRepository, never()).save(any());
    }

    // --- updateChapter ---

    @Test
    void updateChapter_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        ChapterRequest request = createRequest("Updated", "desc", null, null);
        Chapter existing = createEntityWithId(id, "Old", 0, null);
        Chapter updated = createEntityWithId(id, "Updated", 0, null);
        ChapterResponse expectedResponse = createResponse(id, "Updated", 0);

        when(chapterRepository.findById(id)).thenReturn(Optional.of(existing));
        when(chapterRepository.save(existing)).thenReturn(updated);
        when(chapterMapper.toResponseDTO(updated)).thenReturn(expectedResponse);

        ChapterResponse result = chapterService.updateChapter(id, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(chapterMapper).updateEntityFromDTO(request, existing);
    }

    @Test
    void updateChapter_shouldUpdateClassSection_whenClassSectionIdProvided() {
        UUID id = UUID.randomUUID();
        UUID newClassSectionId = UUID.randomUUID();
        ChapterRequest request = createRequest("Updated", null, null, newClassSectionId);
        Chapter existing = createEntityWithId(id, "Old", 0, null);
        ClassSection newClassSection = createClassSection(newClassSectionId);
        Chapter updated = createEntityWithId(id, "Updated", 0, newClassSection);
        ChapterResponse expectedResponse = createResponse(id, "Updated", 0);

        when(chapterRepository.findById(id)).thenReturn(Optional.of(existing));
        when(classSectionRepository.findById(newClassSectionId)).thenReturn(Optional.of(newClassSection));
        when(chapterRepository.save(existing)).thenReturn(updated);
        when(chapterMapper.toResponseDTO(updated)).thenReturn(expectedResponse);

        ChapterResponse result = chapterService.updateChapter(id, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(chapterMapper).updateEntityFromDTO(request, existing);
    }

    @Test
    void updateChapter_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        ChapterRequest request = createRequest("Updated", null, null, null);

        when(chapterRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chapterService.updateChapter(id, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Chapter not found");
    }

    // --- getChapterById ---

    @Test
    void getChapterById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Chapter entity = createEntityWithId(id, "Chapter", 0, null);
        ChapterResponse expectedResponse = createResponse(id, "Chapter", 0);

        when(chapterRepository.findById(id)).thenReturn(Optional.of(entity));
        when(chapterMapper.toResponseDTO(entity)).thenReturn(expectedResponse);

        ChapterResponse result = chapterService.getChapterById(id);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getChapterById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(chapterRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chapterService.getChapterById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Chapter not found");
    }

    // --- getChaptersByClassSectionId ---

    @Test
    void getChaptersByClassSectionId_shouldReturnList_whenChaptersExist() {
        UUID classSectionId = UUID.randomUUID();
        Chapter e1 = createEntityWithId(UUID.randomUUID(), "Ch1", 0, null);
        Chapter e2 = createEntityWithId(UUID.randomUUID(), "Ch2", 1, null);
        ChapterResponse r1 = createResponse(e1.getId(), "Ch1", 0);
        ChapterResponse r2 = createResponse(e2.getId(), "Ch2", 1);

        when(chapterRepository.findByClassSectionIdOrderByOrderIndex(classSectionId))
                .thenReturn(List.of(e1, e2));
        when(chapterMapper.toResponseDTO(e1)).thenReturn(r1);
        when(chapterMapper.toResponseDTO(e2)).thenReturn(r2);

        List<ChapterResponse> result = chapterService.getChaptersByClassSectionId(classSectionId);

        assertThat(result).hasSize(2).containsExactly(r1, r2);
    }

    @Test
    void getChaptersByClassSectionId_shouldReturnEmptyList_whenNoChapters() {
        UUID classSectionId = UUID.randomUUID();
        when(chapterRepository.findByClassSectionIdOrderByOrderIndex(classSectionId))
                .thenReturn(List.of());

        List<ChapterResponse> result = chapterService.getChaptersByClassSectionId(classSectionId);

        assertThat(result).isEmpty();
    }

    // --- deleteChapter ---

    @Test
    void deleteChapter_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(chapterRepository.existsById(id)).thenReturn(true);

        chapterService.deleteChapter(id);

        verify(chapterRepository).deleteById(id);
    }

    @Test
    void deleteChapter_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(chapterRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> chapterService.deleteChapter(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Chapter not found");
        verify(chapterRepository, never()).deleteById(any());
    }

    // --- reorderChapters ---

    @Test
    void reorderChapters_shouldReorderDownwards_whenNewOrderGreaterThanOldOrder() {
        List<Chapter> chapters = createOrderedChapters(5);
        // Move chapter at index 1 to index 3
        Chapter target = chapters.get(1); // orderIndex=1
        ReorderRequest request = new ReorderRequest(target.getId(), 3);

        when(chapterRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(chapterMapper.toResponseDTO(any(Chapter.class))).thenAnswer(inv -> {
            Chapter c = inv.getArgument(0);
            return createResponse(c.getId(), c.getTitle(), c.getOrderIndex());
        });

        List<ChapterResponse> result = chapterService.reorderChapters(request);

        // Target should be at newOrder = 3
        assertThat(target.getOrderIndex()).isEqualTo(3);
        // Other chapters shifted accordingly
        assertThat(chapters.get(0).getOrderIndex()).isEqualTo(0);
        assertThat(chapters.get(2).getOrderIndex()).isEqualTo(1);
        assertThat(chapters.get(3).getOrderIndex()).isEqualTo(2);
        assertThat(chapters.get(4).getOrderIndex()).isEqualTo(4);
        verify(chapterRepository, times(2)).saveAndFlush(target); // temp + final
        verify(chapterRepository).saveAllAndFlush(chapters);
    }

    @Test
    void reorderChapters_shouldReorderUpwards_whenNewOrderLessThanOldOrder() {
        List<Chapter> chapters = createOrderedChapters(5);
        // Move chapter at index 3 to index 1
        Chapter target = chapters.get(3); // orderIndex=3
        ReorderRequest request = new ReorderRequest(target.getId(), 1);

        when(chapterRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(chapterMapper.toResponseDTO(any(Chapter.class))).thenAnswer(inv -> {
            Chapter c = inv.getArgument(0);
            return createResponse(c.getId(), c.getTitle(), c.getOrderIndex());
        });

        List<ChapterResponse> result = chapterService.reorderChapters(request);

        assertThat(target.getOrderIndex()).isEqualTo(1);
        assertThat(chapters.get(0).getOrderIndex()).isEqualTo(0);
        assertThat(chapters.get(1).getOrderIndex()).isEqualTo(2);
        assertThat(chapters.get(2).getOrderIndex()).isEqualTo(3);
        assertThat(chapters.get(4).getOrderIndex()).isEqualTo(4);
    }

    @Test
    void reorderChapters_shouldReturnCurrentState_whenOrderUnchanged() {
        List<Chapter> chapters = createOrderedChapters(3);
        Chapter target = chapters.get(1); // orderIndex=1
        ReorderRequest request = new ReorderRequest(target.getId(), 1);

        when(chapterRepository.findById(target.getId())).thenReturn(Optional.of(target));
        // Return sorted chapters for final result
        when(chapterMapper.toResponseDTO(any(Chapter.class)))
                .thenAnswer(inv -> {
                    Chapter c = inv.getArgument(0);
                    return createResponse(c.getId(), c.getTitle(), c.getOrderIndex());
                });

        List<ChapterResponse> result = chapterService.reorderChapters(request);

        // No changes expected
        assertThat(target.getOrderIndex()).isEqualTo(1);
        verify(chapterRepository, never()).saveAndFlush(any());
        verify(chapterRepository, never()).saveAllAndFlush(any());
    }

    @Test
    void reorderChapters_shouldThrowException_whenChapterNotFound() {
        UUID id = UUID.randomUUID();
        ReorderRequest request = new ReorderRequest(id, 0);

        when(chapterRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chapterService.reorderChapters(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Chapter not found");
    }

    @Test
    void reorderChapters_shouldClamp_whenNewOrderExceedsSize() {
        List<Chapter> chapters = createOrderedChapters(3);
        Chapter target = chapters.get(0); // orderIndex=0
        ReorderRequest request = new ReorderRequest(target.getId(), 10); // > size(3)

        when(chapterRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(chapterMapper.toResponseDTO(any(Chapter.class))).thenAnswer(inv -> {
            Chapter c = inv.getArgument(0);
            return createResponse(c.getId(), c.getTitle(), c.getOrderIndex());
        });

        List<ChapterResponse> result = chapterService.reorderChapters(request);

        // Should be clamped to size-1 = 2
        assertThat(target.getOrderIndex()).isEqualTo(2);
        verify(chapterRepository, times(2)).saveAndFlush(target);
        verify(chapterRepository).saveAllAndFlush(chapters);
    }

    @Test
    void reorderChapters_shouldClampToZero_whenNewOrderLessThanZero() {
        List<Chapter> chapters = createOrderedChapters(3);
        Chapter target = chapters.get(2); // orderIndex=2
        ReorderRequest request = new ReorderRequest(target.getId(), -5); // < 0

        when(chapterRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(chapterMapper.toResponseDTO(any(Chapter.class))).thenAnswer(inv -> {
            Chapter c = inv.getArgument(0);
            return createResponse(c.getId(), c.getTitle(), c.getOrderIndex());
        });

        List<ChapterResponse> result = chapterService.reorderChapters(request);

        // Should be clamped to 0
        assertThat(target.getOrderIndex()).isEqualTo(0);
        verify(chapterRepository, times(2)).saveAndFlush(target);
        verify(chapterRepository).saveAllAndFlush(chapters);
    }

    // --- helper methods ---

    private ChapterRequest createRequest(String title, String description, Integer orderIndex, UUID classSectionId) {
        ChapterRequest request = new ChapterRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setOrderIndex(orderIndex);
        request.setClassSectionId(classSectionId);
        return request;
    }

    private ClassSection createClassSection(UUID id) {
        ClassSection cs = new ClassSection();
        cs.setId(id);
        cs.setSectionName("Section-" + id.toString().substring(0, 8));
        cs.setChapters(new ArrayList<>());
        return cs;
    }

    private Chapter createEntity(String title, Integer orderIndex) {
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setOrderIndex(orderIndex);
        return chapter;
    }

    private Chapter createEntityWithId(UUID id, String title, Integer orderIndex, ClassSection classSection) {
        Chapter chapter = createEntity(title, orderIndex);
        chapter.setId(id);
        chapter.setClassSection(classSection);
        return chapter;
    }

    private ChapterResponse createResponse(UUID id, String title, Integer orderIndex) {
        return ChapterResponse.builder()
                .id(id)
                .title(title)
                .orderIndex(orderIndex)
                .build();
    }

    private List<Chapter> createOrderedChapters(int count) {
        ClassSection classSection = createClassSection(UUID.randomUUID());
        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Chapter chapter = createEntityWithId(UUID.randomUUID(), "Chapter " + i, i, classSection);
            chapters.add(chapter);
        }
        classSection.setChapters(new ArrayList<>(chapters));
        return chapters;
    }
}
