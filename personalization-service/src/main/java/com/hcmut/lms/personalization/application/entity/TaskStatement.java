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
@Table(name = "task_statements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaskStatement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "task_statements_id", nullable = false)
    private UUID taskStatementsId;

    @Column(name = "task_id", nullable = false, unique = true)
    private BigDecimal taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onetsoc_code", referencedColumnName = "onetsoc_code", nullable = false)
    private OccupationData occupation;

    @Column(name = "task", length = 1000, nullable = false)
    private String task;

    @Column(name = "task_type", length = 12)
    private String taskType;

    @Column(name = "incumbents_responding", precision = 4)
    private BigDecimal incumbentsResponding;

    @Column(name = "date_updated", nullable = false)
    private LocalDate dateUpdated;

    @Column(name = "domain_source", length = 30, nullable = false)
    private String domainSource;
}
