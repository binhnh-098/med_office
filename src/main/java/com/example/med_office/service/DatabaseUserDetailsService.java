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

    public DatabaseUserDetailsService(
            NguoiDungRepository nguoiDungRepository,
            ChucVuRepository chucVuRepository
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.chucVuRepository = chucVuRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var nguoiDung = nguoiDungRepository.findByTenDangNhapAndTrangThaiIgnoreCase(username, "ACTIVE")
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String positionRole = nguoiDung.getChucVuId() == null || nguoiDung.getChucVuId().isBlank()
                ? AppRoles.USER
                : chucVuRepository.findById(nguoiDung.getChucVuId())
                        .map(chucVu -> AppRoles.normalizeRoleCode(chucVu.getMaChucVu()))
                        .orElse(AppRoles.USER);

        return User.withUsername(nguoiDung.getTenDangNhap())
                .password(nguoiDung.getMatKhauMaHoa())
                .authorities(authoritiesFor(positionRole))
                .build();
    }

    private Set<SimpleGrantedAuthority> authoritiesFor(String positionRole) {
        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + AppRoles.USER));
        authorities.add(new SimpleGrantedAuthority("ROLE_" + positionRole));
        return authorities;
    }
}
