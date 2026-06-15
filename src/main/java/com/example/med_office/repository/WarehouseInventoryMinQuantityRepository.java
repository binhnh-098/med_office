package com.example.med_office.repository;

import com.example.med_office.entity.WarehouseInventoryMinQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WarehouseInventoryMinQuantityRepository extends JpaRepository<WarehouseInventoryMinQuantity, String> {

    Optional<WarehouseInventoryMinQuantity> findByInventoryKey(String inventoryKey);

    List<WarehouseInventoryMinQuantity> findByInventoryKeyIn(Collection<String> inventoryKeys);
}
