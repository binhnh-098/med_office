package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.AssetLiquidationDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.repository.AssetLiquidationRepository;
import com.example.med_office.repository.AssetRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:asset-liquidation-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AssetLiquidationIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private AssetLiquidationRepository assetLiquidationRepository;

    @Autowired
    private AssetLiquidationService assetLiquidationService;

    private Asset asset;
    private HoSoNhanVien employee;

    @BeforeEach
    void setUp() {
        assetLiquidationRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();
        hoSoNhanVienRepository.deleteAllInBatch();

        // Setup mock employee
        HoSoNhanVien emp = new HoSoNhanVien();
        emp.setCode("NV001");
        emp.setName("Nguyen Van A");
        employee = hoSoNhanVienRepository.save(emp);

        // Setup mock asset with allocation details
        Asset a = new Asset();
        a.setCode("TS-TEST-LIQ");
        a.setName("Test Asset For Liquidation");
        a.setCategory("Máy tính");
        a.setUnit("Cái");
        a.setStatus("ACTIVE");
        a.setCurrentEmployeeId(employee.getId());
        a.setCurrentDepartment("Khoa Ngoai");
        a.setPurchasePrice(BigDecimal.valueOf(10000000.00));
        asset = assetRepository.save(a);
    }

    @AfterEach
    void tearDown() {
        assetLiquidationRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();
        hoSoNhanVienRepository.deleteAllInBatch();
    }

    @Test
    void testAssetLiquidationCycle() {
        // Step 1: Create Liquidation
        AssetLiquidationRequest req = new AssetLiquidationRequest(
                asset.getId(),
                LocalDate.now(),
                BigDecimal.valueOf(5000000.00),
                "QĐTL-2026-001",
                "Thiet bi cu, hieu nang kem",
                "Ghi chu them ve thanh ly"
        );

        AssetLiquidationResponse res = assetLiquidationService.createLiquidation(req);
        assertThat(res.id()).isNotNull();
        assertThat(res.assetId()).isEqualTo(asset.getId());
        assertThat(res.assetCode()).isEqualTo("TS-TEST-LIQ");
        assertThat(res.assetName()).isEqualTo("Test Asset For Liquidation");
        assertThat(res.price()).isEqualByComparingTo(BigDecimal.valueOf(5000000.00));
        assertThat(res.documentNumber()).isEqualTo("QĐTL-2026-001");
        assertThat(res.reason()).isEqualTo("Thiet bi cu, hieu nang kem");
        assertThat(res.priorStatus()).isEqualTo("ACTIVE");
        assertThat(res.purchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(10000000.00));

        // Verify asset status is LIQUIDATED and holders are cleared (null)
        Asset liquidatedAsset = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(liquidatedAsset.getStatus()).isEqualTo("LIQUIDATED");
        assertThat(liquidatedAsset.getCurrentEmployeeId()).isNull();
        assertThat(liquidatedAsset.getCurrentDepartment()).isNull();

        // Step 2: Search liquidations
        Page<AssetLiquidationResponse> pageResult = assetLiquidationService.getLiquidations(
                asset.getId(),
                "QĐTL",
                0,
                10
        );
        assertThat(pageResult.getTotalElements()).isEqualTo(1);
        assertThat(pageResult.getContent().get(0).documentNumber()).isEqualTo("QĐTL-2026-001");

        // Search by keyword reason
        Page<AssetLiquidationResponse> pageByReason = assetLiquidationService.getLiquidations(
                null,
                "hieu nang",
                0,
                10
        );
        assertThat(pageByReason.getTotalElements()).isEqualTo(1);

        // Step 3: Cancel Liquidation
        assetLiquidationService.cancelLiquidation(res.id());

        // Verify liquidation record is deleted
        assertThat(assetLiquidationRepository.existsById(res.id())).isFalse();

        // Verify asset status reverted back to priorStatus (ACTIVE)
        Asset revertedAsset = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(revertedAsset.getStatus()).isEqualTo("ACTIVE");
    }
}
