package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.occupationData.OnetFlattenedRequirementEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface OnetFlattenedRequirementEmbeddingRepository extends JpaRepository<OnetFlattenedRequirementEmbedding,
    UUID> {

  interface EmbeddingExportRow {
    UUID getRequirementId();

    String getOccupationCode();

    String getEmbeddingText();
  }

  interface ValuationSummaryRow {
    BigDecimal getTotalScore();

    Long getMatchCount();
  }

  interface ValuationTopMatchRow {
    UUID getRequirementId();

    String getRequirementText();

    String getElementType();

    BigDecimal getImportanceScore();

    Double getSimilarityScore();

    Double getWeightedScore();
  }

  @Query("""
      select e.requirementId
      from OnetFlattenedRequirementEmbedding e
      where e.requirementId in :requirementIds
      """)
  List<UUID> findExistingRequirementIds(@Param("requirementIds") Collection<UUID> requirementIds);

  @Modifying
  @Transactional
  @Query(value = """
      insert into personalization.onet_flattened_requirement_embeddings
          (id, requirement_id, occupation_code, embedding)
      values
          (gen_random_uuid(), :requirementId, :occupationCode, cast(:embeddingLiteral as vector))
      on conflict (requirement_id)
      do update set
          occupation_code = excluded.occupation_code,
          embedding = excluded.embedding
      """, nativeQuery = true)
  void upsertEmbedding(
      @Param("requirementId") UUID requirementId, @Param("occupationCode") String occupationCode,
      @Param("embeddingLiteral") String embeddingLiteral);

  @Query(value = """
      select
          requirement_id as requirementId,
          occupation_code as occupationCode,
          embedding::text as embeddingText
      from personalization.onet_flattened_requirement_embeddings
      order by occupation_code, requirement_id
      """, nativeQuery = true)
  List<EmbeddingExportRow> findAllForMigrationExport();

  @Query(value = """
      with subject_slos as (
          select cast(unnest(cast(:sloVectorLiterals as text[])) as vector) as slo_vector
      ),
      scores as (
          select
              (1 - (re.embedding <=> s.slo_vector)) as similarity,
              coalesce(ofr.importance_score, 0) as importance_score
          from subject_slos s
          join personalization.onet_flattened_requirement_embeddings re
              on re.occupation_code = :occCode
          join personalization.onet_flattened_requirements ofr
              on ofr.onet_flattened_requirements_id = re.requirement_id
      )
      select
          coalesce(sum(similarity * importance_score), 0) as totalScore,
          count(*) as matchCount
      from scores
      where similarity >= :threshold
      """, nativeQuery = true)
  ValuationSummaryRow computeValuationSummary(
      @Param("sloVectorLiterals") String[] sloVectorLiterals, @Param("occCode") String occupationCode,
      @Param("threshold") double threshold);

  @Query(value = """
      with subject_slos as (
          select cast(unnest(cast(:sloVectorLiterals as text[])) as vector) as slo_vector
      ),
      per_requirement as (
          select
              re.requirement_id as requirementId,
              ofr.content_text as requirementText,
              ofr.element_type as elementType,
              ofr.importance_score as importanceScore,
              max(1 - (re.embedding <=> s.slo_vector)) as similarityScore
          from subject_slos s
          join personalization.onet_flattened_requirement_embeddings re
              on re.occupation_code = :occCode
          join personalization.onet_flattened_requirements ofr
              on ofr.onet_flattened_requirements_id = re.requirement_id
          group by re.requirement_id, ofr.content_text, ofr.element_type, ofr.importance_score
      )
      select
          requirementId,
          requirementText,
          elementType,
          importanceScore,
          similarityScore,
          (coalesce(importanceScore, 0) * similarityScore) as weightedScore
      from per_requirement
      where similarityScore >= :threshold
      order by weightedScore desc, requirementId
      limit :topK
      """, nativeQuery = true)
  List<ValuationTopMatchRow> findTopMatches(
      @Param("sloVectorLiterals") String[] sloVectorLiterals, @Param("occCode") String occupationCode,
      @Param("threshold") double threshold, @Param("topK") int topK);
}

