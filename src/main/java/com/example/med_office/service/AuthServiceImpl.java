package com.example.med_office.service;

import com.example.med_office.dto.LoginResponse;
import com.example.med_office.dto.RoleResponse;
import com.example.med_office.dto.SignupRequest;
import com.example.med_office.entity.ChucVu;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.entity.Role;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.security.AppRoles;
import com.example.med_office.security.PermissionCatalog;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AuthServiceImpl implements AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final ChucVuRepository chucVuRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final PasswordHashService passwordHashService;
    private final AccessControlService accessControlService;

    public AuthServiceImpl(
            NguoiDungRepository nguoiDungRepository,
            ChucVuRepository chucVuRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            PasswordHashService passwordHashService,
            AccessControlService accessControlService
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.passwordHashService = passwordHashService;
        this.accessControlService = accessControlService;
    }

    @Override
    public LoginResponse getLoginResponse(String username) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapAndTrangThaiIgnoreCase(username, "ACTIVE")
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        HoSoNhanVien hoSoNhanVien = hoSoNhanVienRepository.findByNguoiDungId(nguoiDung.getId()).orElse(null);

        String positionName = null;
        String positionRole = AppRoles.USER;
        if (nguoiDung.getChucVuId() != null) {
            var chucVu = chucVuRepository.findById(nguoiDung.getChucVuId()).orElse(null);
            if (chucVu != null) {
                positionName = chucVu.getTenChucVu();
                positionRole = AppRoles.normalizeRoleCode(chucVu.getMaChucVu());
            }
        }

        List<Role> roles = accessControlService.resolveRolesForUser(nguoiDung.getId(), positionRole);
        List<RoleResponse> roleResponses = roles.stream()
                .map(role -> new RoleResponse(role.getId(), role.getCode(), role.getName(), role.getDescription()))
                .toList();
        List<String> permissionCodes = accessControlService.getPermissionCodesForRoles(roles);
        List<String> modules = modulesForPermissions(permissionCodes);
        boolean isAdmin = roles.stream().anyMatch(role -> AppRoles.ADMIN.equals(role.getCode()));
        return new LoginResponse(
                nguoiDung.getId(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getId(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getCode(),
                nguoiDung.getTenDangNhap(),
                firstNonBlank(hoSoNhanVien == null ? null : hoSoNhanVien.getName(), nguoiDung.getTenDangNhap()),
                nguoiDung.getTrangThai(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getEmail(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getPhone(),
                nguoiDung.getPhongBanId(),
                nguoiDung.getChucVuId(),
                positionName,
                roleResponses,
                permissionCodes,
                isAdmin,
                modules,
                nguoiDung.getLanDangNhapCuoi()
        );
    }

    @Override
    @Transactional
    public LoginResponse signup(SignupRequest request) {
        String username = request.getUsername().trim();
        if (nguoiDungRepository.findByTenDangNhap(username).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Username already exists");
        }

        HoSoNhanVien hoSoNhanVien = hoSoNhanVienRepository.findByIdForUpdate(request.getHoSoNhanVienId().trim())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee profile does not exist"));
        if (hoSoNhanVien.getNguoiDungId() != null && !hoSoNhanVien.getNguoiDungId().isBlank()) {
            throw new ResponseStatusException(CONFLICT, "Employee profile already has an account");
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTenDangNhap(username);
        nguoiDung.setMatKhauMaHoa(passwordHashService.encodePasswordForNewUser(request.getPassword()));
        nguoiDung.setChucVuId(resolveChucVuId(request));
        nguoiDung.setTrangThai("ACTIVE");

        NguoiDung savedUser = nguoiDungRepository.save(nguoiDung);
        hoSoNhanVien.setNguoiDungId(savedUser.getId());
        try {
            hoSoNhanVienRepository.saveAndFlush(hoSoNhanVien);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(CONFLICT, "Employee profile already has an account", ex);
        }
        assignInitialRole(savedUser);
        return getLoginResponse(savedUser.getTenDangNhap());
    }

    private String resolveChucVuId(SignupRequest request) {
        if (request.getChucVuId() != null && !request.getChucVuId().isBlank()) {
            return chucVuRepository.findById(request.getChucVuId().trim())
                    .map(ChucVu::getId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Position does not exist"));
        }
        if (request.getMaChucVu() != null && !request.getMaChucVu().isBlank()) {
            return chucVuRepository.findByMaChucVuIgnoreCase(request.getMaChucVu().trim())
                    .map(ChucVu::getId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Position code does not exist"));
        }
        return null;
    }

    private String firstNonBlank(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }

    private void assignInitialRole(NguoiDung nguoiDung) {
        if (nguoiDung.getChucVuId() == null || nguoiDung.getChucVuId().isBlank()) {
            return;
        }
        String roleCode = chucVuRepository.findById(nguoiDung.getChucVuId())
                .map(ChucVu::getMaChucVu)
                .map(AppRoles::toPermissionRoleCode)
                .orElse(AppRoles.USER);
        if (!AppRoles.USER.equals(roleCode)) {
            accessControlService.replaceUserRoles(nguoiDung.getId(), List.of(roleCode));
        }
    }

    private List<String> modulesForPermissions(List<String> permissionCodes) {
        LinkedHashSet<String> modules = new LinkedHashSet<>();
        PermissionCatalog.definitions().stream()
                .filter(definition -> permissionCodes.contains(definition.code()))
                .forEach(definition -> modules.add(definition.moduleCode()));
        return List.copyOf(modules);
    }
}
