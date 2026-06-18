package com.example.med_office.repository;

import com.example.med_office.entity.WarehouseInbound;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WarehouseInboundRepository extends JpaRepository<WarehouseInbound, String>, JpaSpecificationExecutor<WarehouseInbound> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select inbound from WarehouseInbound inbound where inbound.id = :id")
    Optional<WarehouseInbound> findByIdForUpdate(@Param("id") String id);

    @Query("select max(i.code) from WarehouseInbound i where i.code like :prefix%")
    String findMaxCodeByPrefix(@Param("prefix") String prefix);
}

