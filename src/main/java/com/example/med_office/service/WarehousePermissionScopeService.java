package com.example.med_office.service;

import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.WarehouseManagerRepository;
import com.example.med_office.security.AppRoles;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class WarehousePermissionScopeService {

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final WarehouseManagerRepository warehouseManagerRepository;

    public WarehousePermissionScopeService(
            NguoiDungRepository nguoiDungRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            WarehouseManagerRepository warehouseManagerRepository
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
    }

    @Transactional(readOnly = true)
    public boolean isAdmin() {
        Authentication authentication = currentAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> ("ROLE_" + AppRoles.ADMIN).equals(authority.getAuthority()));
    }

    @Transactional(readOnly = true)
    public Set<String> getManagedWarehouseIds() {
        if (isAdmin()) {
            return Set.of();
        }

        NguoiDung currentUser = currentUser();
        HoSoNhanVien employeeProfile = hoSoNhanVienRepository.findByNguoiDungId(currentUser.getId()).orElse(null);
        if (employeeProfile == null) {
            return Set.of();
        }

        return warehouseManagerRepository.findByIdEmployeeProfileId(employeeProfile.getId()).stream()
                .map(manager -> manager.getId().getWarehouseId())
                .filter(warehouseId -> warehouseId != null && !warehouseId.isBlank())
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    @Transactional(readOnly = true)
    public void assertWarehouseAccess(String warehouseId, String message) {
        if (isAdmin()) {
            return;
        }

        if (!getManagedWarehouseIds().contains(warehouseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    private NguoiDung currentUser() {
        String username = currentAuthentication().getName();
        return nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ban can dang nhap de tiep tuc"));
    }

    private Authentication currentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ban can dang nhap de tiep tuc");
        }
        return authentication;
    }
}
