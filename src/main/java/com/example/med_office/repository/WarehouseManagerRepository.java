package com.example.med_office.repository;

import com.example.med_office.entity.WarehouseManager;
import com.example.med_office.entity.WarehouseManagerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface WarehouseManagerRepository extends JpaRepository<WarehouseManager, WarehouseManagerId> {

    List<WarehouseManager> findByIdWarehouseId(String warehouseId);

    List<WarehouseManager> findByIdWarehouseIdIn(Collection<String> warehouseIds);

    void deleteByIdWarehouseId(String warehouseId);
}
