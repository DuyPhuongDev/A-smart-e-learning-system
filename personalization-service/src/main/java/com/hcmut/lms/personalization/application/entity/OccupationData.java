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

import java.util.UUID;

@Entity
@Table(name = "occupation_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OccupationData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "occupation_data_id", nullable = false)
    private UUID occupationDataId;

    @Column(name = "onetsoc_code", length = 10, nullable = false, unique = true)
    private String onetsocCode;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;
}
