package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "ho_so_nhan_vien_id", nullable = false, length = 36)
    private String hoSoNhanVienId;

    @Column(name = "ten_nhan_vien", nullable = false, length = 255)
    private String employeeName;

    @Column(name = "ma_nhan_vien", nullable = false, length = 50)
    private String employeeCode;

    @Column(name = "loai_nghi_phep", nullable = false, length = 50)
    private String leaveType; // ANNUAL, SICK, UNPAID, MATERNITY, PERSONAL

    @Column(name = "tu_ngay", nullable = false)
    private LocalDate startDate;

    @Column(name = "den_ngay", nullable = false)
    private LocalDate endDate;

    @Column(name = "tong_so_ngay", nullable = false)
    private Double totalDays;

    @Column(name = "ly_do", nullable = false, length = 1000)
    private String reason;

    @Column(name = "nguoi_duyet_id", length = 36)
    private String approverId;

    @Column(name = "ten_nguoi_duyet", length = 255)
    private String approverName;

    @Column(name = "nguoi_ban_giao_id", length = 36)
    private String handoverEmployeeId;

    @Column(name = "ten_nguoi_ban_giao", length = 255)
    private String handoverEmployeeName;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String status; // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED

    @Column(name = "buoi_nghi", length = 50)
    private String halfDaySession; // MORNING, AFTERNOON

    @Column(name = "ly_do_tu_choi", length = 1000)
    private String rejectReason;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = "DRAFT";
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
