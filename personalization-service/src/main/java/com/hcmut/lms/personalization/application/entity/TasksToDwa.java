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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks_to_dwas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TasksToDwa extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tasks_to_dwas_id", nullable = false)
    private UUID tasksToDwasId;

    @Column(name = "onetsoc_code", length = 10, nullable = false)
    private String onetsocCode;

    @Column(name = "task_id", nullable = false)
    private BigDecimal taskId;

    @Column(name = "dwa_id", length = 20, nullable = false)
    private String dwaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dwa_id", referencedColumnName = "dwa_id", insertable = false, updatable = false)
    private DwaReference dwaReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onetsoc_code", referencedColumnName = "onetsoc_code", insertable = false, updatable = false)
    private OccupationData occupation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "task_id", insertable = false, updatable = false)
    private TaskStatement taskStatement;

    @Column(name = "date_updated", nullable = false)
    private LocalDate dateUpdated;

    @Column(name = "domain_source", length = 30, nullable = false)
    private String domainSource;
}
