package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.application.entity.OnetFlattenedRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OnetFlattenedRequirementRepository extends JpaRepository<OnetFlattenedRequirement, java.util.UUID> {

    @Query("""
            select r
            from OnetFlattenedRequirement r
            join fetch r.occupation o
            where o.onetsocCode in :occupationCodes
            """)
    List<OnetFlattenedRequirement> findByOccupation_OnetsocCodeIn(@Param("occupationCodes") Collection<String> occupationCodes);

    @Query("""
            select r
            from OnetFlattenedRequirement r
            join fetch r.occupation o
            where r.contentText is not null
              and trim(r.contentText) <> ''
              and not exists (
                  select 1
                  from OnetFlattenedRequirementEmbedding e
                  where e.requirementId = r.onetFlattenedRequirementsId
              )
            order by o.onetsocCode, r.onetFlattenedRequirementsId
            """)
    List<OnetFlattenedRequirement> findMissingEmbeddings(Pageable pageable);

    @Query("""
            select r
            from OnetFlattenedRequirement r
            join fetch r.occupation o
            where o.onetsocCode in :occupationCodes
              and r.contentText is not null
              and trim(r.contentText) <> ''
              and not exists (
                  select 1
                  from OnetFlattenedRequirementEmbedding e
                  where e.requirementId = r.onetFlattenedRequirementsId
              )
            order by o.onetsocCode, r.onetFlattenedRequirementsId
            """)
    List<OnetFlattenedRequirement> findMissingEmbeddingsByOccupationCodes(@Param("occupationCodes") Collection<String> occupationCodes,
                                                                          Pageable pageable);

    long deleteByOccupation_OnetsocCode(String occupationCode);

    default long deleteByOccupation_OnetsocCodeReturningCount(String occupationCode) {
        return deleteByOccupation_OnetsocCode(occupationCode);
    }
}
