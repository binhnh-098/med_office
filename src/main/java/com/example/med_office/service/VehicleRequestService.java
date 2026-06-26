package com.example.med_office.service;

import com.example.med_office.dto.VehicleRequestDTOs.*;
import org.springframework.data.domain.Page;

public interface VehicleRequestService {
    Page<VehicleRequestResponse> getMyRequests(
            String keyword,
            String status,
            String currentUsername,
            int page,
            int size
    );

    Page<VehicleRequestResponse> getPendingApprovals(
            String keyword,
            String currentUsername,
            int page,
            int size
    );

    VehicleRequestResponse getRequestDetail(String id, String currentUsername);

    VehicleRequestResponse createRequest(VehicleRequestUpsertRequest request, String currentUsername);

    VehicleRequestResponse updateRequest(String id, VehicleRequestUpsertRequest request, String currentUsername);

    VehicleRequestResponse submitRequest(String id, String currentUsername);

    VehicleRequestResponse approveRequest(String id, VehicleRequestApproveRequest request, String currentUsername);

    VehicleRequestResponse rejectRequest(String id, VehicleRequestRejectRequest request, String currentUsername);

    void deleteRequest(String id, String currentUsername);
}
