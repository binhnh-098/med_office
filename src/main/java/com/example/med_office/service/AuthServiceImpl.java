package com.example.med_office.service;

import com.example.med_office.dto.LoginResponse;
import com.example.med_office.dto.SignupRequest;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class AuthServiceImpl implements AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final ChucVuRepository chucVuRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            NguoiDungRepository nguoiDungRepository,
            ChucVuRepository chucVuRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse getLoginResponse(String username) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapAndTrangThaiIgnoreCase(username, "ACTIVE")
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        HoSoNhanVien hoSoNhanVien = hoSoNhanVienRepository.findByNguoiDungId(nguoiDung.getId()).orElse(null);

        String positionName = null;
        if (nguoiDung.getChucVuId() != null) {
            positionName = chucVuRepository.findById(nguoiDung.getChucVuId())
                    .map(chucVu -> chucVu.getTenChucVu())
                    .orElse(null);
        }

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
        nguoiDung.setMatKhauMaHoa(passwordEncoder.encode(request.getPassword()));
        nguoiDung.setHoTen(request.getUsername().trim());
        nguoiDung.setTrangThai("ACTIVE");

        NguoiDung savedUser = nguoiDungRepository.save(nguoiDung);
        return getLoginResponse(savedUser.getTenDangNhap());
    }

    private String firstNonBlank(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }
}
