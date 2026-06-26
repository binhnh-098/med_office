package com.example.med_office.service;

import com.example.med_office.dto.BusinessTripDTOs.*;
import com.example.med_office.entity.BusinessTrip;
import com.example.med_office.entity.ChuyenKhoa;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.BusinessTripRepository;
import com.example.med_office.repository.ChuyenKhoaRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
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

import java.time.LocalDate;

@Service
public class BusinessTripServiceImpl implements BusinessTripService {

    private final BusinessTripRepository businessTripRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ChuyenKhoaRepository chuyenKhoaRepository;

    public BusinessTripServiceImpl(
            BusinessTripRepository businessTripRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            NguoiDungRepository nguoiDungRepository,
            ChuyenKhoaRepository chuyenKhoaRepository
    ) {
        this.businessTripRepository = businessTripRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.chuyenKhoaRepository = chuyenKhoaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusinessTripResponse> getMyTrips(
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

        return businessTripRepository.searchMyTrips(employee.getId(), cleanStatus, cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusinessTripResponse> getPendingApprovals(
            String keyword,
            String currentUsername,
            int page,
            int size
    ) {
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return businessTripRepository.searchApprovals(manager.getId(), cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusinessTripResponse> getActiveTrips(
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return businessTripRepository.searchActiveTrips(LocalDate.now(), cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessTripResponse getTripDetail(String id, String currentUsername) {
        BusinessTrip trip = businessTripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin công tác."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        boolean isTripOwner = trip.getHoSoNhanVienId().equals(employee.getId());
        boolean isApprover = employee.getId().equals(trip.getApproverId());
        boolean isHrOrAdmin = hasManageAuthority();

        if (!isTripOwner && !isApprover && !isHrOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem thông tin công tác này.");
        }

        return toResponse(trip);
    }

    @Override
    @Transactional
    public BusinessTripResponse createTrip(BusinessTripUpsertRequest request, String currentUsername) {
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (request.startDate().isAfter(request.endDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu không được sau ngày kết thúc.");
        }

        BusinessTrip trip = new BusinessTrip();
        trip.setHoSoNhanVienId(employee.getId());
        trip.setDestination(request.destination());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setPurpose(request.purpose());
        trip.setStatus("DRAFT");

        if (request.approverId() != null && !request.approverId().isBlank()) {
            HoSoNhanVien approver = hoSoNhanVienRepository.findById(request.approverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người duyệt."));
            trip.setApproverId(approver.getId());
        }

        BusinessTrip saved = businessTripRepository.save(trip);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public BusinessTripResponse updateTrip(String id, BusinessTripUpsertRequest request, String currentUsername) {
        BusinessTrip trip = businessTripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin công tác."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!trip.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật thông tin công tác này.");
        }

        if (!"DRAFT".equals(trip.getStatus()) && !"REJECTED".equals(trip.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được cập nhật đề xuất công tác ở trạng thái Nháp hoặc Từ chối.");
        }

        if (request.startDate().isAfter(request.endDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu không được sau ngày kết thúc.");
        }

        trip.setDestination(request.destination());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setPurpose(request.purpose());
        trip.setStatus("DRAFT");
        trip.setRejectReason(null);

        if (request.approverId() != null && !request.approverId().isBlank()) {
            HoSoNhanVien approver = hoSoNhanVienRepository.findById(request.approverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người duyệt."));
            trip.setApproverId(approver.getId());
        } else {
            trip.setApproverId(null);
        }

        BusinessTrip saved = businessTripRepository.save(trip);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public BusinessTripResponse submitTrip(String id, String currentUsername) {
        BusinessTrip trip = businessTripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin công tác."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!trip.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền gửi duyệt đề xuất công tác này.");
        }

        if (!"DRAFT".equals(trip.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ đề xuất ở trạng thái Nháp mới có thể gửi duyệt.");
        }

        if (trip.getApproverId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đề xuất cần có thông tin người duyệt trước khi gửi.");
        }

        trip.setStatus("PENDING_APPROVAL");
        BusinessTrip saved = businessTripRepository.save(trip);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public BusinessTripResponse approveTrip(String id, String currentUsername) {
        BusinessTrip trip = businessTripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin công tác."));
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);

        boolean isAssignedApprover = manager.getId().equals(trip.getApproverId());
        boolean isHrOrAdmin = hasManageAuthority();

        if (!isAssignedApprover && !isHrOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền duyệt đề xuất công tác này.");
        }

        if (!"PENDING_APPROVAL".equals(trip.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đề xuất công tác không ở trạng thái Chờ duyệt.");
        }

        trip.setStatus("APPROVED");
        trip.setApproverId(manager.getId());
        BusinessTrip saved = businessTripRepository.save(trip);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public BusinessTripResponse rejectTrip(String id, BusinessTripRejectRequest request, String currentUsername) {
        BusinessTrip trip = businessTripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin công tác."));
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);

        boolean isAssignedApprover = manager.getId().equals(trip.getApproverId());
        boolean isHrOrAdmin = hasManageAuthority();

        if (!isAssignedApprover && !isHrOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền từ chối đề xuất công tác này.");
        }

        if (!"PENDING_APPROVAL".equals(trip.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đề xuất công tác không ở trạng thái Chờ duyệt.");
        }

        trip.setStatus("REJECTED");
        trip.setApproverId(manager.getId());
        trip.setRejectReason(request.rejectReason());
        BusinessTrip saved = businessTripRepository.save(trip);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTrip(String id, String currentUsername) {
        BusinessTrip trip = businessTripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin công tác."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!trip.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa đề xuất công tác này.");
        }

        if (!"DRAFT".equals(trip.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể xóa đề xuất công tác ở trạng thái Nháp.");
        }

        businessTripRepository.delete(trip);
    }

    private BusinessTripResponse toResponse(BusinessTrip trip) {
        HoSoNhanVien employee = hoSoNhanVienRepository.findById(trip.getHoSoNhanVienId()).orElse(null);
        String employeeName = employee != null ? employee.getName() : "";
        String employeeCode = employee != null ? employee.getCode() : "";
        String departmentName = "";
        if (employee != null && employee.getSpecialty() != null) {
            departmentName = chuyenKhoaRepository.findById(employee.getSpecialty())
                    .map(ChuyenKhoa::getTenChuyenKhoa)
                    .orElse("");
        }

        String approverName = "";
        if (trip.getApproverId() != null) {
            approverName = hoSoNhanVienRepository.findById(trip.getApproverId())
                    .map(HoSoNhanVien::getName)
                    .orElse("");
        }

        return new BusinessTripResponse(
                trip.getId(),
                trip.getHoSoNhanVienId(),
                employeeName,
                employeeCode,
                departmentName,
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getPurpose(),
                trip.getStatus(),
                trip.getApproverId(),
                approverName,
                trip.getRejectReason(),
                trip.getCreatedAt(),
                trip.getUpdatedAt()
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
}
