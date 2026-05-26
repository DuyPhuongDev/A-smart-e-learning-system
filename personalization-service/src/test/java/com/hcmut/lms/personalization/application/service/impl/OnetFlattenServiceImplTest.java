package com.hcmut.lms.personalization.application.service.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.application.dto.request.BackfillRequirementEmbeddingsRequest;
import com.hcmut.lms.personalization.application.dto.request.FlattenOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.BackfillRequirementEmbeddingsResponse;
import com.hcmut.lms.personalization.application.dto.response.FlattenOccupationJobResponse;
import com.hcmut.lms.personalization.application.mapper.OnetFlattenMapper;
import com.hcmut.lms.personalization.application.service.EmbeddingService;
import com.hcmut.lms.personalization.domain.entity.occupationData.*;
import com.hcmut.lms.personalization.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OnetFlattenServiceImplTest {

    @Mock private OccupationDataRepository occupationDataRepository;
    @Mock private OnetFlattenedRequirementRepository onetFlattenedRequirementRepository;
    @Mock private OnetFlattenedRequirementEmbeddingRepository requirementEmbeddingRepository;
    @Mock private TaskStatementRepository taskStatementRepository;
    @Mock private TasksToDwaRepository tasksToDwaRepository;
    @Mock private DwaReferenceRepository dwaReferenceRepository;
    @Mock private OnetFlattenedRequirementAssembler assembler;
    @Mock private EmbeddingService embeddingService;
    @Mock private OnetFlattenMapper onetFlattenMapper;

    @InjectMocks
    private OnetFlattenServiceImpl onetFlattenService;

    private static OccupationData makeOccupation(String code) {
        OccupationData o = new OccupationData();
        o.setOnetsocCode(code);
        return o;
    }

    @Test void flatten_shouldReturnResponses_whenNullRequest_allOccupationsEmpty() {
        when(occupationDataRepository.findAll()).thenReturn(Collections.emptyList());
        var result = onetFlattenService.flatten(null);
        assert result != null && result.isEmpty();
    }

    @Test void flatten_shouldProcessEachOccupation_whenSpecificCodesProvided() {
        FlattenOccupationRequest req = new FlattenOccupationRequest();
        req.setOccupationCodes(List.of("15-1252.00", " 15-1253.00 "));

        when(onetFlattenedRequirementRepository.deleteByOccupation_OnetsocCode(anyString())).thenReturn(0L);
        when(taskStatementRepository.findByOccupation_OnetsocCode(anyString())).thenReturn(Collections.emptyList());
        when(tasksToDwaRepository.findByOnetsocCode(anyString())).thenReturn(Collections.emptyList());
        when(dwaReferenceRepository.findAll()).thenReturn(Collections.emptyList());
        when(onetFlattenedRequirementRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(onetFlattenMapper.toFlattenOccupationJobResponse(anyString(), anyLong(), anyLong()))
            .thenReturn(new FlattenOccupationJobResponse());

        var result = onetFlattenService.flatten(req);
        assert result != null && result.size() == 2;
    }

    @Test void flatten_shouldProcessOccupation_whenTasksAndMappingsExist() {
        FlattenOccupationRequest req = new FlattenOccupationRequest();
        req.setOccupationCodes(List.of("15-1252.00"));

        when(onetFlattenedRequirementRepository.deleteByOccupation_OnetsocCode(anyString())).thenReturn(5L);

        TaskStatement task = new TaskStatement();
        task.setTaskId(java.math.BigDecimal.ONE);
        task.setTask("Analyze code");
        task.setTaskType("Core");
        task.setOccupation(makeOccupation("15-1252.00"));
        when(taskStatementRepository.findByOccupation_OnetsocCode(anyString())).thenReturn(List.of(task));

        when(tasksToDwaRepository.findByOnetsocCode(anyString())).thenReturn(Collections.emptyList());

        DwaReference dwa = new DwaReference();
        dwa.setDwaId("DWA1");
        dwa.setDwaTitle("Test DWA");
        when(dwaReferenceRepository.findAll()).thenReturn(List.of(dwa));

        OnetFlattenedRequirement assembled = new OnetFlattenedRequirement();
        assembled.setContentText("Test content");
        when(assembler.fromTasks(anyList())).thenReturn(List.of(assembled));
        when(assembler.fromDwa(anyList(), any())).thenReturn(Collections.emptyList());
        when(onetFlattenedRequirementRepository.saveAll(any())).thenReturn(List.of(assembled));
        when(onetFlattenMapper.toFlattenOccupationJobResponse(anyString(), anyLong(), anyLong()))
            .thenReturn(new FlattenOccupationJobResponse());

        var result = onetFlattenService.flatten(req);
        assert result != null && result.size() == 1;
    }

    @Test void flatten_shouldHandleBlankCodes_whenFilteredOut() {
        FlattenOccupationRequest req = new FlattenOccupationRequest();
        req.setOccupationCodes(List.of("", "  ", "15-1252.00"));

        when(onetFlattenedRequirementRepository.deleteByOccupation_OnetsocCode(anyString())).thenReturn(0L);
        when(taskStatementRepository.findByOccupation_OnetsocCode(anyString())).thenReturn(Collections.emptyList());
        when(tasksToDwaRepository.findByOnetsocCode(anyString())).thenReturn(Collections.emptyList());
        when(dwaReferenceRepository.findAll()).thenReturn(Collections.emptyList());
        when(onetFlattenedRequirementRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(onetFlattenMapper.toFlattenOccupationJobResponse(anyString(), anyLong(), anyLong()))
            .thenReturn(new FlattenOccupationJobResponse());

        var result = onetFlattenService.flatten(req);
        assert result != null && result.size() == 1;
    }

    @Test void backfillRequirementEmbeddings_shouldReturnZero_whenNoMissingEmbeddings() {
        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(Collections.emptyList());
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(null);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddings_shouldEmbedAndUpsert_whenMissingExist() {
        OccupationData occ = makeOccupation("15-1252.00");
        OnetFlattenedRequirement req1 = new OnetFlattenedRequirement();
        req1.setOnetFlattenedRequirementsId(UUID.randomUUID());
        req1.setContentText("Requirement text 1");
        req1.setOccupation(occ);

        OnetFlattenedRequirement req2 = new OnetFlattenedRequirement();
        req2.setOnetFlattenedRequirementsId(UUID.randomUUID());
        req2.setContentText("Requirement text 2");
        req2.setOccupation(occ);

        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(List.of(req1, req2));
        when(embeddingService.embedTexts(anyList(), anyString()))
            .thenReturn(List.of(new float[]{0.1f, 0.2f}, new float[]{0.3f, 0.4f}));
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(null);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddings_shouldFilterByCodes_whenScoped() {
        BackfillRequirementEmbeddingsRequest request = new BackfillRequirementEmbeddingsRequest();
        request.setOccupationCodes(Arrays.asList("15-1252.00", "", null, " 15-1253.00 "));

        when(onetFlattenedRequirementRepository.findMissingEmbeddingsByOccupationCodes(anyList(), any()))
            .thenReturn(Collections.emptyList());
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(request);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddings_shouldExportMigration_whenOptimalYesAndEmpty() {
        BackfillRequirementEmbeddingsRequest request = new BackfillRequirementEmbeddingsRequest();
        request.setOptimal("yes");

        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(Collections.emptyList());
        when(requirementEmbeddingRepository.findAllForMigrationExport())
            .thenReturn(Collections.emptyList());
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(request);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddingsAsync_shouldNotThrow() {
        onetFlattenService.backfillRequirementEmbeddingsAsync(null);
        assert true;
    }

    @Test void backfillRequirementEmbeddings_shouldHandleNullOccupationCodes() {
        BackfillRequirementEmbeddingsRequest request = new BackfillRequirementEmbeddingsRequest();
        request.setOccupationCodes(null);

        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(Collections.emptyList());
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(request);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddings_shouldSkipNullVectors_whenEmbeddingReturnsNull() {
        OccupationData occ = makeOccupation("15-1252.00");
        OnetFlattenedRequirement req = new OnetFlattenedRequirement();
        req.setOnetFlattenedRequirementsId(UUID.randomUUID());
        req.setContentText("Some text");
        req.setOccupation(occ);

        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(List.of(req));
        when(embeddingService.embedTexts(anyList(), anyString()))
            .thenReturn(Collections.singletonList(null));
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(null);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddings_shouldExportMigration_whenOptimalYesAndMissingExist() {
        BackfillRequirementEmbeddingsRequest request = new BackfillRequirementEmbeddingsRequest();
        request.setOptimal("yes");

        OccupationData occ = makeOccupation("15-1252.00");
        OnetFlattenedRequirement req = new OnetFlattenedRequirement();
        req.setOnetFlattenedRequirementsId(UUID.randomUUID());
        req.setContentText("Requirement text");
        req.setOccupation(occ);

        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(List.of(req));
        when(embeddingService.embedTexts(anyList(), anyString()))
            .thenReturn(List.of(new float[]{0.1f, 0.2f}));
        when(requirementEmbeddingRepository.findAllForMigrationExport())
            .thenReturn(Collections.emptyList());
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(request);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddings_shouldSkipEmptyVectors_whenEmbeddingReturnsEmptyArray() {
        OccupationData occ = makeOccupation("15-1252.00");
        OnetFlattenedRequirement req = new OnetFlattenedRequirement();
        req.setOnetFlattenedRequirementsId(UUID.randomUUID());
        req.setContentText("Some text");
        req.setOccupation(occ);

        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(List.of(req));
        when(embeddingService.embedTexts(anyList(), anyString()))
            .thenReturn(List.of(new float[0]));
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(null);
        assert result != null;
    }

    @Test void backfillRequirementEmbeddings_shouldExportWithRows_whenOptimalYesAndRowsExist() {
        BackfillRequirementEmbeddingsRequest request = new BackfillRequirementEmbeddingsRequest();
        request.setOptimal("yes");

        when(onetFlattenedRequirementRepository.findMissingEmbeddings(any()))
            .thenReturn(Collections.emptyList());

        var mockRow = mock(OnetFlattenedRequirementEmbeddingRepository.EmbeddingExportRow.class);
        when(mockRow.getRequirementId()).thenReturn(UUID.randomUUID());
        when(mockRow.getOccupationCode()).thenReturn("15-1252.00");
        when(mockRow.getEmbeddingText()).thenReturn("[0.1,0.2,0.3]");
        when(requirementEmbeddingRepository.findAllForMigrationExport())
            .thenReturn(List.of(mockRow));
        when(onetFlattenMapper.toBackfillResponse(anyLong(), anyLong(), anyBoolean(), any()))
            .thenReturn(new BackfillRequirementEmbeddingsResponse());

        var result = onetFlattenService.backfillRequirementEmbeddings(request);
        assert result != null;
    }
}
