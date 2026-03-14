package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.application.entity.DwaReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DwaReferenceRepository extends JpaRepository<DwaReference, java.util.UUID> {
    java.util.Optional<DwaReference> findByDwaId(String dwaId);
}
