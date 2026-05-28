package com.example.med_office.repository;

import com.example.med_office.entity.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChucVuRepository extends JpaRepository<ChucVu, String> {

    List<ChucVu> findByUserIdOrderByTenChucVuAscIdAsc(String userId);

    boolean existsByUserIdAndMaChucVuIgnoreCase(String userId, String maChucVu);

    boolean existsByUserIdAndMaChucVuIgnoreCaseAndIdNot(String userId, String maChucVu, String id);

    Optional<ChucVu> findByMaChucVuIgnoreCase(String maChucVu);

    Optional<ChucVu> findByTenChucVuIgnoreCase(String tenChucVu);
}
