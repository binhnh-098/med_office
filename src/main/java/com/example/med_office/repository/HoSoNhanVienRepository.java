package com.example.med_office.repository;

import com.example.med_office.entity.HoSoNhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface HoSoNhanVienRepository extends JpaRepository<HoSoNhanVien, String>, JpaSpecificationExecutor<HoSoNhanVien> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<HoSoNhanVien> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    boolean existsByNguoiDungId(String nguoiDungId);

    boolean existsByNguoiDungIdAndIdNot(String nguoiDungId, String id);

    Optional<HoSoNhanVien> findByNguoiDungId(String nguoiDungId);
}
