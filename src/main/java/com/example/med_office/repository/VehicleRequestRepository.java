package com.example.med_office.repository;

import com.example.med_office.entity.VehicleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRequestRepository extends JpaRepository<VehicleRequest, String> {

    @Query("SELECT v FROM VehicleRequest v WHERE v.hoSoNhanVienId = :employeeId " +
           "AND (:status IS NULL OR v.status = :status) " +
           "AND (:keyword IS NULL OR LOWER(v.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.routeDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.vehicleType) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<VehicleRequest> searchMyRequests(
            @Param("employeeId") String employeeId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT v FROM VehicleRequest v WHERE v.approverId = :approverId " +
           "AND v.status = 'PENDING_APPROVAL' " +
           "AND (:keyword IS NULL OR LOWER(v.routeDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<VehicleRequest> searchApprovals(
            @Param("approverId") String approverId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT v FROM VehicleRequest v WHERE v.vehicleId = :vehicleId " +
           "AND v.status IN ('PENDING_APPROVAL', 'APPROVED') " +
           "AND (:excludeRequestId IS NULL OR v.id <> :excludeRequestId) " +
           "AND v.departureTime < :returnTime AND v.returnTime > :departureTime")
    java.util.List<VehicleRequest> findOverlappingRequests(
            @Param("vehicleId") String vehicleId,
            @Param("departureTime") java.time.LocalDateTime departureTime,
            @Param("returnTime") java.time.LocalDateTime returnTime,
            @Param("excludeRequestId") String excludeRequestId
    );
}
