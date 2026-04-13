package com.example.med_office.repository;

import java.util.Optional;

import com.example.med_office.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    Optional<NguoiDung> findByTenDangNhapAndTrangThaiIgnoreCase(String tenDangNhap, String trangThai);
}
