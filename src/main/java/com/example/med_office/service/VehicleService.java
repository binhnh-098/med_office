package com.example.med_office.service;

import com.example.med_office.dto.VehicleDTOs.VehicleResponse;
import com.example.med_office.dto.VehicleDTOs.VehicleUpsertRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VehicleService {
    Page<VehicleResponse> getVehicles(String keyword, int page, int size);
    List<VehicleResponse> getAllVehicles();
    VehicleResponse getVehicleDetail(String id);
    VehicleResponse createVehicle(VehicleUpsertRequest request);
    VehicleResponse updateVehicle(String id, VehicleUpsertRequest request);
    void deleteVehicle(String id);
}
