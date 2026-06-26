package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.BusinessTripDTOs.*;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.BusinessTrip;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.BusinessTripRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.security.PermissionCatalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:business-trip-integration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class BusinessTripIntegrationTest {

    @Autowired
    private BusinessTripRepository businessTripRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private BusinessTripService businessTripService;

    private NguoiDung employeeUser;
    private HoSoNhanVien employeeProfile;

    private NguoiDung managerUser;
    private HoSoNhanVien managerProfile;

    private NguoiDung otherUser;
    private HoSoNhanVien otherProfile;

    @BeforeEach
    void setUp() {
        // Create manager
        managerUser = new NguoiDung();
        managerUser.setTenDangNhap("manager.trip");
        managerUser.setMatKhauMaHoa("hashed_password");
        managerUser = nguoiDungRepository.save(managerUser);

        managerProfile = new HoSoNhanVien();
        managerProfile.setNguoiDungId(managerUser.getId());
        managerProfile.setCode("EMP-MGR-TRIP");
        managerProfile.setName("Trưởng phòng Du lịch");
        managerProfile.setActive(true);
        managerProfile = hoSoNhanVienRepository.save(managerProfile);

        // Create employee
        employeeUser = new NguoiDung();
        employeeUser.setTenDangNhap("employee.trip");
        employeeUser.setMatKhauMaHoa("hashed_password");
        employeeUser = nguoiDungRepository.save(employeeUser);

        employeeProfile = new HoSoNhanVien();
        employeeProfile.setNguoiDungId(employeeUser.getId());
        employeeProfile.setCode("EMP-TRIP-01");
        employeeProfile.setName("Nhân viên Đi công tác");
        employeeProfile.setDirectManagerId(managerProfile.getId());
        employeeProfile.setActive(true);
        employeeProfile = hoSoNhanVienRepository.save(employeeProfile);

        // Create other employee
        otherUser = new NguoiDung();
        otherUser.setTenDangNhap("employee.trip-other");
        otherUser.setMatKhauMaHoa("hashed_password");
        otherUser = nguoiDungRepository.save(otherUser);

        otherProfile = new HoSoNhanVien();
        otherProfile.setNguoiDungId(otherUser.getId());
        otherProfile.setCode("EMP-TRIP-02");
        otherProfile.setName("Nhân viên khác");
        otherProfile.setActive(true);
        otherProfile = hoSoNhanVienRepository.save(otherProfile);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        businessTripRepository.deleteAllInBatch();
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
    void completeBusinessTripLifecycleFlow() {
        // 1. Authenticate as employee and create a business trip request
        authenticate("employee.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);

        BusinessTripUpsertRequest createReq = new BusinessTripUpsertRequest(
                "Đà Nẵng",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                "Hội nghị khách hàng miền Trung",
                managerProfile.getId()
        );

        BusinessTripResponse created = businessTripService.createTrip(createReq, "employee.trip");
        assertThat(created.id()).isNotNull();
        assertThat(created.status()).isEqualTo("DRAFT");
        assertThat(created.destination()).isEqualTo("Đà Nẵng");
        assertThat(created.employeeName()).isEqualTo("Nhân viên Đi công tác");

        // 2. Update the draft business trip
        BusinessTripUpsertRequest updateReq = new BusinessTripUpsertRequest(
                "Nha Trang",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(6),
                "Hội nghị khách hàng miền Trung và Nam",
                managerProfile.getId()
        );

        BusinessTripResponse updated = businessTripService.updateTrip(created.id(), updateReq, "employee.trip");
        assertThat(updated.destination()).isEqualTo("Nha Trang");
        assertThat(updated.status()).isEqualTo("DRAFT");

        // 3. Submit for approval
        BusinessTripResponse submitted = businessTripService.submitTrip(updated.id(), "employee.trip");
        assertThat(submitted.status()).isEqualTo("PENDING_APPROVAL");

        // 4. Verify other manager/user without manage permission cannot approve
        authenticate("employee.trip-other", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);
        assertThrows(ResponseStatusException.class, () ->
                businessTripService.approveTrip(submitted.id(), "employee.trip-other")
        );

        // 5. Manager approves the trip request
        authenticate("manager.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE);
        BusinessTripResponse approved = businessTripService.approveTrip(submitted.id(), "manager.trip");
        assertThat(approved.status()).isEqualTo("APPROVED");
        assertThat(approved.approverName()).isEqualTo("Trưởng phòng Du lịch");
    }

    @Test
    void rejectBusinessTripFlow() {
        // 1. Employee creates and submits request
        authenticate("employee.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);
        BusinessTripUpsertRequest createReq = new BusinessTripUpsertRequest(
                "Hải Phòng",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                "Khảo sát thị trường chi nhánh",
                managerProfile.getId()
        );
        BusinessTripResponse created = businessTripService.createTrip(createReq, "employee.trip");
        businessTripService.submitTrip(created.id(), "employee.trip");

        // 2. Manager rejects the request
        authenticate("manager.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE);
        BusinessTripRejectRequest rejectReq = new BusinessTripRejectRequest("Tuần này có cuộc họp khẩn cấp tại trụ sở chính.");
        BusinessTripResponse rejected = businessTripService.rejectTrip(created.id(), rejectReq, "manager.trip");

        assertThat(rejected.status()).isEqualTo("REJECTED");
        assertThat(rejected.rejectReason()).isEqualTo("Tuần này có cuộc họp khẩn cấp tại trụ sở chính.");
    }

    @Test
    void activeTripsFilterFlow() {
        // Create an approved trip that overlaps with today
        authenticate("employee.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);
        BusinessTripUpsertRequest activeTripReq = new BusinessTripUpsertRequest(
                "Cần Thơ",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(2),
                "Hỗ trợ đại lý Cần Thơ",
                managerProfile.getId()
        );
        BusinessTripResponse createdActive = businessTripService.createTrip(activeTripReq, "employee.trip");
        businessTripService.submitTrip(createdActive.id(), "employee.trip");

        authenticate("manager.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE);
        businessTripService.approveTrip(createdActive.id(), "manager.trip");

        // Create an approved trip in the future (should not appear in active trips)
        authenticate("employee.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);
        BusinessTripUpsertRequest futureTripReq = new BusinessTripUpsertRequest(
                "Sơn La",
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                "Từ thiện và khảo sát",
                managerProfile.getId()
        );
        BusinessTripResponse createdFuture = businessTripService.createTrip(futureTripReq, "employee.trip");
        businessTripService.submitTrip(createdFuture.id(), "employee.trip");

        authenticate("manager.trip", PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE);
        businessTripService.approveTrip(createdFuture.id(), "manager.trip");

        // Get active trips
        Page<BusinessTripResponse> activeTrips = businessTripService.getActiveTrips(null, 0, 10);
        
        // Assert only Cần Thơ trip is active
        assertThat(activeTrips.getContent()).hasSize(1);
        assertThat(activeTrips.getContent().get(0).destination()).isEqualTo("Cần Thơ");
    }
}
