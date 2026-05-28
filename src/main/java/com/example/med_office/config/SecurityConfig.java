package com.example.med_office.config;

import com.example.med_office.security.AppPermissions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint unauthorizedEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/login",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/signup").hasAnyRole(AppPermissions.ADMIN_ROLES)
                        .requestMatchers("/api/nguoi-dung/**").hasAnyRole(AppPermissions.ADMIN_ROLES)
                        .requestMatchers("/api/chuc-vu/**").hasAnyRole(AppPermissions.ADMIN_ROLES)
                        .requestMatchers(HttpMethod.GET, "/api/chuyen-khoa", "/api/chuyen-khoa/**")
                        .hasAnyRole(AppPermissions.STAFF_ROLES)
                        .requestMatchers(HttpMethod.POST, "/api/chuyen-khoa", "/api/chuyen-khoa/**")
                        .hasAnyRole(AppPermissions.SPECIALTY_WRITE_ROLES)
                        .requestMatchers(HttpMethod.PUT, "/api/chuyen-khoa/**")
                        .hasAnyRole(AppPermissions.SPECIALTY_WRITE_ROLES)
                        .requestMatchers(HttpMethod.DELETE, "/api/chuyen-khoa/**")
                        .hasAnyRole(AppPermissions.SPECIALTY_WRITE_ROLES)
                        .requestMatchers(HttpMethod.GET, "/api/ho-so-nhan-vien", "/api/ho-so-nhan-vien/**")
                        .hasAnyRole(AppPermissions.PROFILE_READ_ROLES)
                        .requestMatchers(HttpMethod.POST, "/api/ho-so-nhan-vien", "/api/ho-so-nhan-vien/**")
                        .hasAnyRole(AppPermissions.PROFILE_WRITE_ROLES)
                        .requestMatchers(HttpMethod.PUT, "/api/ho-so-nhan-vien/**")
                        .hasAnyRole(AppPermissions.PROFILE_WRITE_ROLES)
                        .requestMatchers(HttpMethod.DELETE, "/api/ho-so-nhan-vien/**")
                        .hasAnyRole(AppPermissions.PROFILE_WRITE_ROLES)
                        .requestMatchers("/api/cong-van-den/**", "/api/cong-van-di/**")
                        .hasAnyRole(AppPermissions.DOCUMENT_ROLES)
                        .requestMatchers(HttpMethod.POST, "/api/doctor-meals/dishes", "/api/doctor-meals/dishes/**")
                        .hasAnyRole(AppPermissions.MEAL_MENU_WRITE_ROLES)
                        .requestMatchers(HttpMethod.PUT, "/api/doctor-meals/dishes/**")
                        .hasAnyRole(AppPermissions.MEAL_MENU_WRITE_ROLES)
                        .requestMatchers(HttpMethod.DELETE, "/api/doctor-meals/dishes/**")
                        .hasAnyRole(AppPermissions.MEAL_MENU_WRITE_ROLES)
                        .requestMatchers("/api/doctor-meals/**").hasAnyRole(AppPermissions.MEAL_ROLES)
                        .requestMatchers("/api/rowboat/**").hasAnyRole(AppPermissions.ROWBOAT_ROLES)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "code": 401,
                      "message": "Authentication is required",
                      "data": null
                    }
                    """.trim());
        };
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "code": 403,
                      "message": "Access is denied",
                      "data": null
                    }
                    """.trim());
        };
    }
}
