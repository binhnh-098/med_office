package com.example.med_office.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class LeaveRequestDTOs {

    private LeaveRequestDTOs() {}

    public record LeaveRequestUpsertRequest(
            String leaveType,
            LocalDate startDate,
            LocalDate endDate,
            Double totalDays,
            String reason,
            String approverId,
            String handoverEmployeeId
    ) {}

    public record LeaveRequestResponse(
            String id,
            String hoSoNhanVienId,
            String employeeName,
            String employeeCode,
            String leaveType,
            LocalDate startDate,
            LocalDate endDate,
            Double totalDays,
            String reason,
            String approverId,
            String approverName,
            String handoverEmployeeId,
            String handoverEmployeeName,
            String status,
            String rejectReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record LeaveBalanceResponse(
            Double annualLeaveTotal,
            Double annualLeaveUsed,
            Double annualLeaveRemaining,
            Boolean hasSubordinates
    ) {}

    public record LeaveRequestRejectRequest(
            String rejectReason
    ) {}
}
