package com.example.med_office.service;

import com.example.med_office.dto.LoginResponse;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.NguoiDungRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final ChucVuRepository chucVuRepository;

    public AuthServiceImpl(NguoiDungRepository nguoiDungRepository, ChucVuRepository chucVuRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
    }

    @Override
    public LoginResponse getLoginResponse(String username) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapAndTrangThaiIgnoreCase(username, "ACTIVE")
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        String positionName = null;
        if (nguoiDung.getChucVuId() != null) {
            positionName = chucVuRepository.findById(nguoiDung.getChucVuId())
                    .map(chucVu -> chucVu.getTenChucVu())
                    .orElse(null);
        }

        return new LoginResponse(
                nguoiDung.getId(),
                nguoiDung.getTenDangNhap(),
                nguoiDung.getHoTen(),
                nguoiDung.getTrangThai(),
                nguoiDung.getEmail(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getPhongBanId(),
                nguoiDung.getChucVuId(),
                positionName,
                nguoiDung.getLanDangNhapCuoi()
        );
    }
}
