package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.application.entity.OccupationData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OccupationDataRepository extends JpaRepository<OccupationData, UUID> {
    Page<OccupationData> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<OccupationData> findByOnetsocCode(String onetsocCode);

    List<OccupationData> findByOnetsocCodeIn(List<String> onetsocCodes);
}
