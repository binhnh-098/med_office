package com.example.med_office.repository;

import com.example.med_office.entity.CongVanDen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CongVanDenRepository extends JpaRepository<CongVanDen, Long>, JpaSpecificationExecutor<CongVanDen> {
    boolean existsBySoCongVanIgnoreCase(String soCongVan);
}
