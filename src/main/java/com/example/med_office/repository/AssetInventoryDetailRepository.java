package com.example.med_office.repository;

import com.example.med_office.entity.AssetInventoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetInventoryDetailRepository extends JpaRepository<AssetInventoryDetail, String> {
    List<AssetInventoryDetail> findByInventoryId(String inventoryId);
}
