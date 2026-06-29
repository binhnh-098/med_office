package com.example.med_office.config;

import com.example.med_office.security.ActiveAccountFilter;
import com.example.med_office.security.PermissionCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint unauthorizedEntryPoint,
            AccessDeniedHandler accessDeniedHandler,
            ActiveAccountFilter activeAccountFilter
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
                        .requestMatchers(HttpMethod.POST, "/api/signup").hasAuthority(PermissionCatalog.SYSTEM_ACCOUNTS_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/nguoi-dung", "/api/nguoi-dung/**")
                        .hasAuthority(PermissionCatalog.SYSTEM_ACCOUNTS_VIEW)
                        .requestMatchers(HttpMethod.PUT, "/api/nguoi-dung/**")
                        .hasAuthority(PermissionCatalog.SYSTEM_ACCOUNTS_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/kho", "/api/kho/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_VIEW, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/kho", "/api/kho/**")
                        .hasAuthority(PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.PUT, "/api/kho/**")
                        .hasAuthority(PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/warehouses", "/api/warehouses/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_VIEW, PermissionCatalog.WAREHOUSE_MANAGE, PermissionCatalog.WAREHOUSE_INBOUND_VIEW, PermissionCatalog.WAREHOUSE_OUTBOUND_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-inbounds/*/submit")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INBOUND_SUBMIT, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-inbounds/*/approve")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INBOUND_APPROVE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-inbounds/*/reject")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INBOUND_REJECT, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-inbounds/*/complete")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INBOUND_COMPLETE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/warehouse-inbounds", "/api/warehouse-inbounds/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INBOUND_VIEW, PermissionCatalog.WAREHOUSE_VIEW, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-inbounds", "/api/warehouse-inbounds/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INBOUND_CREATE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.PUT, "/api/warehouse-inbounds/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INBOUND_UPDATE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-outbounds/*/submit")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_SUBMIT, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-outbounds/*/approve")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_APPROVE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-outbounds/*/reject")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_REJECT, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-outbounds/*/complete")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_COMPLETE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.DELETE, "/api/warehouse-outbounds/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_CREATE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/warehouse-outbounds", "/api/warehouse-outbounds/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_VIEW, PermissionCatalog.WAREHOUSE_VIEW, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/api/warehouse-outbounds", "/api/warehouse-outbounds/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_CREATE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.PUT, "/api/warehouse-outbounds/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_OUTBOUND_UPDATE, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/warehouse-inventories", "/api/warehouse-inventories/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INVENTORY_VIEW, PermissionCatalog.WAREHOUSE_VIEW, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/inventory-balances", "/api/inventory-balances/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INVENTORY_VIEW, PermissionCatalog.WAREHOUSE_VIEW, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/inventory-items", "/api/inventory-items/**")
                        .hasAnyAuthority(PermissionCatalog.WAREHOUSE_INVENTORY_VIEW, PermissionCatalog.WAREHOUSE_VIEW, PermissionCatalog.WAREHOUSE_MANAGE)
                        .requestMatchers("/api/admin/**").hasAuthority(PermissionCatalog.SYSTEM_PERMISSIONS_MANAGE)
                        .requestMatchers("/api/leave-requests/balance").authenticated()
                        .requestMatchers("/api/leave-requests", "/api/leave-requests/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_LEAVE_VIEW)
                        .requestMatchers("/api/business-trips/active").hasAnyAuthority(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW, PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE)
                        .requestMatchers("/api/business-trips/approvals", "/api/business-trips/*/approve", "/api/business-trips/*/reject")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE)
                        .requestMatchers("/api/business-trips", "/api/business-trips/**")
                        .hasAnyAuthority(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW, PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE)
                        .requestMatchers("/api/vehicle-requests/approvals", "/api/vehicle-requests/*/approve", "/api/vehicle-requests/*/reject")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE)
                        .requestMatchers("/api/vehicle-requests", "/api/vehicle-requests/**")
                        .hasAnyAuthority(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW, PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/vehicles", "/api/vehicles/*", "/api/vehicles/all")
                        .hasAnyAuthority(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW, PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE)
                        .requestMatchers("/api/vehicles", "/api/vehicles/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/assets", "/api/assets/*", "/api/assets/all")
                        .hasAnyAuthority(PermissionCatalog.CATALOGS_ASSET_VIEW, PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers("/api/assets", "/api/assets/**")
                        .hasAuthority(PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/asset-handovers", "/api/asset-handovers/*")
                        .hasAnyAuthority(PermissionCatalog.CATALOGS_ASSET_VIEW, PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers("/api/asset-handovers", "/api/asset-handovers/**")
                        .hasAuthority(PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/asset-maintenances", "/api/asset-maintenances/*")
                        .hasAnyAuthority(PermissionCatalog.CATALOGS_ASSET_VIEW, PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers("/api/asset-maintenances", "/api/asset-maintenances/**")
                        .hasAuthority(PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/asset-inventories", "/api/asset-inventories/*")
                        .hasAnyAuthority(PermissionCatalog.CATALOGS_ASSET_VIEW, PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers("/api/asset-inventories", "/api/asset-inventories/**")
                        .hasAuthority(PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/asset-liquidations", "/api/asset-liquidations/*")
                        .hasAnyAuthority(PermissionCatalog.CATALOGS_ASSET_VIEW, PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers("/api/asset-liquidations", "/api/asset-liquidations/**")
                        .hasAuthority(PermissionCatalog.CATALOGS_ASSET_MANAGE)
                        .requestMatchers("/api/chuc-vu/**").hasAuthority(PermissionCatalog.EMPLOYEES_ORGANIZATION_VIEW)
                        .requestMatchers(HttpMethod.GET, "/api/chuyen-khoa", "/api/chuyen-khoa/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_ORGANIZATION_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/chuyen-khoa", "/api/chuyen-khoa/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_DIRECTORY_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/chuyen-khoa/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_DIRECTORY_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/chuyen-khoa/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_DIRECTORY_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/ho-so-nhan-vien", "/api/ho-so-nhan-vien/**")
                        .hasAnyAuthority(
                                PermissionCatalog.EMPLOYEES_DIRECTORY_VIEW,
                                PermissionCatalog.WAREHOUSE_VIEW,
                                PermissionCatalog.DOCUMENTS_INCOMING_VIEW,
                                PermissionCatalog.DOCUMENTS_OUTGOING_VIEW
                        )
                        .requestMatchers(HttpMethod.POST, "/api/ho-so-nhan-vien", "/api/ho-so-nhan-vien/**")
                        .hasAnyAuthority(PermissionCatalog.EMPLOYEES_CREATE, PermissionCatalog.EMPLOYEES_DIRECTORY_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/ho-so-nhan-vien/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_DIRECTORY_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/ho-so-nhan-vien/**")
                        .hasAuthority(PermissionCatalog.EMPLOYEES_DIRECTORY_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/cong-van-den", "/api/cong-van-den/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_INCOMING_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/cong-van-den", "/api/cong-van-den/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_INCOMING_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/cong-van-den/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_INCOMING_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/cong-van-den/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_INCOMING_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/cong-van-di", "/api/cong-van-di/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_OUTGOING_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/cong-van-di", "/api/cong-van-di/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_OUTGOING_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/cong-van-di/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_OUTGOING_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/reference-documents/**")
                        .hasAuthority(PermissionCatalog.DOCUMENTS_REFERENCE_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/doctor-meals/dishes", "/api/doctor-meals/dishes/**")
                        .hasAuthority(PermissionCatalog.MEALS_WEEKLY_MENU_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/doctor-meals/dishes/**")
                        .hasAuthority(PermissionCatalog.MEALS_WEEKLY_MENU_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/doctor-meals/dishes/**")
                        .hasAuthority(PermissionCatalog.MEALS_WEEKLY_MENU_UPDATE)
                        .requestMatchers(HttpMethod.POST, "/api/doctor-meals/registrations", "/api/doctor-meals/registrations/**")
                        .hasAuthority(PermissionCatalog.MEALS_DOCTOR_UPDATE)
                        .requestMatchers("/api/doctor-meals/**").hasAuthority(PermissionCatalog.MEALS_DOCTOR_VIEW)
                        .requestMatchers(HttpMethod.GET, "/api/meals/weekly-menu", "/api/meals/weekly-menu/**")
                        .hasAuthority(PermissionCatalog.MEALS_WEEKLY_MENU_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/meals/weekly-menu", "/api/meals/weekly-menu/**")
                        .hasAuthority(PermissionCatalog.MEALS_WEEKLY_MENU_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/meals/weekly-menu/**")
                        .hasAuthority(PermissionCatalog.MEALS_WEEKLY_MENU_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/meals/doctor", "/api/meals/doctor/**")
                        .hasAuthority(PermissionCatalog.MEALS_DOCTOR_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/meals/doctor", "/api/meals/doctor/**")
                        .hasAuthority(PermissionCatalog.MEALS_DOCTOR_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/meals/doctor/**")
                        .hasAuthority(PermissionCatalog.MEALS_DOCTOR_UPDATE)
                        .requestMatchers(HttpMethod.GET, "/api/meals/patient", "/api/meals/patient/**")
                        .hasAuthority(PermissionCatalog.MEALS_PATIENT_VIEW)
                        .requestMatchers(HttpMethod.POST, "/api/meals/patient", "/api/meals/patient/**")
                        .hasAuthority(PermissionCatalog.MEALS_PATIENT_UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/meals/patient/**")
                        .hasAuthority(PermissionCatalog.MEALS_PATIENT_UPDATE)
                        .requestMatchers("/api/rowboat/**").hasAuthority(PermissionCatalog.OVERVIEW_DASHBOARD_VIEW)
                        .requestMatchers(HttpMethod.GET, "/api/integration/channels", "/api/integration/sync-logs")
                        .hasAnyAuthority(
                                PermissionCatalog.INTEGRATION_CONNECTIONS_VIEW,
                                PermissionCatalog.INTEGRATION_LOGS_VIEW,
                                PermissionCatalog.INTEGRATION_ERRORS_VIEW,
                                PermissionCatalog.INTEGRATION_MANAGE
                        )
                        .requestMatchers("/api/integration", "/api/integration/**")
                        .hasAuthority(PermissionCatalog.INTEGRATION_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/sales-orders", "/api/sales-orders/**")
                        .hasAuthority(PermissionCatalog.SALES_ORDERS_VIEW)
                        .requestMatchers("/api/sales-orders", "/api/sales-orders/**")
                        .hasAuthority(PermissionCatalog.SALES_ORDERS_MANAGE)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(activeAccountFilter, AuthorizationFilter.class)
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
                      "message": "Bạn cần đăng nhập để tiếp tục.",
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
                      "message": "Bạn không có quyền thực hiện chức năng này.",
                      "data": null
                    }
                    """.trim());
        };
    }
}
