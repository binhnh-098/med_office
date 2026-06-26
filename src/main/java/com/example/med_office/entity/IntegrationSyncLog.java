package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "integration_sync_logs")
public class IntegrationSyncLog {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "account_name", nullable = false, length = 255)
    private String accountName;

    @Column(name = "action", nullable = false, length = 255)
    private String action;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "execution_time", nullable = false)
    private Integer executionTime;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
