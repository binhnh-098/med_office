package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.AssetDTOs.*;
import com.example.med_office.dto.AssetHandoverDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.repository.AssetHandoverRepository;
import com.example.med_office.repository.AssetRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:asset-handover-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AssetHandoverIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private AssetHandoverRepository assetHandoverRepository;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetHandoverService assetHandoverService;

    private Asset asset;
    private HoSoNhanVien employee1;
    private HoSoNhanVien employee2;

    @BeforeEach
    void setUp() {
        assetHandoverRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();
        hoSoNhanVienRepository.deleteAllInBatch();

        // 1. Setup mock asset
        Asset a = new Asset();
        a.setCode("TS-TEST-01");
        a.setName("Test Asset");
        a.setCategory("Máy tính");
        a.setUnit("Cái");
        asset = assetRepository.save(a);

        // 2. Setup mock employees
        HoSoNhanVien e1 = new HoSoNhanVien();
        e1.setCode("NV001");
        e1.setName("Nguyen Van A");
        employee1 = hoSoNhanVienRepository.save(e1);

        HoSoNhanVien e2 = new HoSoNhanVien();
        e2.setCode("NV002");
        e2.setName("Tran Van B");
        employee2 = hoSoNhanVienRepository.save(e2);
    }

    @AfterEach
    void tearDown() {
        assetHandoverRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();
        hoSoNhanVienRepository.deleteAllInBatch();
    }

    @Test
    void testAssetHandoverTransferReclaimFlow() {
        // Step 1: Handover from stock to Employee 1 / Khoa Noi
        AssetHandoverUpsertRequest handoverReq = new AssetHandoverUpsertRequest(
                asset.getId(),
                "HANDOVER",
                employee1.getId(),
                "Khoa Nội",
                LocalDate.now(),
                "BB-BG-01",
                "Bàn giao máy tính làm việc"
        );

        AssetHandoverResponse handoverRes = assetHandoverService.createHandover(handoverReq);
        assertThat(handoverRes.id()).isNotNull();
        assertThat(handoverRes.type()).isEqualTo("HANDOVER");
        assertThat(handoverRes.toEmployeeId()).isEqualTo(employee1.getId());
        assertThat(handoverRes.toEmployeeName()).isEqualTo("Nguyen Van A");
        assertThat(handoverRes.toDepartment()).isEqualTo("Khoa Nội");
        assertThat(handoverRes.fromEmployeeId()).isNull();

        // Verify asset current state is updated
        Asset updatedAsset1 = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(updatedAsset1.getCurrentEmployeeId()).isEqualTo(employee1.getId());
        assertThat(updatedAsset1.getCurrentDepartment()).isEqualTo("Khoa Nội");

        // Step 2: Transfer from Employee 1 to Employee 2 / Khoa Ngoại
        AssetHandoverUpsertRequest transferReq = new AssetHandoverUpsertRequest(
                asset.getId(),
                "TRANSFER",
                employee2.getId(),
                "Khoa Ngoại",
                LocalDate.now(),
                "BB-DC-01",
                "Điều chuyển do đổi vị trí làm việc"
        );

        AssetHandoverResponse transferRes = assetHandoverService.createHandover(transferReq);
        assertThat(transferRes.id()).isNotNull();
        assertThat(transferRes.type()).isEqualTo("TRANSFER");
        assertThat(transferRes.fromEmployeeId()).isEqualTo(employee1.getId());
        assertThat(transferRes.fromEmployeeName()).isEqualTo("Nguyen Van A");
        assertThat(transferRes.fromDepartment()).isEqualTo("Khoa Nội");
        assertThat(transferRes.toEmployeeId()).isEqualTo(employee2.getId());
        assertThat(transferRes.toEmployeeName()).isEqualTo("Tran Van B");
        assertThat(transferRes.toDepartment()).isEqualTo("Khoa Ngoại");

        // Verify asset current state is updated
        Asset updatedAsset2 = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(updatedAsset2.getCurrentEmployeeId()).isEqualTo(employee2.getId());
        assertThat(updatedAsset2.getCurrentDepartment()).isEqualTo("Khoa Ngoại");

        // Step 3: Reclaim from Employee 2 back to inventory
        AssetHandoverUpsertRequest reclaimReq = new AssetHandoverUpsertRequest(
                asset.getId(),
                "RECLAIM",
                null,
                null,
                LocalDate.now(),
                "BB-TH-01",
                "Thu hồi lưu kho"
        );

        AssetHandoverResponse reclaimRes = assetHandoverService.createHandover(reclaimReq);
        assertThat(reclaimRes.id()).isNotNull();
        assertThat(reclaimRes.type()).isEqualTo("RECLAIM");
        assertThat(reclaimRes.fromEmployeeId()).isEqualTo(employee2.getId());
        assertThat(reclaimRes.fromEmployeeName()).isEqualTo("Tran Van B");
        assertThat(reclaimRes.fromDepartment()).isEqualTo("Khoa Ngoại");
        assertThat(reclaimRes.toEmployeeId()).isNull();
        assertThat(reclaimRes.toDepartment()).isNull();

        // Verify asset current state is cleared
        Asset updatedAsset3 = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(updatedAsset3.getCurrentEmployeeId()).isNull();
        assertThat(updatedAsset3.getCurrentDepartment()).isNull();

        // Step 4: Search handovers list
        Page<AssetHandoverResponse> pageResult = assetHandoverService.getHandovers(asset.getId(), null, null, 0, 10);
        assertThat(pageResult.getTotalElements()).isEqualTo(3);
    }
}
