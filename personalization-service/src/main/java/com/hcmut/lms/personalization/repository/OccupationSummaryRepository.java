package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.application.entity.OccupationData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface OccupationSummaryRepository extends Repository<OccupationData, String> {

    @Query("""
        SELECT COUNT(t) FROM TaskStatement t WHERE t.occupation.onetsocCode = :code
        """)
    long countTasks(@Param("code") String occupationCode);

    @Query("""
        SELECT COUNT(d) FROM DwaReference d WHERE d.dwaId IN (
            SELECT map.dwaId FROM TasksToDwa map WHERE map.onetsocCode = :code
        )
        """)
    long countDwa(@Param("code") String occupationCode);
}
