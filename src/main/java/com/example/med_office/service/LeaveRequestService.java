package com.example.med_office.service;

import com.example.med_office.dto.LeaveRequestDTOs.*;
import org.springframework.data.domain.Page;

public interface LeaveRequestService {
    Page<LeaveRequestResponse> getLeaveRequests(
            String keyword,
            String status,
            String employeeId,
            String currentUsername,
            int page,
            int size
    );

    LeaveRequestResponse getLeaveRequestDetail(String id, String currentUsername);

    LeaveRequestResponse createLeaveRequest(LeaveRequestUpsertRequest request, String currentUsername);

    LeaveRequestResponse updateLeaveRequest(String id, LeaveRequestUpsertRequest request, String currentUsername);

    LeaveRequestResponse submitLeaveRequest(String id, String currentUsername);

    LeaveRequestResponse approveLeaveRequest(String id, String currentUsername);

    LeaveRequestResponse rejectLeaveRequest(String id, LeaveRequestRejectRequest request, String currentUsername);

    void deleteLeaveRequest(String id, String currentUsername);

    LeaveBalanceResponse getLeaveBalance(String currentUsername);
}
