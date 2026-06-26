package com.example.med_office.service;

import com.example.med_office.dto.BusinessTripDTOs.*;
import org.springframework.data.domain.Page;

public interface BusinessTripService {
    Page<BusinessTripResponse> getMyTrips(
            String keyword,
            String status,
            String currentUsername,
            int page,
            int size
    );

    Page<BusinessTripResponse> getPendingApprovals(
            String keyword,
            String currentUsername,
            int page,
            int size
    );

    Page<BusinessTripResponse> getActiveTrips(
            String keyword,
            int page,
            int size
    );

    BusinessTripResponse getTripDetail(String id, String currentUsername);

    BusinessTripResponse createTrip(BusinessTripUpsertRequest request, String currentUsername);

    BusinessTripResponse updateTrip(String id, BusinessTripUpsertRequest request, String currentUsername);

    BusinessTripResponse submitTrip(String id, String currentUsername);

    BusinessTripResponse approveTrip(String id, String currentUsername);

    BusinessTripResponse rejectTrip(String id, BusinessTripRejectRequest request, String currentUsername);

    void deleteTrip(String id, String currentUsername);
}
