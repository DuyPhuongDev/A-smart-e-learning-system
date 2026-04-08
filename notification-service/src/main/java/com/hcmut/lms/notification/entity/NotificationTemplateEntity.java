package com.hcmut.lms.notification.entity;

import com.hcmut.lms.notification.enums.NotificationChannel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_templates", schema = "notification")
public class NotificationTemplateEntity extends AuditableEntity {

    @Id
    @Column(name = "code", length = 128)
    private String code;

    @Column(name = "title_template", nullable = false, columnDefinition = "text")
    private String titleTemplate;

    @Column(name = "content_template", nullable = false, columnDefinition = "text")
    private String contentTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private NotificationChannel channel;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "updated_by")
    private UUID updatedBy;
}
