package com.example.med_office.repository;

import com.example.med_office.entity.AssetHandover;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetHandoverRepository extends JpaRepository<AssetHandover, String> {

    @Query("SELECT h FROM AssetHandover h JOIN h.asset a WHERE " +
           "(:assetId IS NULL OR h.assetId = :assetId) " +
           "AND (:type IS NULL OR h.type = :type) " +
           "AND (:keyword IS NULL OR LOWER(a.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(h.documentNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AssetHandover> searchHandovers(
            @Param("assetId") String assetId,
            @Param("type") String type,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
