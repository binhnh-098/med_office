package com.example.med_office.service;

import com.example.med_office.dto.NguoiDungResponse;
import com.example.med_office.dto.NguoiDungRoleUpdateRequest;
import com.example.med_office.entity.ChucVu;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.security.AppPermissions;
import com.example.med_office.security.AppRoles;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class NguoiDungServiceImpl implements NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final ChucVuRepository chucVuRepository;

    public NguoiDungServiceImpl(
            NguoiDungRepository nguoiDungRepository,
            ChucVuRepository chucVuRepository
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
    }

    @Override
    public List<NguoiDungResponse> getUsers() {
        return nguoiDungRepository.findAll().stream()
                .sorted(Comparator.comparing(NguoiDung::getTenDangNhap, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NguoiDungResponse updateUserRole(String id, NguoiDungRoleUpdateRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        nguoiDung.setChucVuId(resolveChucVuId(request));
        return toResponse(nguoiDungRepository.save(nguoiDung));
    }

    private String resolveChucVuId(NguoiDungRoleUpdateRequest request) {
        if (request.getChucVuId() != null && !request.getChucVuId().isBlank()) {
            return chucVuRepository.findById(request.getChucVuId().trim())
                    .map(ChucVu::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Position does not exist"));
        }
        if (request.getMaChucVu() != null && !request.getMaChucVu().isBlank()) {
            return chucVuRepository.findByMaChucVuIgnoreCase(request.getMaChucVu().trim())
                    .map(ChucVu::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Position code does not exist"));
        }
        return null;
    }

    private NguoiDungResponse toResponse(NguoiDung nguoiDung) {
        ChucVu chucVu = nguoiDung.getChucVuId() == null ? null : chucVuRepository.findById(nguoiDung.getChucVuId()).orElse(null);
        String role = chucVu == null ? AppRoles.USER : AppRoles.normalizeRoleCode(chucVu.getMaChucVu());
        LinkedHashSet<String> roles = new LinkedHashSet<>();
        roles.add(AppRoles.USER);
        roles.add(role);
        List<String> roleList = List.copyOf(roles);

        return new NguoiDungResponse(
                nguoiDung.getId(),
                nguoiDung.getTenDangNhap(),
                nguoiDung.getHoTen(),
                nguoiDung.getEmail(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getChucVuId(),
                chucVu == null ? null : chucVu.getMaChucVu(),
                chucVu == null ? null : chucVu.getTenChucVu(),
                roleList,
                AppPermissions.modulesForRoles(roleList),
                nguoiDung.getTrangThai()
        );
    }
}
