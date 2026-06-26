package com.example.med_office.repository;

import com.example.med_office.entity.IntegrationSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IntegrationSyncLogRepository extends JpaRepository<IntegrationSyncLog, String>, JpaSpecificationExecutor<IntegrationSyncLog> {
}
