package com.example.med_office.service;

import com.example.med_office.dto.ContractDTOs.*;
import com.example.med_office.entity.Contract;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.ContractRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public ContractServiceImpl(
            ContractRepository contractRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            NguoiDungRepository nguoiDungRepository
    ) {
        this.contractRepository = contractRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public Page<ContractResponse> getContracts(
            String keyword,
            String status,
            String employeeId,
            String currentUsername,
            int page,
            int size
    ) {
        HoSoNhanVien currentEmployee = getHoSoNhanVienForUsername(currentUsername);
        boolean isHrOrAdmin = hasAuthority(PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE);

        Specification<Contract> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // 1. Access Control
            if (isHrOrAdmin) {
                if (employeeId != null && !employeeId.isBlank()) {
                    predicates.add(cb.equal(root.get("hoSoNhanVienId"), employeeId));
                }
            } else {
                // Employees can only see their own contracts
                predicates.add(cb.equal(root.get("hoSoNhanVienId"), currentEmployee.getId()));
            }

            // 2. Keyword filter (contract number, employee code, or employee name through join or subquery)
            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword.toLowerCase() + "%";
                
                // We'll subquery ho_so_nhan_vien for keyword match on name or code
                Specification<Contract> subquerySpec = (rootSub, querySub, cbSub) -> {
                    // This is a join/like query
                    return cbSub.like(cbSub.lower(root.get("contractNumber")), kw);
                };
                
                // Let's get list of hoSoNhanVienIds matching keyword
                List<String> matchingEmployeeIds = hoSoNhanVienRepository.findAll().stream()
                        .filter(emp -> emp.getName().toLowerCase().contains(keyword.toLowerCase()) || 
                                       emp.getCode().toLowerCase().contains(keyword.toLowerCase()))
                        .map(HoSoNhanVien::getId)
                        .toList();

                if (matchingEmployeeIds.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("contractNumber")), kw));
                } else {
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("contractNumber")), kw),
                            root.get("hoSoNhanVienId").in(matchingEmployeeIds)
                    ));
                }
            }

            // 3. Status filter (with dynamic logic)
            if (status != null && !status.isBlank()) {
                LocalDate today = LocalDate.now();
                LocalDate todayPlus30 = today.plusDays(30);

                if ("EXPIRED".equalsIgnoreCase(status)) {
                    predicates.add(cb.or(
                            cb.equal(root.get("status"), "EXPIRED"),
                            cb.and(
                                    cb.isNotNull(root.get("endDate")),
                                    cb.lessThan(root.get("endDate"), today)
                            )
                    ));
                } else if ("EXPIRING_SOON".equalsIgnoreCase(status)) {
                    predicates.add(cb.and(
                            cb.notEqual(root.get("status"), "EXPIRED"),
                            cb.isNotNull(root.get("endDate")),
                            cb.greaterThanOrEqualTo(root.get("endDate"), today),
                            cb.lessThanOrEqualTo(root.get("endDate"), todayPlus30)
                    ));
                } else if ("ACTIVE".equalsIgnoreCase(status)) {
                    predicates.add(cb.and(
                            cb.notEqual(root.get("status"), "EXPIRED"),
                            cb.or(
                                    cb.isNull(root.get("endDate")),
                                    cb.greaterThan(root.get("endDate"), todayPlus30)
                            )
                    ));
                }
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));
        return contractRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public ContractResponse getContractDetail(String id, String currentUsername) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng."));
        
        HoSoNhanVien currentEmployee = getHoSoNhanVienForUsername(currentUsername);
        boolean isHrOrAdmin = hasAuthority(PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE) ||
                hasAuthority(PermissionCatalog.EMPLOYEES_CONTRACT_EXPIRING_VIEW);

        if (!contract.getHoSoNhanVienId().equals(currentEmployee.getId()) && !isHrOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem hợp đồng này.");
        }

        return toResponse(contract);
    }

    @Override
    @Transactional
    public ContractResponse createContract(ContractUpsertRequest request, String currentUsername) {
        if (!hasAuthority(PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền tạo hợp đồng.");
        }

        if (contractRepository.existsByContractNumber(request.contractNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số hợp đồng đã tồn tại trong hệ thống.");
        }

        HoSoNhanVien employee = hoSoNhanVienRepository.findById(request.hoSoNhanVienId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy hồ sơ nhân sự."));

        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
        }

        Contract contract = new Contract();
        contract.setHoSoNhanVienId(employee.getId());
        contract.setContractNumber(request.contractNumber());
        contract.setContractType(request.contractType());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setSalary(request.salary());
        contract.setStatus(request.status() != null ? request.status() : "ACTIVE");
        contract.setNote(request.note());

        Contract saved = contractRepository.save(contract);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ContractResponse updateContract(String id, ContractUpsertRequest request, String currentUsername) {
        if (!hasAuthority(PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật hợp đồng.");
        }

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng."));

        if (contractRepository.existsByContractNumberAndIdNot(request.contractNumber(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số hợp đồng đã tồn tại trong hệ thống.");
        }

        HoSoNhanVien employee = hoSoNhanVienRepository.findById(request.hoSoNhanVienId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy hồ sơ nhân sự."));

        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
        }

        contract.setHoSoNhanVienId(employee.getId());
        contract.setContractNumber(request.contractNumber());
        contract.setContractType(request.contractType());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setSalary(request.salary());
        if (request.status() != null) {
            contract.setStatus(request.status());
        }
        contract.setNote(request.note());

        Contract saved = contractRepository.save(contract);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteContract(String id, String currentUsername) {
        if (!hasAuthority(PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa hợp đồng.");
        }

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng."));

        contractRepository.delete(contract);
    }

    private String computeStatus(Contract contract) {
        if ("EXPIRED".equalsIgnoreCase(contract.getStatus())) {
            return "EXPIRED";
        }
        LocalDate today = LocalDate.now();
        if (contract.getEndDate() != null) {
            if (contract.getEndDate().isBefore(today)) {
                return "EXPIRED";
            }
            if (!contract.getEndDate().isBefore(today) && !contract.getEndDate().isAfter(today.plusDays(30))) {
                return "EXPIRING_SOON";
            }
        }
        return "ACTIVE";
    }

    private ContractResponse toResponse(Contract contract) {
        HoSoNhanVien employee = hoSoNhanVienRepository.findById(contract.getHoSoNhanVienId()).orElse(null);
        String employeeName = employee != null ? employee.getName() : "";
        String employeeCode = employee != null ? employee.getCode() : "";

        return new ContractResponse(
                contract.getId(),
                contract.getHoSoNhanVienId(),
                employeeName,
                employeeCode,
                contract.getContractNumber(),
                contract.getContractType(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getSalary(),
                computeStatus(contract),
                contract.getNote(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }

    private HoSoNhanVien getHoSoNhanVienForUsername(String username) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy người dùng."));
        return hoSoNhanVienRepository.findByNguoiDungId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài khoản chưa liên kết với hồ sơ nhân sự."));
    }

    private boolean hasAuthority(String permission) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }
}
