package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.ContractDTOs.*;
import com.example.med_office.entity.Contract;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.repository.ContractRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:contract-integration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ContractIntegrationTest {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    private NguoiDung employeeUser1;
    private HoSoNhanVien employeeProfile1;

    private NguoiDung employeeUser2;
    private HoSoNhanVien employeeProfile2;

    @BeforeEach
    void setUp() {
        // Create employee 1
        employeeUser1 = new NguoiDung();
        employeeUser1.setTenDangNhap("employee.one");
        employeeUser1.setMatKhauMaHoa("hashed");
        employeeUser1 = nguoiDungRepository.save(employeeUser1);

        employeeProfile1 = new HoSoNhanVien();
        employeeProfile1.setNguoiDungId(employeeUser1.getId());
        employeeProfile1.setCode("EMP-001");
        employeeProfile1.setName("Nhân viên Một");
        employeeProfile1.setActive(true);
        employeeProfile1 = hoSoNhanVienRepository.save(employeeProfile1);

        // Create employee 2
        employeeUser2 = new NguoiDung();
        employeeUser2.setTenDangNhap("employee.two");
        employeeUser2.setMatKhauMaHoa("hashed");
        employeeUser2 = nguoiDungRepository.save(employeeUser2);

        employeeProfile2 = new HoSoNhanVien();
        employeeProfile2.setNguoiDungId(employeeUser2.getId());
        employeeProfile2.setCode("EMP-002");
        employeeProfile2.setName("Nhân viên Hai");
        employeeProfile2.setActive(true);
        employeeProfile2 = hoSoNhanVienRepository.save(employeeProfile2);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        contractRepository.deleteAllInBatch();
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
    void completeContractLifecycleFlow() {
        // 1. Authenticate as HR to create a contract
        authenticate("employee.one", PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE);

        ContractUpsertRequest createRequest = new ContractUpsertRequest(
                employeeProfile1.getId(),
                "HDLD/2026/001",
                "THU_VIEC",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                new BigDecimal("15000000"),
                "ACTIVE",
                "Thử việc 2 tháng"
        );

        ContractResponse created = contractService.createContract(createRequest, "employee.one");
        assertThat(created.id()).isNotNull();
        assertThat(created.contractNumber()).isEqualTo("HDLD/2026/001");
        assertThat(created.status()).isEqualTo("ACTIVE");

        // 2. Try to create duplicate contract number and expect failure
        assertThrows(ResponseStatusException.class, () ->
                contractService.createContract(createRequest, "employee.one")
        );

        // 3. Authenticate as normal employee (without manage perm) and try to update it -> expect failure
        authenticate("employee.two", PermissionCatalog.EMPLOYEES_CONTRACT_EXPIRING_VIEW);
        ContractUpsertRequest updateRequest = new ContractUpsertRequest(
                employeeProfile1.getId(),
                "HDLD/2026/001-REV",
                "XAC_DINH_THOI_HAN",
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                new BigDecimal("18000000"),
                "ACTIVE",
                "Ký hợp đồng chính thức"
        );
        assertThrows(ResponseStatusException.class, () ->
                contractService.updateContract(created.id(), updateRequest, "employee.two")
        );

        // 4. Authenticate back as HR and update successfully
        authenticate("employee.one", PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE);
        ContractResponse updated = contractService.updateContract(created.id(), updateRequest, "employee.one");
        assertThat(updated.contractNumber()).isEqualTo("HDLD/2026/001-REV");
        assertThat(updated.contractType()).isEqualTo("XAC_DINH_THOI_HAN");
        assertThat(updated.salary()).isEqualByComparingTo("18000000");

        // 5. Test Access Control for Listing:
        // Create another contract for Employee 2
        ContractUpsertRequest createRequest2 = new ContractUpsertRequest(
                employeeProfile2.getId(),
                "HDLD/2026/002",
                "KHONG_THOI_HAN",
                LocalDate.now(),
                null,
                new BigDecimal("22000000"),
                "ACTIVE",
                "Hợp đồng không thời hạn"
        );
        contractService.createContract(createRequest2, "employee.one");

        // HR listings should return both contracts
        Page<ContractResponse> hrList = contractService.getContracts(null, null, null, "employee.one", 0, 10);
        assertThat(hrList.getContent()).hasSize(2);

        // Employee 2 should only see their own contract
        authenticate("employee.two", PermissionCatalog.EMPLOYEES_CONTRACT_EXPIRING_VIEW);
        Page<ContractResponse> empList = contractService.getContracts(null, null, null, "employee.two", 0, 10);
        assertThat(empList.getContent()).hasSize(1);
        assertThat(empList.getContent().get(0).contractNumber()).isEqualTo("HDLD/2026/002");

        // 6. Test dynamic status computation
        authenticate("employee.one", PermissionCatalog.EMPLOYEES_CONTRACT_MANAGE);

        // Expiring Soon (EndDate is within 30 days from now, e.g. plus 15 days)
        ContractUpsertRequest expiringRequest = new ContractUpsertRequest(
                employeeProfile1.getId(),
                "HDLD-EXPIRING",
                "THU_VIEC",
                LocalDate.now().minusDays(15),
                LocalDate.now().plusDays(15),
                new BigDecimal("10000000"),
                "ACTIVE",
                "Sắp hết hạn"
        );
        ContractResponse expiring = contractService.createContract(expiringRequest, "employee.one");
        assertThat(expiring.status()).isEqualTo("EXPIRING_SOON");

        // Expired (EndDate has passed, e.g. end date is yesterday)
        ContractUpsertRequest expiredRequest = new ContractUpsertRequest(
                employeeProfile1.getId(),
                "HDLD-EXPIRED",
                "THU_VIEC",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1),
                new BigDecimal("10000000"),
                "ACTIVE",
                "Đã hết hạn"
        );
        ContractResponse expired = contractService.createContract(expiredRequest, "employee.one");
        assertThat(expired.status()).isEqualTo("EXPIRED");

        // 7. Delete contract as HR
        contractService.deleteContract(expired.id(), "employee.one");
        assertThrows(ResponseStatusException.class, () ->
                contractService.getContractDetail(expired.id(), "employee.one")
        );
    }
}
