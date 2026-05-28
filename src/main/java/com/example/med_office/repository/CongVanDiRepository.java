package com.example.med_office.repository;

import com.example.med_office.entity.CongVanDi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CongVanDiRepository extends JpaRepository<CongVanDi, String>, JpaSpecificationExecutor<CongVanDi> {
}
