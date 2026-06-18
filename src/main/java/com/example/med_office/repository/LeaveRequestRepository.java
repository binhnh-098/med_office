package com.example.med_office.repository;

import com.example.med_office.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String>, JpaSpecificationExecutor<LeaveRequest> {
    List<LeaveRequest> findByHoSoNhanVienId(String hoSoNhanVienId);
    boolean existsByApproverId(String approverId);
    boolean existsByHoSoNhanVienIdAndApproverId(String hoSoNhanVienId, String approverId);
}
