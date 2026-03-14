package com.hcmut.lms.personalization.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "onet_flattened_requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OnetFlattenedRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "onet_flattened_requirements_id", nullable = false)
    private UUID onetFlattenedRequirementsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onetsoc_code", referencedColumnName = "onetsoc_code", nullable = false)
    private OccupationData occupation;

    @Column(name = "element_type", length = 20, nullable = false)
    private String elementType;

    @Column(name = "original_id", length = 50)
    private String originalId;

    @Column(name = "content_text")
    private String contentText;

    @Column(name = "importance_score", precision = 5, scale = 2)
    private BigDecimal importanceScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
