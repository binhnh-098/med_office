package com.example.med_office.service;

import com.example.med_office.dto.LoginResponse;
import com.example.med_office.dto.SignupRequest;
import com.example.med_office.entity.ChucVu;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.security.AppPermissions;
import com.example.med_office.security.AppRoles;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class AuthServiceImpl implements AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final ChucVuRepository chucVuRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final PasswordHashService passwordHashService;

    public AuthServiceImpl(
            NguoiDungRepository nguoiDungRepository,
            ChucVuRepository chucVuRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            PasswordHashService passwordHashService
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.passwordHashService = passwordHashService;
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

        List<String> roles = rolesForResponse(positionRole);
        return new LoginResponse(
                nguoiDung.getId(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getId(),
                nguoiDung.getTenDangNhap(),
                firstNonBlank(hoSoNhanVien == null ? null : hoSoNhanVien.getName(), nguoiDung.getHoTen()),
                nguoiDung.getTrangThai(),
                firstNonBlank(hoSoNhanVien == null ? null : hoSoNhanVien.getEmail(), nguoiDung.getEmail()),
                firstNonBlank(hoSoNhanVien == null ? null : hoSoNhanVien.getPhone(), nguoiDung.getSoDienThoai()),
                nguoiDung.getPhongBanId(),
                nguoiDung.getChucVuId(),
                positionName,
                roles,
                AppPermissions.modulesForRoles(roles),
                nguoiDung.getLanDangNhapCuoi()
        );
    }

    @Override
    public LoginResponse signup(SignupRequest request) {
        if (nguoiDungRepository.findByTenDangNhap(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Username already exists");
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTenDangNhap(request.getUsername().trim());
        nguoiDung.setMatKhauMaHoa(passwordHashService.encodePasswordForNewUser(request.getPassword()));
        nguoiDung.setHoTen(firstNonBlank(request.getFullName(), request.getUsername()).trim());
        nguoiDung.setEmail(blankToNull(request.getEmail()));
        nguoiDung.setSoDienThoai(blankToNull(request.getPhoneNumber()));
        nguoiDung.setChucVuId(resolveChucVuId(request));
        nguoiDung.setTrangThai("ACTIVE");

        NguoiDung savedUser = nguoiDungRepository.save(nguoiDung);
        return getLoginResponse(savedUser.getTenDangNhap());
    }

    private String resolveChucVuId(SignupRequest request) {
        if (request.getChucVuId() != null && !request.getChucVuId().isBlank()) {
            return chucVuRepository.findById(request.getChucVuId().trim())
                    .map(ChucVu::getId)
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Position does not exist"));
        }
        if (request.getMaChucVu() != null && !request.getMaChucVu().isBlank()) {
            return chucVuRepository.findByMaChucVuIgnoreCase(request.getMaChucVu().trim())
                    .map(ChucVu::getId)
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Position code does not exist"));
        }
        return null;
    }

    private String firstNonBlank(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private List<String> rolesForResponse(String positionRole) {
        LinkedHashSet<String> roles = new LinkedHashSet<>();
        roles.add(AppRoles.USER);
        roles.add(positionRole);
        return List.copyOf(roles);
    }
}
