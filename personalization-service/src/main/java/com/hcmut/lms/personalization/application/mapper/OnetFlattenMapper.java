package com.hcmut.lms.personalization.application.mapper;

import com.hcmut.lms.personalization.application.dto.response.BackfillRequirementEmbeddingsResponse;
import com.hcmut.lms.personalization.application.dto.response.FlattenOccupationJobResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OnetFlattenMapper {

  default FlattenOccupationJobResponse toFlattenOccupationJobResponse(
      String occupationCode, long deletedCount,
      long insertedCount) {
    return FlattenOccupationJobResponse.builder()
        .occupationCode(occupationCode)
        .deletedCount(deletedCount)
        .insertedCount(insertedCount)
        .build();
  }

  default BackfillRequirementEmbeddingsResponse toBackfillResponse(
      long missingCount, long embeddedCount,
      boolean migrationGenerated, String migrationFile) {
    return BackfillRequirementEmbeddingsResponse.builder()
        .missingCount(missingCount)
        .embeddedCount(embeddedCount)
        .migrationGenerated(migrationGenerated)
        .migrationFile(migrationFile)
        .build();
  }
}

