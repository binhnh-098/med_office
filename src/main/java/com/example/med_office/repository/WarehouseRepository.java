package com.example.med_office.repository;

import com.example.med_office.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface WarehouseRepository extends JpaRepository<Warehouse, String>, JpaSpecificationExecutor<Warehouse> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    long countByStatusIgnoreCase(String status);

    List<Warehouse> findByStatusIgnoreCaseOrderByNameAsc(String status);

    List<Warehouse> findAllByOrderByNameAsc();

    List<Warehouse> findByIdInOrderByNameAsc(Iterable<String> ids);

    @Query("select w.id from Warehouse w")
    Set<String> findAllIds();
}
