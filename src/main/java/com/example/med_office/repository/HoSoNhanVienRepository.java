package com.example.med_office.repository;

import com.example.med_office.entity.HoSoNhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface HoSoNhanVienRepository extends JpaRepository<HoSoNhanVien, Long>, JpaSpecificationExecutor<HoSoNhanVien> {
    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByNguoiDungId(Long nguoiDungId);

    boolean existsByNguoiDungIdAndIdNot(Long nguoiDungId, Long id);

    Optional<HoSoNhanVien> findByNguoiDungId(Long nguoiDungId);
}
