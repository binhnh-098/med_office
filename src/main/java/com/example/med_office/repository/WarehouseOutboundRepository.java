package com.example.med_office.repository;

import com.example.med_office.entity.WarehouseOutbound;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WarehouseOutboundRepository extends JpaRepository<WarehouseOutbound, String>, JpaSpecificationExecutor<WarehouseOutbound> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select outbound from WarehouseOutbound outbound where outbound.id = :id")
    Optional<WarehouseOutbound> findByIdForUpdate(@Param("id") String id);

    @Query("select max(o.code) from WarehouseOutbound o where o.code like :prefix%")
    String findMaxCodeByPrefix(@Param("prefix") String prefix);
}
