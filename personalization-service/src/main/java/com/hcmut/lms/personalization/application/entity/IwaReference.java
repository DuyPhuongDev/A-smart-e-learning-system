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
@Table(name = "iwa_reference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IwaReference extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "iwa_reference_id", nullable = false)
    private UUID iwaReferenceId;

    @Column(name = "iwa_id", length = 20, nullable = false, unique = true)
    private String iwaId;

    @Column(name = "element_id", length = 20, nullable = false)
    private String elementId;

    @Column(name = "element_name", length = 150, nullable = false)
    private String elementName;

    @Column(name = "element_description", length = 1500, nullable = false)
    private String elementDescription;

    @Column(name = "iwa_title", length = 150, nullable = false)
    private String iwaTitle;
}
