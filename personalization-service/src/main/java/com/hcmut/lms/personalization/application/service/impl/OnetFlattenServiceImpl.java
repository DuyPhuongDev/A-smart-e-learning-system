package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.request.BackfillRequirementEmbeddingsRequest;
import com.hcmut.lms.personalization.application.dto.request.FlattenOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.BackfillRequirementEmbeddingsResponse;
import com.hcmut.lms.personalization.application.dto.response.FlattenOccupationJobResponse;
import com.hcmut.lms.personalization.application.service.EmbeddingService;
import com.hcmut.lms.personalization.application.service.OnetFlattenService;
import com.hcmut.lms.personalization.application.mapper.OnetFlattenedRequirementAssembler;
import com.hcmut.lms.personalization.application.entity.DwaReference;
import com.hcmut.lms.personalization.application.entity.OccupationData;
import com.hcmut.lms.personalization.application.entity.OnetFlattenedRequirement;
import com.hcmut.lms.personalization.application.entity.TaskStatement;
import com.hcmut.lms.personalization.application.entity.TasksToDwa;
import com.hcmut.lms.personalization.repository.DwaReferenceRepository;
import com.hcmut.lms.personalization.repository.OccupationDataRepository;
import com.hcmut.lms.personalization.repository.OnetFlattenedRequirementEmbeddingRepository;
import com.hcmut.lms.personalization.repository.OnetFlattenedRequirementRepository;
import com.hcmut.lms.personalization.repository.TaskStatementRepository;
import com.hcmut.lms.personalization.repository.TasksToDwaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnetFlattenServiceImpl implements OnetFlattenService {

    private static final int DEFAULT_MAX_ROWS = 5000;
    private static final String V4_MIGRATION_FILE = "personalization-service/src/main/resources/db/migration/V4__init_onet_flattened_requirement_embeddings.sql";

    private final OccupationDataRepository occupationDataRepository;
    private final OnetFlattenedRequirementRepository onetFlattenedRequirementRepository;
    private final OnetFlattenedRequirementEmbeddingRepository requirementEmbeddingRepository;
    private final TaskStatementRepository taskStatementRepository;
    private final TasksToDwaRepository tasksToDwaRepository;
    private final DwaReferenceRepository dwaReferenceRepository;
    private final OnetFlattenedRequirementAssembler assembler;
    private final EmbeddingService embeddingService;

    @Override
    @Transactional
    public List<FlattenOccupationJobResponse> flatten(FlattenOccupationRequest request) {
        List<String> codes = resolveCodes(request);
        List<FlattenOccupationJobResponse> responses = new ArrayList<>();
        for (String code : codes) {
            FlattenOccupationJobResponse resp = processOccupation(code);
            responses.add(resp);
        }
        return responses;
    }

    @Override
    @Async("taskExecutor")
    public void backfillRequirementEmbeddingsAsync(BackfillRequirementEmbeddingsRequest request) {
        try {
            BackfillRequirementEmbeddingsResponse response = backfillRequirementEmbeddings(request);
            log.info("Async requirement-embedding backfill completed: missing={} embedded={} migrationGenerated={}",
                    response.getMissingCount(), response.getEmbeddedCount(), response.isMigrationGenerated());
        } catch (Exception ex) {
            log.error("Async requirement-embedding backfill failed", ex);
        }
    }

    @Override
    public BackfillRequirementEmbeddingsResponse backfillRequirementEmbeddings(BackfillRequirementEmbeddingsRequest request) {
        List<String> scopedCodes = request != null
                ? sanitizeOccupationCodes(request.getOccupationCodes())
                : List.of();

        List<OnetFlattenedRequirement> missing = scopedCodes.isEmpty()
                ? onetFlattenedRequirementRepository.findMissingEmbeddings(PageRequest.of(0, DEFAULT_MAX_ROWS))
                : onetFlattenedRequirementRepository.findMissingEmbeddingsByOccupationCodes(scopedCodes, PageRequest.of(0, DEFAULT_MAX_ROWS));

        long missingCount = missing.size();
        if (missing.isEmpty()) {
            boolean generated = request != null && "yes".equalsIgnoreCase(request.getOptimal()) && exportV4Migration();
            return BackfillRequirementEmbeddingsResponse.builder()
                    .missingCount(0)
                    .embeddedCount(0)
                    .migrationGenerated(generated)
                    .migrationFile(generated ? V4_MIGRATION_FILE : null)
                    .build();
        }

        List<String> texts = missing.stream().map(OnetFlattenedRequirement::getContentText).toList();
        List<float[]> vectors = embeddingService.embedTexts(texts, "retrieval_document");

        long embeddedCount = 0;
        for (int i = 0; i < missing.size(); i++) {
            float[] vector = i < vectors.size() ? vectors.get(i) : null;
            if (vector == null || vector.length == 0) {
                continue;
            }
            OnetFlattenedRequirement requirement = missing.get(i);
            String vectorLiteral = toVectorLiteral(vector);
            requirementEmbeddingRepository.upsertEmbedding(
                    requirement.getOnetFlattenedRequirementsId(),
                    requirement.getOccupation().getOnetsocCode(),
                    vectorLiteral);
            embeddedCount++;
        }

        boolean generated = request != null && "yes".equalsIgnoreCase(request.getOptimal()) && exportV4Migration();

        return BackfillRequirementEmbeddingsResponse.builder()
                .missingCount(missingCount)
                .embeddedCount(embeddedCount)
                .migrationGenerated(generated)
                .migrationFile(generated ? V4_MIGRATION_FILE : null)
                .build();
    }

    private List<String> resolveCodes(FlattenOccupationRequest request) {
        if (request == null || request.getOccupationCodes() == null || request.getOccupationCodes().isEmpty()) {
            return occupationDataRepository.findAll().stream().map(OccupationData::getOnetsocCode).toList();
        }
        return request.getOccupationCodes().stream().filter(c -> c != null && !c.isBlank())
                .map(String::trim).map(String::toUpperCase).distinct().collect(Collectors.toList());
    }

    private List<String> sanitizeOccupationCodes(List<String> occupationCodes) {
        if (occupationCodes == null || occupationCodes.isEmpty()) {
            return List.of();
        }
        return occupationCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .map(String::toUpperCase)
                .distinct()
                .toList();
    }

    private FlattenOccupationJobResponse processOccupation(String occupationCode) {
        log.info("Flattening occupation {} via repositories", occupationCode);
        long deleted = onetFlattenedRequirementRepository.deleteByOccupation_OnetsocCode(occupationCode);

        List<TaskStatement> tasks = taskStatementRepository.findByOccupation_OnetsocCode(occupationCode);
        List<TasksToDwa> mappings = tasksToDwaRepository.findByOnetsocCode(occupationCode);
        Map<String, DwaReference> dwaById = dwaReferenceRepository.findAll().stream()
                .collect(Collectors.toMap(DwaReference::getDwaId, d -> d));

        List<OnetFlattenedRequirement> toSave = new ArrayList<>();
        toSave.addAll(assembler.fromTasks(tasks));
        toSave.addAll(assembler.fromDwa(mappings, dwaById));

        onetFlattenedRequirementRepository.saveAll(toSave);

        return FlattenOccupationJobResponse.builder()
                .occupationCode(occupationCode)
                .deletedCount(deleted)
                .insertedCount((long) toSave.size())
                .build();
    }

    private String toVectorLiteral(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(BigDecimal.valueOf(vector[i]).toPlainString());
        }
        sb.append(']');
        return sb.toString();
    }

    private boolean exportV4Migration() {
        List<OnetFlattenedRequirementEmbeddingRepository.EmbeddingExportRow> rows = requirementEmbeddingRepository.findAllForMigrationExport();
        Path filePath = Paths.get(System.getProperty("user.dir"), V4_MIGRATION_FILE);

        StringBuilder sql = new StringBuilder();
        sql.append("-- Auto-generated by backfill endpoint\n");
        sql.append("-- Snapshot of personalization.onet_flattened_requirement_embeddings\n\n");

        if (rows.isEmpty()) {
            sql.append("-- No rows to seed\n");
            sql.append("SELECT 1 WHERE FALSE;\n");
        } else {
            sql.append("insert into personalization.onet_flattened_requirement_embeddings (id, requirement_id, occupation_code, embedding) values\n");

            for (int i = 0; i < rows.size(); i++) {
                OnetFlattenedRequirementEmbeddingRepository.EmbeddingExportRow row = rows.get(i);
                sql.append("(gen_random_uuid(), '")
                        .append(row.getRequirementId())
                        .append("', ")
                        .append(row.getOccupationCode() == null ? "null" : "'" + row.getOccupationCode().replace("'", "''") + "'")
                        .append(", cast('")
                        .append(row.getEmbeddingText().replace("'", "''"))
                        .append("' as vector))");
                sql.append(i == rows.size() - 1 ? "\n" : ",\n");
            }

            sql.append("on conflict (requirement_id) do update set\n");
            sql.append("    occupation_code = excluded.occupation_code,\n");
            sql.append("    embedding = excluded.embedding;\n");
        }

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, sql.toString(), StandardCharsets.UTF_8);
            log.info("Generated migration snapshot at {} with {} rows", filePath, rows.size());
            return true;
        } catch (IOException e) {
            log.error("Failed to generate migration snapshot file {}", filePath, e);
            return false;
        }
    }
}
