package com.example.med_office.service;

import com.example.med_office.dto.VehicleDTOs.VehicleResponse;
import com.example.med_office.dto.VehicleDTOs.VehicleUpsertRequest;
import com.example.med_office.entity.Vehicle;
import com.example.med_office.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> getVehicles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Vehicle> vehicles;
        if (keyword != null && !keyword.trim().isEmpty()) {
            vehicles = vehicleRepository.search(keyword.trim(), pageable);
        } else {
            vehicles = vehicleRepository.findAll(pageable);
        }
        return vehicles.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleDetail(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + id));
        return toResponse(vehicle);
    }

    @Override
    public VehicleResponse createVehicle(VehicleUpsertRequest request) {
        Vehicle vehicle = new Vehicle();
        copyProperties(request, vehicle);
        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    @Override
    public VehicleResponse updateVehicle(String id, VehicleUpsertRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + id));
        copyProperties(request, vehicle);
        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    @Override
    public void deleteVehicle(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + id));
        vehicleRepository.delete(vehicle);
    }

    private void copyProperties(VehicleUpsertRequest src, Vehicle target) {
        target.setName(src.name());
        target.setLicensePlate(src.licensePlate());
        target.setDriverName(src.driverName());
        target.setDriverPhone(src.driverPhone());
        target.setSeatCapacity(src.seatCapacity());
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getLicensePlate(),
                vehicle.getDriverName(),
                vehicle.getDriverPhone(),
                vehicle.getSeatCapacity(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}
