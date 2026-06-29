package com.example.med_office.repository;

import com.example.med_office.entity.SalesOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, String>, JpaSpecificationExecutor<SalesOrder> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select so from SalesOrder so where so.id = :id")
    Optional<SalesOrder> findByIdForUpdate(@Param("id") String id);

    @Query("select max(so.code) from SalesOrder so where so.code like :prefix%")
    String findMaxCodeByPrefix(@Param("prefix") String prefix);
}
