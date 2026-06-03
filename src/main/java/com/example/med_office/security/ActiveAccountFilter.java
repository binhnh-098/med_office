package com.example.med_office.security;

import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ActiveAccountFilter extends OncePerRequestFilter {

    private static final String LOCKED_MESSAGE = "Tài khoản đã bị khóa.";

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;

    public ActiveAccountFilter(
            NguoiDungRepository nguoiDungRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isActive = nguoiDungRepository.findByTenDangNhapAndTrangThaiIgnoreCase(authentication.getName(), "ACTIVE")
                .filter(nguoiDung -> hoSoNhanVienRepository.findByNguoiDungId(nguoiDung.getId())
                        .map(hoSoNhanVien -> !Boolean.FALSE.equals(hoSoNhanVien.getActive()))
                        .orElse(true))
                .isPresent();
        if (isActive) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {
                  "code": 403,
                  "message": "Tài khoản đã bị khóa.",
                  "data": null
                }
                """.trim());
    }
}
