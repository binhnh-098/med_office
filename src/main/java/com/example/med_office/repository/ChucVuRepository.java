package com.example.med_office.repository;

import com.example.med_office.entity.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChucVuRepository extends JpaRepository<ChucVu, String> {

    boolean existsByMaChucVuIgnoreCase(String maChucVu);

    boolean existsByMaChucVuIgnoreCaseAndIdNot(String maChucVu, String id);

    Optional<ChucVu> findByMaChucVuIgnoreCase(String maChucVu);

    Optional<ChucVu> findByTenChucVuIgnoreCase(String tenChucVu);
}
