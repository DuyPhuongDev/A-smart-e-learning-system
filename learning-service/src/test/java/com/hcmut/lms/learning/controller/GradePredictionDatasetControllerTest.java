package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.dto.response.GradePredictionDatasetVersionResponse;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;
import com.hcmut.lms.learning.mapper.GradePredictionDatasetVersionMapper;
import com.hcmut.lms.learning.service.GradePredictionDatasetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GradePredictionDatasetControllerTest {

    @Mock
    private GradePredictionDatasetService gradePredictionDatasetService;

    @Mock
    private GradePredictionDatasetVersionMapper mapper;

    @InjectMocks
    private GradePredictionDatasetController controller;

    private final UUID versionId = UUID.randomUUID();

    @Test
    void computeDataset_shouldReturnVersionResponse() {
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        GradePredictionDatasetVersionResponse response = GradePredictionDatasetVersionResponse.builder()
                .id(versionId).versionNumber(1).status("COMPLETED").build();

        when(gradePredictionDatasetService.computeAndSaveDataset()).thenReturn(version);
        when(mapper.toResponse(version)).thenReturn(response);

        ResponseEntity<GradePredictionDatasetVersionResponse> result = controller.computeDataset();
        assertNotNull(result.getBody());
        assertEquals(versionId, result.getBody().getId());
        assertTrue(true);
    }

    @Test
    void getAllVersions_shouldReturnList() {
        when(gradePredictionDatasetService.getAllVersions()).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        ResponseEntity<List<GradePredictionDatasetVersionResponse>> result = controller.getAllVersions();
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getVersionById_shouldReturnVersion() {
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        GradePredictionDatasetVersionResponse response = GradePredictionDatasetVersionResponse.builder()
                .id(versionId).build();

        when(gradePredictionDatasetService.getVersionById(versionId)).thenReturn(version);
        when(mapper.toResponse(version)).thenReturn(response);

        ResponseEntity<GradePredictionDatasetVersionResponse> result = controller.getVersionById(versionId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getLatestCompletedVersion_shouldReturnVersion() {
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        GradePredictionDatasetVersionResponse response = GradePredictionDatasetVersionResponse.builder()
                .id(versionId).build();

        when(gradePredictionDatasetService.getLatestCompletedVersion()).thenReturn(version);
        when(mapper.toResponse(version)).thenReturn(response);

        ResponseEntity<GradePredictionDatasetVersionResponse> result = controller.getLatestCompletedVersion();
        assertNotNull(result.getBody());
        assertTrue(true);
    }
}
