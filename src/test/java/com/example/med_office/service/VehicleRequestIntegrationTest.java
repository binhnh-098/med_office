package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.VehicleRequestDTOs.*;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.entity.Vehicle;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.VehicleRepository;
import com.example.med_office.repository.VehicleRequestRepository;
import com.example.med_office.security.PermissionCatalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:vehicle-request-integration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class VehicleRequestIntegrationTest {

    @Autowired
    private VehicleRequestRepository vehicleRequestRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VehicleRequestService vehicleRequestService;

    private NguoiDung employeeUser;
    private HoSoNhanVien employeeProfile;

    private NguoiDung managerUser;
    private HoSoNhanVien managerProfile;

    private Vehicle fixedVehicle;

    @BeforeEach
    void setUp() {
        // Create manager
        managerUser = new NguoiDung();
        managerUser.setTenDangNhap("manager.vehicle");
        managerUser.setMatKhauMaHoa("hashed_password");
        managerUser = nguoiDungRepository.save(managerUser);

        managerProfile = new HoSoNhanVien();
        managerProfile.setNguoiDungId(managerUser.getId());
        managerProfile.setCode("EMP-MGR-VEH");
        managerProfile.setName("Trưởng phòng Hành chính");
        managerProfile.setActive(true);
        managerProfile = hoSoNhanVienRepository.save(managerProfile);

        // Create employee
        employeeUser = new NguoiDung();
        employeeUser.setTenDangNhap("employee.vehicle");
        employeeUser.setMatKhauMaHoa("hashed_password");
        employeeUser = nguoiDungRepository.save(employeeUser);

        employeeProfile = new HoSoNhanVien();
        employeeProfile.setNguoiDungId(employeeUser.getId());
        employeeProfile.setCode("EMP-VEH-01");
        employeeProfile.setName("Nhân viên Đăng ký Xe");
        employeeProfile.setDirectManagerId(managerProfile.getId());
        employeeProfile.setActive(true);
        employeeProfile = hoSoNhanVienRepository.save(employeeProfile);

        // Create vehicle with 4 seats
        fixedVehicle = new Vehicle();
        fixedVehicle.setName("Toyota Corolla");
        fixedVehicle.setLicensePlate("30F-12345");
        fixedVehicle.setDriverName("Tài xế A");
        fixedVehicle.setDriverPhone("0987654321");
        fixedVehicle.setSeatCapacity(4);
        fixedVehicle = vehicleRepository.save(fixedVehicle);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        vehicleRequestRepository.deleteAllInBatch();
        vehicleRepository.deleteAllInBatch();
        hoSoNhanVienRepository.deleteAllInBatch();
        nguoiDungRepository.deleteAllInBatch();
    }

    private void authenticate(String username, String... permissions) {
        List<SimpleGrantedAuthority> authorities = List.of(permissions).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                "N/A",
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void whenPassengerCountExceedsVehicleSeatCapacity_thenThrowException() {
        authenticate("employee.vehicle", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);

        VehicleRequestUpsertRequest createReq = new VehicleRequestUpsertRequest(
                fixedVehicle.getId(),
                "Toyota Corolla",
                LocalDateTime.of(2026, 6, 25, 10, 0),
                LocalDateTime.of(2026, 6, 25, 12, 0),
                "Hanoi - Haiphong",
                5, // Exceeds 4
                "Meeting",
                managerProfile.getId()
        );

        assertThrows(ResponseStatusException.class, () ->
                vehicleRequestService.createRequest(createReq, "employee.vehicle")
        );
    }

    @Test
    void whenOverlappingRequestsExceedSeatCapacity_thenThrowException() {
        authenticate("employee.vehicle", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);

        // 1. Create and submit request 1 (2 passengers, 10:00 - 12:00)
        VehicleRequestUpsertRequest req1 = new VehicleRequestUpsertRequest(
                fixedVehicle.getId(),
                "Toyota Corolla",
                LocalDateTime.of(2026, 6, 25, 10, 0),
                LocalDateTime.of(2026, 6, 25, 12, 0),
                "Route 1",
                2,
                "Purpose 1",
                managerProfile.getId()
        );
        VehicleRequestResponse res1 = vehicleRequestService.createRequest(req1, "employee.vehicle");
        vehicleRequestService.submitRequest(res1.id(), "employee.vehicle");

        // Approve it so it becomes ACTIVE (occupying the seats)
        authenticate("manager.vehicle", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE);
        vehicleRequestService.approveRequest(
                res1.id(),
                new VehicleRequestApproveRequest("Tài xế A", "0987654321", "30F-12345"),
                "manager.vehicle"
        );

        // 2. Try to create request 2 that overlaps and has passengerCount = 3 (Total = 5 > 4 seats)
        authenticate("employee.vehicle", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);
        VehicleRequestUpsertRequest req2 = new VehicleRequestUpsertRequest(
                fixedVehicle.getId(),
                "Toyota Corolla",
                LocalDateTime.of(2026, 6, 25, 11, 0), // Overlaps
                LocalDateTime.of(2026, 6, 25, 13, 0),
                "Route 2",
                3, // Overlapping passenger count = 2 + 3 = 5 > 4
                "Purpose 2",
                managerProfile.getId()
        );

        assertThrows(ResponseStatusException.class, () ->
                vehicleRequestService.createRequest(req2, "employee.vehicle")
        );

        // 3. Try to create request 3 that overlaps but has passengerCount = 2 (Total = 4 <= 4 seats) - should succeed
        VehicleRequestUpsertRequest req3 = new VehicleRequestUpsertRequest(
                fixedVehicle.getId(),
                "Toyota Corolla",
                LocalDateTime.of(2026, 6, 25, 11, 0), // Overlaps
                LocalDateTime.of(2026, 6, 25, 13, 0),
                "Route 3",
                2, // Overlapping passenger count = 2 + 2 = 4 <= 4
                "Purpose 3",
                managerProfile.getId()
        );
        VehicleRequestResponse res3 = vehicleRequestService.createRequest(req3, "employee.vehicle");
        assertThat(res3.id()).isNotNull();
    }
}
