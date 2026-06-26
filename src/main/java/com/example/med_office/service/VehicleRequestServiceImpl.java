package com.example.med_office.service;

import com.example.med_office.dto.VehicleRequestDTOs.*;
import com.example.med_office.entity.ChuyenKhoa;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.entity.Vehicle;
import com.example.med_office.entity.VehicleRequest;
import com.example.med_office.repository.ChuyenKhoaRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.VehicleRepository;
import com.example.med_office.repository.VehicleRequestRepository;
import com.example.med_office.security.PermissionCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class VehicleRequestServiceImpl implements VehicleRequestService {

    private final VehicleRequestRepository vehicleRequestRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ChuyenKhoaRepository chuyenKhoaRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleRequestServiceImpl(
            VehicleRequestRepository vehicleRequestRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            NguoiDungRepository nguoiDungRepository,
            ChuyenKhoaRepository chuyenKhoaRepository,
            VehicleRepository vehicleRepository
    ) {
        this.vehicleRequestRepository = vehicleRequestRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.chuyenKhoaRepository = chuyenKhoaRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleRequestResponse> getMyRequests(
            String keyword,
            String status,
            String currentUsername,
            int page,
            int size
    ) {
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;
        String cleanStatus = (status == null || status.isBlank()) ? null : status;

        return vehicleRequestRepository.searchMyRequests(employee.getId(), cleanStatus, cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleRequestResponse> getPendingApprovals(
            String keyword,
            String currentUsername,
            int page,
            int size
    ) {
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return vehicleRequestRepository.searchApprovals(manager.getId(), cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleRequestResponse getRequestDetail(String id, String currentUsername) {
        VehicleRequest request = vehicleRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu xe."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        boolean isOwner = request.getHoSoNhanVienId().equals(employee.getId());
        boolean isApprover = employee.getId().equals(request.getApproverId());
        boolean isHrOrAdmin = hasManageAuthority();

        if (!isOwner && !isApprover && !isHrOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem thông tin yêu cầu xe này.");
        }

        return toResponse(request);
    }

    @Override
    @Transactional
    public VehicleRequestResponse createRequest(VehicleRequestUpsertRequest request, String currentUsername) {
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (request.departureTime().isAfter(request.returnTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thời gian xuất phát không được sau thời gian về.");
        }

        validatePassengerCapacity(request.vehicleId(), request.departureTime(), request.returnTime(), request.passengerCount(), null);

        VehicleRequest vehicleRequest = new VehicleRequest();
        vehicleRequest.setHoSoNhanVienId(employee.getId());
        vehicleRequest.setVehicleType(request.vehicleType());
        vehicleRequest.setDepartureTime(request.departureTime());
        vehicleRequest.setReturnTime(request.returnTime());
        vehicleRequest.setRouteDescription(request.routeDescription());
        vehicleRequest.setPassengerCount(request.passengerCount());
        vehicleRequest.setPurpose(request.purpose());
        vehicleRequest.setStatus("DRAFT");

        if (request.vehicleId() != null && !request.vehicleId().isBlank()) {
            Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy xe được chọn."));
            vehicleRequest.setVehicleId(vehicle.getId());
            vehicleRequest.setDriverName(vehicle.getDriverName());
            vehicleRequest.setDriverPhone(vehicle.getDriverPhone());
            vehicleRequest.setLicensePlate(vehicle.getLicensePlate());
        } else {
            vehicleRequest.setVehicleId(null);
        }

        if (request.approverId() != null && !request.approverId().isBlank()) {
            HoSoNhanVien approver = hoSoNhanVienRepository.findById(request.approverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người duyệt."));
            vehicleRequest.setApproverId(approver.getId());
        }

        VehicleRequest saved = vehicleRequestRepository.save(vehicleRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleRequestResponse updateRequest(String id, VehicleRequestUpsertRequest request, String currentUsername) {
        VehicleRequest vehicleRequest = vehicleRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu xe."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!vehicleRequest.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật yêu cầu xe này.");
        }

        if (!"DRAFT".equals(vehicleRequest.getStatus()) && !"REJECTED".equals(vehicleRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được cập nhật yêu cầu xe ở trạng thái Nháp hoặc Từ chối.");
        }

        if (request.departureTime().isAfter(request.returnTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thời gian xuất phát không được sau thời gian về.");
        }

        validatePassengerCapacity(request.vehicleId(), request.departureTime(), request.returnTime(), request.passengerCount(), id);

        vehicleRequest.setVehicleType(request.vehicleType());
        vehicleRequest.setDepartureTime(request.departureTime());
        vehicleRequest.setReturnTime(request.returnTime());
        vehicleRequest.setRouteDescription(request.routeDescription());
        vehicleRequest.setPassengerCount(request.passengerCount());
        vehicleRequest.setPurpose(request.purpose());
        vehicleRequest.setStatus("DRAFT");
        vehicleRequest.setRejectReason(null);

        if (request.vehicleId() != null && !request.vehicleId().isBlank()) {
            Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy xe được chọn."));
            vehicleRequest.setVehicleId(vehicle.getId());
            vehicleRequest.setDriverName(vehicle.getDriverName());
            vehicleRequest.setDriverPhone(vehicle.getDriverPhone());
            vehicleRequest.setLicensePlate(vehicle.getLicensePlate());
        } else {
            vehicleRequest.setVehicleId(null);
            vehicleRequest.setDriverName(null);
            vehicleRequest.setDriverPhone(null);
            vehicleRequest.setLicensePlate(null);
        }

        if (request.approverId() != null && !request.approverId().isBlank()) {
            HoSoNhanVien approver = hoSoNhanVienRepository.findById(request.approverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người duyệt."));
            vehicleRequest.setApproverId(approver.getId());
        } else {
            vehicleRequest.setApproverId(null);
        }

        VehicleRequest saved = vehicleRequestRepository.save(vehicleRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleRequestResponse submitRequest(String id, String currentUsername) {
        VehicleRequest vehicleRequest = vehicleRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu xe."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!vehicleRequest.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền gửi duyệt yêu cầu xe này.");
        }

        if (!"DRAFT".equals(vehicleRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ yêu cầu xe ở trạng thái Nháp mới có thể gửi duyệt.");
        }

        if (vehicleRequest.getApproverId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yêu cầu xe cần có người duyệt trước khi gửi.");
        }

        validatePassengerCapacity(vehicleRequest.getVehicleId(), vehicleRequest.getDepartureTime(), vehicleRequest.getReturnTime(), vehicleRequest.getPassengerCount(), vehicleRequest.getId());

        vehicleRequest.setStatus("PENDING_APPROVAL");
        VehicleRequest saved = vehicleRequestRepository.save(vehicleRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleRequestResponse approveRequest(String id, VehicleRequestApproveRequest request, String currentUsername) {
        VehicleRequest vehicleRequest = vehicleRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu xe."));
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);

        boolean isAssignedApprover = manager.getId().equals(vehicleRequest.getApproverId());
        boolean isHrOrAdmin = hasManageAuthority();

        if (!isAssignedApprover && !isHrOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền duyệt yêu cầu xe này.");
        }

        if (!"PENDING_APPROVAL".equals(vehicleRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yêu cầu xe không ở trạng thái Chờ duyệt.");
        }

        vehicleRequest.setStatus("APPROVED");
        vehicleRequest.setApproverId(manager.getId());
        vehicleRequest.setDriverName(request.driverName());
        vehicleRequest.setDriverPhone(request.driverPhone());
        vehicleRequest.setLicensePlate(request.licensePlate());

        VehicleRequest saved = vehicleRequestRepository.save(vehicleRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleRequestResponse rejectRequest(String id, VehicleRequestRejectRequest request, String currentUsername) {
        VehicleRequest vehicleRequest = vehicleRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu xe."));
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);

        boolean isAssignedApprover = manager.getId().equals(vehicleRequest.getApproverId());
        boolean isHrOrAdmin = hasManageAuthority();

        if (!isAssignedApprover && !isHrOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền từ chối yêu cầu xe này.");
        }

        if (!"PENDING_APPROVAL".equals(vehicleRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yêu cầu xe không ở trạng thái Chờ duyệt.");
        }

        vehicleRequest.setStatus("REJECTED");
        vehicleRequest.setApproverId(manager.getId());
        vehicleRequest.setRejectReason(request.rejectReason());

        VehicleRequest saved = vehicleRequestRepository.save(vehicleRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRequest(String id, String currentUsername) {
        VehicleRequest vehicleRequest = vehicleRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu xe."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!vehicleRequest.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa yêu cầu xe này.");
        }

        if (!"DRAFT".equals(vehicleRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể xóa yêu cầu xe ở trạng thái Nháp.");
        }

        vehicleRequestRepository.delete(vehicleRequest);
    }

    private VehicleRequestResponse toResponse(VehicleRequest request) {
        HoSoNhanVien employee = hoSoNhanVienRepository.findById(request.getHoSoNhanVienId()).orElse(null);
        String employeeName = employee != null ? employee.getName() : "";
        String employeeCode = employee != null ? employee.getCode() : "";
        String departmentName = "";
        if (employee != null && employee.getSpecialty() != null) {
            departmentName = chuyenKhoaRepository.findById(employee.getSpecialty())
                    .map(ChuyenKhoa::getTenChuyenKhoa)
                    .orElse("");
        }

        String approverName = "";
        if (request.getApproverId() != null) {
            approverName = hoSoNhanVienRepository.findById(request.getApproverId())
                    .map(HoSoNhanVien::getName)
                    .orElse("");
        }

        String vehicleName = "";
        if (request.getVehicleId() != null) {
            vehicleName = vehicleRepository.findById(request.getVehicleId())
                    .map(Vehicle::getName)
                    .orElse("");
        }

        return new VehicleRequestResponse(
                request.getId(),
                request.getHoSoNhanVienId(),
                request.getVehicleId(),
                vehicleName,
                employeeName,
                employeeCode,
                departmentName,
                request.getVehicleType(),
                request.getDepartureTime(),
                request.getReturnTime(),
                request.getRouteDescription(),
                request.getPassengerCount(),
                request.getPurpose(),
                request.getStatus(),
                request.getApproverId(),
                approverName,
                request.getDriverName(),
                request.getDriverPhone(),
                request.getLicensePlate(),
                request.getRejectReason(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    private HoSoNhanVien getHoSoNhanVienForUsername(String username) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy người dùng."));
        return hoSoNhanVienRepository.findByNguoiDungId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài khoản chưa liên kết với hồ sơ nhân sự."));
    }

    private boolean hasManageAuthority() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE));
    }

    private void validatePassengerCapacity(String vehicleId, LocalDateTime departureTime, LocalDateTime returnTime, int passengerCount, String excludeRequestId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy xe được chọn."));

        // 1. Single request capacity check
        if (passengerCount > vehicle.getSeatCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Số lượng người đi (" + passengerCount + " người) vượt quá số chỗ của xe (" + vehicle.getSeatCapacity() + " chỗ).");
        }

        // 2. Overlapping requests capacity check
        java.util.List<VehicleRequest> overlapping = vehicleRequestRepository.findOverlappingRequests(
                vehicleId, departureTime, returnTime, excludeRequestId
        );

        if (overlapping.isEmpty()) {
            return;
        }

        // Collect all relevant time bounds
        java.util.List<LocalDateTime> times = new java.util.ArrayList<>();
        times.add(departureTime);
        times.add(returnTime);
        for (VehicleRequest r : overlapping) {
            times.add(r.getDepartureTime());
            times.add(r.getReturnTime());
        }

        // Sort unique time points
        java.util.List<LocalDateTime> sortedTimes = times.stream()
                .distinct()
                .sorted()
                .toList();

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        // Check each sub-interval within [departureTime, returnTime]
        for (int i = 0; i < sortedTimes.size() - 1; i++) {
            LocalDateTime start = sortedTimes.get(i);
            LocalDateTime end = sortedTimes.get(i + 1);

            // Check if this sub-interval is within the requested interval
            if (!start.isBefore(departureTime) && !end.isAfter(returnTime)) {
                int totalPassengers = passengerCount;

                for (VehicleRequest r : overlapping) {
                    // Check if request r covers [start, end]
                    if (!r.getDepartureTime().isAfter(start) && !r.getReturnTime().isBefore(end)) {
                        totalPassengers += r.getPassengerCount();
                    }
                }

                if (totalPassengers > vehicle.getSeatCapacity()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Tổng số người đi xe (" + totalPassengers + " người) trong khoảng từ " +
                            start.format(formatter) + " đến " + end.format(formatter) +
                            " vượt quá số chỗ của xe (" + vehicle.getSeatCapacity() + " chỗ).");
                }
            }
        }
    }
}
