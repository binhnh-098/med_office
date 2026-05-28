package com.example.med_office.repository;

import com.example.med_office.entity.ChuyenKhoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChuyenKhoaRepository extends JpaRepository<ChuyenKhoa, String> {

    List<ChuyenKhoa> findByUserIdOrderByTenChuyenKhoaAscIdChuyenKhoaAsc(String userId);

    boolean existsByUserIdAndTenChuyenKhoaIgnoreCase(String userId, String tenChuyenKhoa);

    boolean existsByUserIdAndTenChuyenKhoaIgnoreCaseAndIdChuyenKhoaNot(
            String userId,
            String tenChuyenKhoa,
            String idChuyenKhoa
    );

    Optional<ChuyenKhoa> findByTenChuyenKhoaIgnoreCase(String tenChuyenKhoa);
}
