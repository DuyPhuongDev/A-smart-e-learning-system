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

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "scales_reference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ScalesReference extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "scales_reference_id", nullable = false)
    private UUID scalesReferenceId;

    @Column(name = "scale_id", length = 3, nullable = false, unique = true)
    private String scaleId;

    @Column(name = "scale_name", length = 50, nullable = false)
    private String scaleName;

    @Column(name = "minimum", precision = 3, scale = 0, nullable = false)
    private BigDecimal minimum;

    @Column(name = "maximum", precision = 3, scale = 0, nullable = false)
    private BigDecimal maximum;
}
