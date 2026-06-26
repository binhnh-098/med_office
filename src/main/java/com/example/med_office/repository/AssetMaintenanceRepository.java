package com.example.med_office.repository;

import com.example.med_office.entity.AssetMaintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetMaintenanceRepository extends JpaRepository<AssetMaintenance, String> {

    @Query("SELECT m FROM AssetMaintenance m JOIN m.asset a WHERE " +
           "(:assetId IS NULL OR m.assetId = :assetId) " +
           "AND (:status IS NULL OR m.status = :status) " +
           "AND (:keyword IS NULL OR LOWER(a.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.provider) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AssetMaintenance> searchMaintenances(
            @Param("assetId") String assetId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
