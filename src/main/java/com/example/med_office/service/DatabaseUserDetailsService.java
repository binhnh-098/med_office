package com.example.med_office.service;

import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.security.AppRoles;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;
    private final ChucVuRepository chucVuRepository;
    private final AccessControlService accessControlService;

    public DatabaseUserDetailsService(
            NguoiDungRepository nguoiDungRepository,
            ChucVuRepository chucVuRepository,
            AccessControlService accessControlService
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
        this.accessControlService = accessControlService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var nguoiDung = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isActive = "ACTIVE".equalsIgnoreCase(nguoiDung.getTrangThai());
        String positionRole = nguoiDung.getChucVuId() == null || nguoiDung.getChucVuId().isBlank()
                ? AppRoles.USER
                : chucVuRepository.findById(nguoiDung.getChucVuId())
                        .map(chucVu -> AppRoles.normalizeRoleCode(chucVu.getMaChucVu()))
                        .orElse(AppRoles.USER);

        var roles = accessControlService.resolveRolesForUser(nguoiDung.getId(), positionRole);
        var permissions = accessControlService.getPermissionCodesForRoles(roles);

        return User.withUsername(nguoiDung.getTenDangNhap())
                .password(nguoiDung.getMatKhauMaHoa())
                .disabled(!isActive)
                .authorities(authoritiesFor(roles.stream().map(role -> role.getCode()).toList(), permissions))
                .build();
    }

    private Set<SimpleGrantedAuthority> authoritiesFor(Iterable<String> roles, Iterable<String> permissions) {
        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + AppRoles.normalizeRoleCode(role))));
        permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        return authorities;
    }
}
