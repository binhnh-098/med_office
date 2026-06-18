package com.example.med_office.service;

import com.example.med_office.dto.LeaveRequestDTOs.*;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.LeaveRequest;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.LeaveRequestRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.security.PermissionCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            NguoiDungRepository nguoiDungRepository
    ) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public Page<LeaveRequestResponse> getLeaveRequests(
            String keyword,
            String status,
            String employeeId,
            String currentUsername,
            int page,
            int size
    ) {
        HoSoNhanVien currentEmployee = getHoSoNhanVienForUsername(currentUsername);
        boolean isHrOrAdmin = hasManageAuthority();
        boolean hasSubordinates = hoSoNhanVienRepository.existsByDirectManagerId(currentEmployee.getId());

        Specification<LeaveRequest> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (isHrOrAdmin) {
                if (employeeId != null && !employeeId.isBlank()) {
                    predicates.add(cb.equal(root.get("hoSoNhanVienId"), employeeId));
                }
            } else {
                if (employeeId != null && !employeeId.isBlank()) {
                    if (currentEmployee.getId().equals(employeeId)) {
                        predicates.add(cb.equal(root.get("hoSoNhanVienId"), employeeId));
                    } else {
                        HoSoNhanVien targetEmployee = hoSoNhanVienRepository.findById(employeeId).orElse(null);
                        boolean isAssignedApprover = leaveRequestRepository.existsByHoSoNhanVienIdAndApproverId(employeeId, currentEmployee.getId());
                        if (targetEmployee != null && (currentEmployee.getId().equals(targetEmployee.getDirectManagerId()) || isAssignedApprover)) {
                            predicates.add(cb.equal(root.get("hoSoNhanVienId"), employeeId));
                        } else {
                            predicates.add(cb.equal(root.get("id"), ""));
                        }
                    }
                } else {
                    boolean isApprover = hoSoNhanVienRepository.existsByDirectManagerId(currentEmployee.getId()) ||
                            leaveRequestRepository.existsByApproverId(currentEmployee.getId());
                    if (isApprover) {
                        List<String> subordinateIds = hoSoNhanVienRepository.findByDirectManagerId(currentEmployee.getId()).stream()
                                .map(HoSoNhanVien::getId)
                                .toList();
                        if (subordinateIds.isEmpty()) {
                            predicates.add(cb.equal(root.get("approverId"), currentEmployee.getId()));
                        } else {
                            predicates.add(cb.or(
                                    root.get("hoSoNhanVienId").in(subordinateIds),
                                    cb.equal(root.get("approverId"), currentEmployee.getId())
                            ));
                        }
                    } else {
                        predicates.add(cb.equal(root.get("hoSoNhanVienId"), currentEmployee.getId()));
                    }
                }
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("employeeName")), kw),
                        cb.like(cb.lower(root.get("employeeCode")), kw),
                        cb.like(cb.lower(root.get("reason")), kw)
                ));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return leaveRequestRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public LeaveRequestResponse getLeaveRequestDetail(String id, String currentUsername) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn nghỉ phép."));
        HoSoNhanVien currentEmployee = getHoSoNhanVienForUsername(currentUsername);

        HoSoNhanVien requesterEmployee = hoSoNhanVienRepository.findById(leaveRequest.getHoSoNhanVienId()).orElse(null);
        boolean isDirectManager = requesterEmployee != null && currentEmployee.getId().equals(requesterEmployee.getDirectManagerId());
        boolean isHrOrAdmin = hasManageAuthority();
        boolean isAssignedApprover = currentEmployee.getId().equals(leaveRequest.getApproverId());

        if (!leaveRequest.getHoSoNhanVienId().equals(currentEmployee.getId()) && !isDirectManager && !isHrOrAdmin && !isAssignedApprover) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem đơn nghỉ phép này.");
        }

        return toResponse(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequestResponse createLeaveRequest(LeaveRequestUpsertRequest request, String currentUsername) {
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setHoSoNhanVienId(employee.getId());
        leaveRequest.setEmployeeName(employee.getName());
        leaveRequest.setEmployeeCode(employee.getCode());
        leaveRequest.setLeaveType(request.leaveType());
        leaveRequest.setStartDate(request.startDate());
        leaveRequest.setEndDate(request.endDate());
        leaveRequest.setTotalDays(request.totalDays());
        leaveRequest.setReason(request.reason());
        leaveRequest.setStatus("DRAFT");

        if (request.approverId() != null && !request.approverId().isBlank()) {
            HoSoNhanVien approver = hoSoNhanVienRepository.findById(request.approverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người duyệt."));
            leaveRequest.setApproverId(approver.getId());
            leaveRequest.setApproverName(approver.getName());
        }
        if (request.handoverEmployeeId() != null && !request.handoverEmployeeId().isBlank()) {
            HoSoNhanVien handover = hoSoNhanVienRepository.findById(request.handoverEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người bàn giao."));
            leaveRequest.setHandoverEmployeeId(handover.getId());
            leaveRequest.setHandoverEmployeeName(handover.getName());
        }

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveRequestResponse updateLeaveRequest(String id, LeaveRequestUpsertRequest request, String currentUsername) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn nghỉ phép."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!leaveRequest.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật đơn nghỉ phép này.");
        }

        if (!"DRAFT".equals(leaveRequest.getStatus()) && !"REJECTED".equals(leaveRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được cập nhật đơn nghỉ phép ở trạng thái Nháp hoặc Từ chối.");
        }

        leaveRequest.setLeaveType(request.leaveType());
        leaveRequest.setStartDate(request.startDate());
        leaveRequest.setEndDate(request.endDate());
        leaveRequest.setTotalDays(request.totalDays());
        leaveRequest.setReason(request.reason());
        leaveRequest.setStatus("DRAFT");
        leaveRequest.setRejectReason(null);

        if (request.approverId() != null && !request.approverId().isBlank()) {
            HoSoNhanVien approver = hoSoNhanVienRepository.findById(request.approverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người duyệt."));
            leaveRequest.setApproverId(approver.getId());
            leaveRequest.setApproverName(approver.getName());
        } else {
            leaveRequest.setApproverId(null);
            leaveRequest.setApproverName(null);
        }

        if (request.handoverEmployeeId() != null && !request.handoverEmployeeId().isBlank()) {
            HoSoNhanVien handover = hoSoNhanVienRepository.findById(request.handoverEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người bàn giao."));
            leaveRequest.setHandoverEmployeeId(handover.getId());
            leaveRequest.setHandoverEmployeeName(handover.getName());
        } else {
            leaveRequest.setHandoverEmployeeId(null);
            leaveRequest.setHandoverEmployeeName(null);
        }

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveRequestResponse submitLeaveRequest(String id, String currentUsername) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn nghỉ phép."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!leaveRequest.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền gửi duyệt đơn nghỉ phép này.");
        }

        if (!"DRAFT".equals(leaveRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ đơn ở trạng thái Nháp mới có thể gửi duyệt.");
        }

        leaveRequest.setStatus("PENDING_APPROVAL");
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveRequestResponse approveLeaveRequest(String id, String currentUsername) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn nghỉ phép."));
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);

        HoSoNhanVien employee = hoSoNhanVienRepository.findByIdForUpdate(leaveRequest.getHoSoNhanVienId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin nhân viên nghỉ phép."));

        boolean isDirectManager = manager.getId().equals(employee.getDirectManagerId());
        boolean isHrOrAdmin = hasManageAuthority();
        boolean isAssignedApprover = manager.getId().equals(leaveRequest.getApproverId());

        if (!isDirectManager && !isHrOrAdmin && !isAssignedApprover) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền duyệt đơn nghỉ phép của nhân viên này.");
        }

        if (!"PENDING_APPROVAL".equals(leaveRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn nghỉ phép không ở trạng thái Chờ duyệt.");
        }

        if ("ANNUAL".equals(leaveRequest.getLeaveType())) {
            double remaining = (employee.getAnnualLeaveTotal() != null ? employee.getAnnualLeaveTotal() : 12.0) -
                    (employee.getAnnualLeaveUsed() != null ? employee.getAnnualLeaveUsed() : 0.0);
            if (leaveRequest.getTotalDays() > remaining) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhân viên không đủ số ngày phép năm còn lại (Còn lại: " + remaining + " ngày).");
            }
            double used = (employee.getAnnualLeaveUsed() != null ? employee.getAnnualLeaveUsed() : 0.0) + leaveRequest.getTotalDays();
            employee.setAnnualLeaveUsed(used);
            hoSoNhanVienRepository.save(employee);
        }

        leaveRequest.setStatus("APPROVED");
        leaveRequest.setApproverId(manager.getId());
        leaveRequest.setApproverName(manager.getName());

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveRequestResponse rejectLeaveRequest(String id, LeaveRequestRejectRequest request, String currentUsername) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn nghỉ phép."));
        HoSoNhanVien manager = getHoSoNhanVienForUsername(currentUsername);

        HoSoNhanVien employee = hoSoNhanVienRepository.findById(leaveRequest.getHoSoNhanVienId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin nhân viên nghỉ phép."));

        boolean isDirectManager = manager.getId().equals(employee.getDirectManagerId());
        boolean isHrOrAdmin = hasManageAuthority();
        boolean isAssignedApprover = manager.getId().equals(leaveRequest.getApproverId());

        if (!isDirectManager && !isHrOrAdmin && !isAssignedApprover) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền từ chối đơn nghỉ phép của nhân viên này.");
        }

        if (!"PENDING_APPROVAL".equals(leaveRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn nghỉ phép không ở trạng thái Chờ duyệt.");
        }

        leaveRequest.setStatus("REJECTED");
        leaveRequest.setApproverId(manager.getId());
        leaveRequest.setApproverName(manager.getName());
        leaveRequest.setRejectReason(request.rejectReason());

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteLeaveRequest(String id, String currentUsername) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn nghỉ phép."));
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);

        if (!leaveRequest.getHoSoNhanVienId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa đơn nghỉ phép này.");
        }

        if (!"DRAFT".equals(leaveRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể xóa đơn nghỉ phép ở trạng thái Nháp.");
        }

        leaveRequestRepository.delete(leaveRequest);
    }

    @Override
    public LeaveBalanceResponse getLeaveBalance(String currentUsername) {
        HoSoNhanVien employee = getHoSoNhanVienForUsername(currentUsername);
        double total = employee.getAnnualLeaveTotal() != null ? employee.getAnnualLeaveTotal() : 12.0;
        double used = employee.getAnnualLeaveUsed() != null ? employee.getAnnualLeaveUsed() : 0.0;
        double remaining = total - used;
        boolean hasSubordinates = hoSoNhanVienRepository.existsByDirectManagerId(employee.getId()) || leaveRequestRepository.existsByApproverId(employee.getId());
        return new LeaveBalanceResponse(total, used, remaining, hasSubordinates);
    }

    private HoSoNhanVien getHoSoNhanVienForUsername(String username) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy người dùng."));
        return hoSoNhanVienRepository.findByNguoiDungId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài khoản chưa liên kết với hồ sơ nhân sự."));
    }

    private boolean hasManageAuthority() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(PermissionCatalog.EMPLOYEES_LEAVE_MANAGE));
    }

    private LeaveRequestResponse toResponse(LeaveRequest request) {
        return new LeaveRequestResponse(
                request.getId(),
                request.getHoSoNhanVienId(),
                request.getEmployeeName(),
                request.getEmployeeCode(),
                request.getLeaveType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTotalDays(),
                request.getReason(),
                request.getApproverId(),
                request.getApproverName(),
                request.getHandoverEmployeeId(),
                request.getHandoverEmployeeName(),
                request.getStatus(),
                request.getRejectReason(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
