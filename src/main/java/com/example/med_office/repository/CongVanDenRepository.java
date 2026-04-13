package com.example.med_office.repository;

import com.example.med_office.entity.CongVanDen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CongVanDenRepository extends JpaRepository<CongVanDen, Long> {
    boolean existsBySoCongVanIgnoreCase(String soCongVan);
}
