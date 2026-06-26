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
            String handoverEmployeeId,
            String halfDaySession
    ) {
        public LeaveRequestUpsertRequest(
                String leaveType,
                LocalDate startDate,
                LocalDate endDate,
                Double totalDays,
                String reason,
                String approverId,
                String handoverEmployeeId
        ) {
            this(leaveType, startDate, endDate, totalDays, reason, approverId, handoverEmployeeId, null);
        }
    }

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
            String halfDaySession,
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
