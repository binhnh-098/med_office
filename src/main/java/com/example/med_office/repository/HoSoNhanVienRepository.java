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

    @Query("""
            select hoSoNhanVien
            from HoSoNhanVien hoSoNhanVien
            where (:excludeId is null or hoSoNhanVien.id <> :excludeId)
              and (:keyword is null
                   or lower(hoSoNhanVien.code) like lower(concat('%', :keyword, '%'))
                   or lower(hoSoNhanVien.name) like lower(concat('%', :keyword, '%')))
            order by
              case when hoSoNhanVien.active = true then 0 else 1 end,
              hoSoNhanVien.name asc,
              hoSoNhanVien.code asc
            """)
    List<HoSoNhanVien> findDirectManagerOptions(
            @Param("keyword") String keyword,
            @Param("excludeId") String excludeId,
            org.springframework.data.domain.Pageable pageable
    );
}
