package com.example.med_office.repository;

import com.example.med_office.entity.AssetInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetInventoryRepository extends JpaRepository<AssetInventory, String> {

    Optional<AssetInventory> findByDocumentNumber(String documentNumber);

    @Query("SELECT i FROM AssetInventory i WHERE " +
           "(:status IS NULL OR i.status = :status) " +
           "AND (:keyword IS NULL OR LOWER(i.documentNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.notes) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AssetInventory> searchInventories(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
