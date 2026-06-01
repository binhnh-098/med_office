package com.example.med_office.service;

import com.example.med_office.dto.NguoiDungResponse;
import com.example.med_office.dto.NguoiDungRoleUpdateRequest;
import com.example.med_office.entity.ChucVu;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.entity.Role;
import com.example.med_office.entity.UserRole;
import com.example.med_office.entity.UserRoleId;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.RoleRepository;
import com.example.med_office.repository.UserRoleRepository;
import com.example.med_office.security.AppPermissions;
import com.example.med_office.security.AppRoles;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NguoiDungServiceImpl implements NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final ChucVuRepository chucVuRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public NguoiDungServiceImpl(
            NguoiDungRepository nguoiDungRepository,
            ChucVuRepository chucVuRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public List<NguoiDungResponse> getUsers() {
        List<NguoiDung> nguoiDungs = nguoiDungRepository.findAll();
        if (nguoiDungs.isEmpty()) {
            return List.of();
        }

        Map<String, HoSoNhanVien> hoSoByNguoiDungId = hoSoNhanVienRepository.findByNguoiDungIdIn(
                        nguoiDungs.stream().map(NguoiDung::getId).toList()
                ).stream()
                .filter(hoSoNhanVien -> hoSoNhanVien.getNguoiDungId() != null)
                .collect(Collectors.toMap(
                        HoSoNhanVien::getNguoiDungId,
                        Function.identity(),
                        (first, ignored) -> first
                ));

        return nguoiDungs.stream()
                .sorted(Comparator.comparing(NguoiDung::getTenDangNhap, String.CASE_INSENSITIVE_ORDER))
                .map(nguoiDung -> toResponse(nguoiDung, hoSoByNguoiDungId.get(nguoiDung.getId())))
                .toList();
    }

    @Override
    @Transactional
    public NguoiDungResponse updateUserRole(String id, NguoiDungRoleUpdateRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        nguoiDung.setChucVuId(resolveChucVuId(request));
        NguoiDung savedNguoiDung = nguoiDungRepository.save(nguoiDung);
        if (request.getRoleCodes() == null) {
            syncRoleFromChucVu(savedNguoiDung);
        } else {
            replaceUserRoles(savedNguoiDung.getId(), request.getRoleCodes());
        }
        HoSoNhanVien hoSoNhanVien = hoSoNhanVienRepository.findByNguoiDungId(savedNguoiDung.getId()).orElse(null);
        return toResponse(savedNguoiDung, hoSoNhanVien);
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

    private NguoiDungResponse toResponse(NguoiDung nguoiDung, HoSoNhanVien hoSoNhanVien) {
        ChucVu chucVu = nguoiDung.getChucVuId() == null ? null : chucVuRepository.findById(nguoiDung.getChucVuId()).orElse(null);
        List<String> roleList = roleCodesForUser(nguoiDung, chucVu);

        return new NguoiDungResponse(
                nguoiDung.getId(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getId(),
                nguoiDung.getTenDangNhap(),
                nguoiDung.getPhongBanId(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getCode(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getBirthDate(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getGender(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getIdentityNumber(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getSocialInsurance(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getDegree(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getSpecialty(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getAcademicTitle(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getAcademicTitleName(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getCertificate(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getHonorTitle(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getAvatarImage(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getOnlineBooking(),
                hoSoNhanVien == null ? null : hoSoNhanVien.getActive(),
                nguoiDung.getChucVuId(),
                chucVu == null ? null : chucVu.getMaChucVu(),
                chucVu == null ? null : chucVu.getTenChucVu(),
                roleList,
                AppPermissions.modulesForRoles(roleList),
                nguoiDung.getTrangThai(),
                nguoiDung.getLanDangNhapCuoi()
        );
    }

    private List<String> roleCodesForUser(NguoiDung nguoiDung, ChucVu chucVu) {
        List<String> roleIds = userRoleRepository.findByIdUserId(nguoiDung.getId()).stream()
                .map(userRole -> userRole.getId().getRoleId())
                .toList();
        if (!roleIds.isEmpty()) {
            Map<String, Role> rolesById = roleRepository.findAllById(roleIds).stream()
                    .collect(Collectors.toMap(Role::getId, Function.identity()));
            return roleIds.stream()
                    .map(rolesById::get)
                    .filter(role -> role != null)
                    .map(Role::getCode)
                    .toList();
        }
        String role = chucVu == null ? AppRoles.USER : AppRoles.toPermissionRoleCode(chucVu.getMaChucVu());
        return AppRoles.USER.equals(role) ? List.of() : List.of(role);
    }

    private void syncRoleFromChucVu(NguoiDung nguoiDung) {
        if (nguoiDung.getChucVuId() == null || nguoiDung.getChucVuId().isBlank()) {
            userRoleRepository.deleteByIdUserId(nguoiDung.getId());
            return;
        }
        String roleCode = chucVuRepository.findById(nguoiDung.getChucVuId())
                .map(ChucVu::getMaChucVu)
                .map(AppRoles::toPermissionRoleCode)
                .orElse(AppRoles.USER);
        if (AppRoles.USER.equals(roleCode)) {
            userRoleRepository.deleteByIdUserId(nguoiDung.getId());
            return;
        }
        roleRepository.findByCodeIgnoreCase(roleCode).ifPresent(role -> {
            userRoleRepository.deleteByIdUserId(nguoiDung.getId());
            UserRole userRole = new UserRole();
            userRole.setId(new UserRoleId(nguoiDung.getId(), role.getId()));
            userRoleRepository.save(userRole);
        });
    }

    private void replaceUserRoles(String userId, List<String> roleCodes) {
        List<String> normalizedCodes = roleCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(AppRoles::normalizeRoleCode)
                .distinct()
                .toList();
        Map<String, Role> rolesByCode = roleRepository.findByCodeIn(normalizedCodes).stream()
                .collect(Collectors.toMap(Role::getCode, Function.identity()));
        List<String> missingCodes = normalizedCodes.stream()
                .filter(code -> !rolesByCode.containsKey(code))
                .toList();
        if (!missingCodes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role does not exist: " + missingCodes.get(0));
        }

        userRoleRepository.deleteByIdUserId(userId);
        userRoleRepository.saveAll(normalizedCodes.stream()
                .map(rolesByCode::get)
                .map(role -> {
                    UserRole userRole = new UserRole();
                    userRole.setId(new UserRoleId(userId, role.getId()));
                    return userRole;
                })
                .toList());
    }
}
