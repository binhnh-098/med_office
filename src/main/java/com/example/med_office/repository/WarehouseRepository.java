package com.example.med_office.repository;

import com.example.med_office.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, String>, JpaSpecificationExecutor<Warehouse> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    long countByStatusIgnoreCase(String status);

    List<Warehouse> findByStatusIgnoreCaseOrderByNameAsc(String status);

    List<Warehouse> findAllByOrderByNameAsc();

    List<Warehouse> findByIdInOrderByNameAsc(Iterable<String> ids);
}
