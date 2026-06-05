package com.example.med_office.repository;

import com.example.med_office.entity.WarehouseInbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WarehouseInboundRepository extends JpaRepository<WarehouseInbound, String>, JpaSpecificationExecutor<WarehouseInbound> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);
}
