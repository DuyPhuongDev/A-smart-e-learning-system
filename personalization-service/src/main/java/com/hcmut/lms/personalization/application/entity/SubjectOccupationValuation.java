package com.hcmut.lms.personalization.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "subject_occupation_valuations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubjectOccupationValuation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @Column(name = "subject_code", length = 50)
    private String subjectCode;

    @Column(name = "subject_name", length = 255)
    private String subjectName;

    @Column(name = "target_occupation_code", length = 10, nullable = false)
    private String targetOccupationCode;

    @Column(name = "target_occupation_title", length = 255)
    private String targetOccupationTitle;

    @Column(name = "total_value", precision = 18, scale = 4, nullable = false)
    private BigDecimal totalValue;

    @Column(name = "value_density", precision = 18, scale = 4, nullable = false)
    private BigDecimal valueDensity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details")
    private Map<String, Object> details;
}
