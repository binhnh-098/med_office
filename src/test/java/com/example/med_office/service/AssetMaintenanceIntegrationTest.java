package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.AssetMaintenanceDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.repository.AssetMaintenanceRepository;
import com.example.med_office.repository.AssetRepository;
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
        "spring.datasource.url=jdbc:h2:mem:asset-maintenance-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AssetMaintenanceIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetMaintenanceRepository assetMaintenanceRepository;

    @Autowired
    private AssetMaintenanceService assetMaintenanceService;

    private Asset asset;

    @BeforeEach
    void setUp() {
        assetMaintenanceRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();

        // Setup mock asset
        Asset a = new Asset();
        a.setCode("TS-TEST-02");
        a.setName("Test Asset For Maintenance");
        a.setCategory("Điều hòa");
        a.setUnit("Cái");
        a.setStatus("ACTIVE");
        asset = assetRepository.save(a);
    }

    @AfterEach
    void tearDown() {
        assetMaintenanceRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();
    }

    @Test
    void testAssetMaintenanceCycle() {
        // Step 1: Send to maintenance
        AssetMaintenanceSendRequest sendReq = new AssetMaintenanceSendRequest(
                asset.getId(),
                "Cong ty Sua chua Co dien",
                BigDecimal.valueOf(1500000.00),
                LocalDate.now(),
                "Bao duong dinh ky dieu hoa",
                "Du kien 2 ngay"
        );

        AssetMaintenanceResponse sendRes = assetMaintenanceService.createMaintenance(sendReq);
        assertThat(sendRes.id()).isNotNull();
        assertThat(sendRes.status()).isEqualTo("UNDER_MAINTENANCE");
        assertThat(sendRes.provider()).isEqualTo("Cong ty Sua chua Co dien");
        assertThat(sendRes.cost()).isEqualByComparingTo(BigDecimal.valueOf(1500000.00));

        // Verify asset status transitioned to MAINTENANCE
        Asset updatedAsset1 = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(updatedAsset1.getStatus()).isEqualTo("MAINTENANCE");

        // Step 2: Complete maintenance (transition back to ACTIVE)
        AssetMaintenanceCompleteRequest completeReq = new AssetMaintenanceCompleteRequest(
                LocalDate.now().plusDays(2),
                BigDecimal.valueOf(1600000.00), // actual cost
                "Da thay block, nap ga bo sung",
                "Hoat dong lanh sau, tot",
                "ACTIVE" // next status
        );

        AssetMaintenanceResponse completeRes = assetMaintenanceService.completeMaintenance(sendRes.id(), completeReq);
        assertThat(completeRes.status()).isEqualTo("COMPLETED");
        assertThat(completeRes.completionDate()).isEqualTo(LocalDate.now().plusDays(2));
        assertThat(completeRes.cost()).isEqualByComparingTo(BigDecimal.valueOf(1600000.00));

        // Verify asset status transitioned back to ACTIVE
        Asset updatedAsset2 = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(updatedAsset2.getStatus()).isEqualTo("ACTIVE");

        // Step 3: Send to maintenance again to test cancellation
        AssetMaintenanceSendRequest sendReq2 = new AssetMaintenanceSendRequest(
                asset.getId(),
                "Cong ty Sua chua B",
                BigDecimal.valueOf(500000.00),
                LocalDate.now(),
                "Kiem tra hu hong nut bam",
                "Lam luon trong ngay"
        );

        AssetMaintenanceResponse sendRes2 = assetMaintenanceService.createMaintenance(sendReq2);
        assertThat(sendRes2.status()).isEqualTo("UNDER_MAINTENANCE");

        // Verify asset status transitioned back to MAINTENANCE
        Asset updatedAsset3 = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(updatedAsset3.getStatus()).isEqualTo("MAINTENANCE");

        // Cancel maintenance
        AssetMaintenanceResponse cancelRes = assetMaintenanceService.cancelMaintenance(sendRes2.id());
        assertThat(cancelRes.status()).isEqualTo("CANCELLED");

        // Verify asset status transitioned back to ACTIVE
        Asset updatedAsset4 = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(updatedAsset4.getStatus()).isEqualTo("ACTIVE");

        // Step 4: Search maintenance logs
        Page<AssetMaintenanceResponse> pageResult = assetMaintenanceService.getMaintenances(
                asset.getId(),
                null,
                "Co dien",
                0,
                10
        );
        assertThat(pageResult.getTotalElements()).isEqualTo(1);
        assertThat(pageResult.getContent().get(0).provider()).contains("Co dien");
    }
}
