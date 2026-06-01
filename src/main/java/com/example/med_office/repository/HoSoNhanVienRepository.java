package com.example.med_office.repository;

import com.example.med_office.entity.HoSoNhanVien;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HoSoNhanVienRepository extends JpaRepository<HoSoNhanVien, String>, JpaSpecificationExecutor<HoSoNhanVien> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<HoSoNhanVien> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, String id);

    boolean existsByNguoiDungId(String nguoiDungId);

    boolean existsByNguoiDungIdAndIdNot(String nguoiDungId, String id);

    Optional<HoSoNhanVien> findByNguoiDungId(String nguoiDungId);

    List<HoSoNhanVien> findByNguoiDungIdIn(Collection<String> nguoiDungIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select hoSoNhanVien from HoSoNhanVien hoSoNhanVien where hoSoNhanVien.id = :id")
    Optional<HoSoNhanVien> findByIdForUpdate(@Param("id") String id);
}
