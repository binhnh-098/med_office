package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.LeaveRequestDTOs.*;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.LeaveRequest;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.LeaveRequestRepository;
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
        "spring.datasource.url=jdbc:h2:mem:leave-request-integration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class LeaveRequestIntegrationTest {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private LeaveRequestService leaveRequestService;

    private NguoiDung employeeUser;
    private HoSoNhanVien employeeProfile;

    private NguoiDung managerUser;
    private HoSoNhanVien managerProfile;

    private NguoiDung otherUser;
    private HoSoNhanVien otherProfile;

    @BeforeEach
    void setUp() {
        // Create direct manager
        managerUser = new NguoiDung();
        managerUser.setTenDangNhap("manager.one");
        managerUser.setMatKhauMaHoa("hashed_password");
        managerUser = nguoiDungRepository.save(managerUser);

        managerProfile = new HoSoNhanVien();
        managerProfile.setNguoiDungId(managerUser.getId());
        managerProfile.setCode("EMP-MGR");
        managerProfile.setName("Trưởng phòng A");
        managerProfile.setActive(true);
        managerProfile = hoSoNhanVienRepository.save(managerProfile);

        // Create employee under manager
        employeeUser = new NguoiDung();
        employeeUser.setTenDangNhap("employee.one");
        employeeUser.setMatKhauMaHoa("hashed_password");
        employeeUser = nguoiDungRepository.save(employeeUser);

        employeeProfile = new HoSoNhanVien();
        employeeProfile.setNguoiDungId(employeeUser.getId());
        employeeProfile.setCode("EMP-001");
        employeeProfile.setName("Nhân viên B");
        employeeProfile.setDirectManagerId(managerProfile.getId());
        employeeProfile.setAnnualLeaveTotal(12.0);
        employeeProfile.setAnnualLeaveUsed(0.0);
        employeeProfile.setActive(true);
        employeeProfile = hoSoNhanVienRepository.save(employeeProfile);

        // Create another independent employee
        otherUser = new NguoiDung();
        otherUser.setTenDangNhap("employee.other");
        otherUser.setMatKhauMaHoa("hashed_password");
        otherUser = nguoiDungRepository.save(otherUser);

        otherProfile = new HoSoNhanVien();
        otherProfile.setNguoiDungId(otherUser.getId());
        otherProfile.setCode("EMP-002");
        otherProfile.setName("Nhân viên C");
        otherProfile.setAnnualLeaveTotal(10.0);
        otherProfile.setAnnualLeaveUsed(0.0);
        otherProfile.setActive(true);
        otherProfile = hoSoNhanVienRepository.save(otherProfile);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        leaveRequestRepository.deleteAllInBatch();
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
    void completeLeaveRequestLifecycleFlow() {
        // 1. Authenticate as employee and get initial balance
        authenticate("employee.one");
        LeaveBalanceResponse initialBalance = leaveRequestService.getLeaveBalance("employee.one");
        assertThat(initialBalance.annualLeaveTotal()).isEqualTo(12.0);
        assertThat(initialBalance.annualLeaveUsed()).isEqualTo(0.0);
        assertThat(initialBalance.annualLeaveRemaining()).isEqualTo(12.0);
        assertThat(initialBalance.hasSubordinates()).isFalse();

        // Check that manager has subordinates flag set
        LeaveBalanceResponse managerBalance = leaveRequestService.getLeaveBalance("manager.one");
        assertThat(managerBalance.hasSubordinates()).isTrue();

        // 2. Create draft leave request
        LeaveRequestUpsertRequest upsertRequest = new LeaveRequestUpsertRequest(
                "ANNUAL",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 3),
                3.0,
                "Đi du lịch hè với gia đình",
                null,
                null
        );
        LeaveRequestResponse created = leaveRequestService.createLeaveRequest(upsertRequest, "employee.one");
        assertThat(created.id()).isNotNull();
        assertThat(created.status()).isEqualTo("DRAFT");
        assertThat(created.totalDays()).isEqualTo(3.0);
        assertThat(created.hoSoNhanVienId()).isEqualTo(employeeProfile.getId());

        // 3. Update draft leave request
        LeaveRequestUpsertRequest updateRequest = new LeaveRequestUpsertRequest(
                "ANNUAL",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 2),
                2.0,
                "Đi du lịch hè với gia đình - Rút ngắn thời gian",
                null,
                null
        );
        LeaveRequestResponse updated = leaveRequestService.updateLeaveRequest(created.id(), updateRequest, "employee.one");
        assertThat(updated.status()).isEqualTo("DRAFT");
        assertThat(updated.totalDays()).isEqualTo(2.0);
        assertThat(updated.reason()).contains("Rút ngắn");

        // 4. Submit leave request for approval
        LeaveRequestResponse submitted = leaveRequestService.submitLeaveRequest(updated.id(), "employee.one");
        assertThat(submitted.status()).isEqualTo("PENDING_APPROVAL");

        // 5. Verify independent manager cannot approve
        authenticate("employee.other");
        assertThrows(ResponseStatusException.class, () ->
                leaveRequestService.approveLeaveRequest(submitted.id(), "employee.other")
        );

        // 6. Direct manager approves leave request
        authenticate("manager.one");
        LeaveRequestResponse approved = leaveRequestService.approveLeaveRequest(submitted.id(), "manager.one");
        assertThat(approved.status()).isEqualTo("APPROVED");
        assertThat(approved.approverId()).isEqualTo(managerProfile.getId());
        assertThat(approved.approverName()).isEqualTo(managerProfile.getName());

        // 7. Verify balance is correctly updated
        authenticate("employee.one");
        LeaveBalanceResponse updatedBalance = leaveRequestService.getLeaveBalance("employee.one");
        assertThat(updatedBalance.annualLeaveUsed()).isEqualTo(2.0);
        assertThat(updatedBalance.annualLeaveRemaining()).isEqualTo(10.0);
    }

    @Test
    void rejectLeaveRequestLifecycleFlow() {
        // 1. Authenticate employee, create and submit request
        authenticate("employee.one");
        LeaveRequestUpsertRequest upsertRequest = new LeaveRequestUpsertRequest(
                "ANNUAL",
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 12),
                3.0,
                "Việc riêng gia đình",
                null,
                null
        );
        LeaveRequestResponse created = leaveRequestService.createLeaveRequest(upsertRequest, "employee.one");
        leaveRequestService.submitLeaveRequest(created.id(), "employee.one");

        // 2. Direct manager rejects the request
        authenticate("manager.one");
        LeaveRequestRejectRequest rejectRequest = new LeaveRequestRejectRequest("Dự án đang trong giai đoạn nước rút, không thể nghỉ phép.");
        LeaveRequestResponse rejected = leaveRequestService.rejectLeaveRequest(created.id(), rejectRequest, "manager.one");

        assertThat(rejected.status()).isEqualTo("REJECTED");
        assertThat(rejected.rejectReason()).isEqualTo("Dự án đang trong giai đoạn nước rút, không thể nghỉ phép.");
        assertThat(rejected.approverId()).isEqualTo(managerProfile.getId());

        // 3. Verify balance is NOT deducted
        authenticate("employee.one");
        LeaveBalanceResponse balance = leaveRequestService.getLeaveBalance("employee.one");
        assertThat(balance.annualLeaveUsed()).isEqualTo(0.0);
    }

    @Test
    void rejectWhenExceedingAnnualLeaveBalance() {
        // 1. Employee tries to request 15 days of annual leave (only has 12)
        authenticate("employee.one");
        LeaveRequestUpsertRequest upsertRequest = new LeaveRequestUpsertRequest(
                "ANNUAL",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 15),
                15.0,
                "Nghỉ phép dài ngày",
                null,
                null
        );
        LeaveRequestResponse created = leaveRequestService.createLeaveRequest(upsertRequest, "employee.one");
        leaveRequestService.submitLeaveRequest(created.id(), "employee.one");

        // 2. Manager tries to approve but fails due to insufficient balance
        authenticate("manager.one");
        assertThrows(ResponseStatusException.class, () ->
                leaveRequestService.approveLeaveRequest(created.id(), "manager.one")
        );
    }

    @Test
    void hrAdminCanApproveAnyRequest() {
        // 1. Employee creates and submits request
        authenticate("employee.one");
        LeaveRequestUpsertRequest upsertRequest = new LeaveRequestUpsertRequest(
                "PERSONAL",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 2),
                2.0,
                "Việc riêng",
                null,
                null
        );
        LeaveRequestResponse created = leaveRequestService.createLeaveRequest(upsertRequest, "employee.one");
        leaveRequestService.submitLeaveRequest(created.id(), "employee.one");

        // 2. Authenticate as independent user who has HR leave manage authority
        authenticate("employee.other", PermissionCatalog.EMPLOYEES_LEAVE_MANAGE);
        LeaveRequestResponse approved = leaveRequestService.approveLeaveRequest(created.id(), "employee.other");
        assertThat(approved.status()).isEqualTo("APPROVED");
        assertThat(approved.approverId()).isEqualTo(otherProfile.getId());
    }

    @Test
    void listRequestsFiltersCorrectlyByRoleAndScope() {
        // 1. Employee 1 submits a request
        authenticate("employee.one");
        LeaveRequestUpsertRequest upsert1 = new LeaveRequestUpsertRequest(
                "ANNUAL", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 2), 2.0, "Nghỉ phép 1",
                null, null
        );
        LeaveRequestResponse r1 = leaveRequestService.createLeaveRequest(upsert1, "employee.one");
        leaveRequestService.submitLeaveRequest(r1.id(), "employee.one");

        // 2. Employee 2 (other employee) submits a request
        authenticate("employee.other");
        LeaveRequestUpsertRequest upsert2 = new LeaveRequestUpsertRequest(
                "ANNUAL", LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 6), 2.0, "Nghỉ phép 2",
                null, null
        );
        LeaveRequestResponse r2 = leaveRequestService.createLeaveRequest(upsert2, "employee.other");
        leaveRequestService.submitLeaveRequest(r2.id(), "employee.other");

        // 3. Employee 1 lists requests (should only see their own)
        authenticate("employee.one");
        Page<LeaveRequestResponse> myLeaves = leaveRequestService.getLeaveRequests(
                null, null, null, "employee.one", 0, 10
        );
        assertThat(myLeaves.getContent()).hasSize(1);
        assertThat(myLeaves.getContent().getFirst().id()).isEqualTo(r1.id());

        // 4. Manager 1 lists requests (should see subordinates requests)
        authenticate("manager.one");
        Page<LeaveRequestResponse> subordinateLeaves = leaveRequestService.getLeaveRequests(
                null, null, null, "manager.one", 0, 10
        );
        assertThat(subordinateLeaves.getContent()).hasSize(1);
        assertThat(subordinateLeaves.getContent().getFirst().id()).isEqualTo(r1.id());

        // 5. Admin lists requests (should see all requests)
        authenticate("employee.other", PermissionCatalog.EMPLOYEES_LEAVE_MANAGE);
        Page<LeaveRequestResponse> allLeaves = leaveRequestService.getLeaveRequests(
                null, null, null, "employee.other", 0, 10
        );
        assertThat(allLeaves.getContent()).hasSize(2);
    }

    @Test
    void testLeaveRequestWithAssignedApproverAndHandover() {
        // 1. Employee creates and submits request, assigning "other" as the approver and manager as handover
        authenticate("employee.one");
        LeaveRequestUpsertRequest upsertRequest = new LeaveRequestUpsertRequest(
                "ANNUAL",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2),
                2.0,
                "Nghỉ phép có bàn giao",
                otherProfile.getId(),
                managerProfile.getId()
        );
        LeaveRequestResponse created = leaveRequestService.createLeaveRequest(upsertRequest, "employee.one");
        assertThat(created.approverId()).isEqualTo(otherProfile.getId());
        assertThat(created.approverName()).isEqualTo(otherProfile.getName());
        assertThat(created.handoverEmployeeId()).isEqualTo(managerProfile.getId());
        assertThat(created.handoverEmployeeName()).isEqualTo(managerProfile.getName());

        leaveRequestService.submitLeaveRequest(created.id(), "employee.one");

        // 2. The assigned approver (employee.other) should be able to see the details
        authenticate("employee.other");
        LeaveRequestResponse detail = leaveRequestService.getLeaveRequestDetail(created.id(), "employee.other");
        assertThat(detail.id()).isEqualTo(created.id());

        // 3. The assigned approver approves it
        LeaveRequestResponse approved = leaveRequestService.approveLeaveRequest(created.id(), "employee.other");
        assertThat(approved.status()).isEqualTo("APPROVED");
        assertThat(approved.approverId()).isEqualTo(otherProfile.getId());
    }
}
