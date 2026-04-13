package com.example.med_office.service;

import com.example.med_office.repository.NguoiDungRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    public DatabaseUserDetailsService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var nguoiDung = nguoiDungRepository.findByTenDangNhapAndTrangThaiIgnoreCase(username, "ACTIVE")
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(nguoiDung.getTenDangNhap())
                .password(nguoiDung.getMatKhauMaHoa())
                .roles("USER")
                .build();
    }
}
