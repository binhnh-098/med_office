package com.example.med_office.repository;

import com.example.med_office.entity.AssetLiquidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetLiquidationRepository extends JpaRepository<AssetLiquidation, String> {

    @Query("SELECT l FROM AssetLiquidation l JOIN l.asset a WHERE " +
           "(:assetId IS NULL OR l.assetId = :assetId) " +
           "AND (:keyword IS NULL OR LOWER(a.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.documentNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.reason) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AssetLiquidation> searchLiquidations(
            @Param("assetId") String assetId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
