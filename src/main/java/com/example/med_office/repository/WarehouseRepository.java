package com.example.med_office.repository;

import com.example.med_office.entity.Warehouse;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WarehouseRepository extends JpaRepository<Warehouse, String>, JpaSpecificationExecutor<Warehouse> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    long countByStatusIgnoreCase(String status);

    List<Warehouse> findByStatusIgnoreCaseOrderByNameAsc(String status);

    List<Warehouse> findAllByOrderByNameAsc();

    List<Warehouse> findByIdInOrderByNameAsc(Iterable<String> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Warehouse w where w.id = :id")
    Optional<Warehouse> findByIdForUpdate(@Param("id") String id);

    @Query("select w.id from Warehouse w")
    Set<String> findAllIds();
}
