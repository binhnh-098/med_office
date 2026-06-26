package com.example.med_office.repository;

import com.example.med_office.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String>, JpaSpecificationExecutor<LeaveRequest> {
    List<LeaveRequest> findByHoSoNhanVienId(String hoSoNhanVienId);
    boolean existsByApproverId(String approverId);
    boolean existsByHoSoNhanVienIdAndApproverId(String hoSoNhanVienId, String approverId);

    @Query("SELECT r FROM LeaveRequest r " +
           "WHERE r.hoSoNhanVienId = :employeeId " +
           "AND r.startDate <= :endDate " +
           "AND r.endDate >= :startDate " +
           "AND r.status != 'REJECTED'")
    List<LeaveRequest> findOverlappingRequests(
            @Param("employeeId") String employeeId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );

    @Query("SELECT SUM(r.totalDays) FROM LeaveRequest r " +
           "WHERE r.hoSoNhanVienId = :employeeId " +
           "AND r.leaveType = 'ANNUAL' " +
           "AND r.status = 'APPROVED' " +
           "AND r.endDate <= :date")
    Double sumApprovedAnnualLeaveDaysBeforeDate(
            @Param("employeeId") String employeeId,
            @Param("date") java.time.LocalDate date
    );
}
