package com.hcmut.lms.personalization.domain.entity.occupationData;

import com.hcmut.lms.personalization.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "dwa_reference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DwaReference extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "dwa_reference_id", nullable = false)
  private UUID dwaReferenceId;

  @Column(name = "dwa_id", length = 20, nullable = false, unique = true)
  private String dwaId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "iwa_id", referencedColumnName = "iwa_id", nullable = false)
  private IwaReference iwaReference;

  @Column(name = "element_id", length = 20, nullable = false)
  private String elementId;

  @Column(name = "element_name", length = 150, nullable = false)
  private String elementName;

  @Column(name = "element_description", length = 1500, nullable = false)
  private String elementDescription;

  @Column(name = "dwa_title", length = 150, nullable = false)
  private String dwaTitle;
}
