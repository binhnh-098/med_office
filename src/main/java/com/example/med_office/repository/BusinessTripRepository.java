package com.example.med_office.repository;

import com.example.med_office.entity.BusinessTrip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusinessTripRepository extends JpaRepository<BusinessTrip, String> {

    Page<BusinessTrip> findByHoSoNhanVienId(String hoSoNhanVienId, Pageable pageable);

    @Query("SELECT b FROM BusinessTrip b WHERE b.hoSoNhanVienId = :employeeId " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:keyword IS NULL OR LOWER(b.destination) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BusinessTrip> searchMyTrips(
            @Param("employeeId") String employeeId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT b FROM BusinessTrip b WHERE b.approverId = :approverId " +
           "AND b.status = 'PENDING_APPROVAL' " +
           "AND (:keyword IS NULL OR LOWER(b.destination) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BusinessTrip> searchApprovals(
            @Param("approverId") String approverId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Dynamic list of active trips (approved and overlaps with current date)
    @Query("SELECT b FROM BusinessTrip b WHERE b.status = 'APPROVED' " +
           "AND :currentDate BETWEEN b.startDate AND b.endDate " +
           "AND (:keyword IS NULL OR LOWER(b.destination) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BusinessTrip> searchActiveTrips(
            @Param("currentDate") LocalDate currentDate,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
