package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "integration_channels")
public class IntegrationChannel {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "provider_id", nullable = false, unique = true, length = 50)
    private String providerId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "DISCONNECTED";

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "app_password", length = 255)
    private String appPassword;

    @Column(name = "client_id", length = 255)
    private String clientId;

    @Column(name = "client_secret", length = 255)
    private String clientSecret;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
