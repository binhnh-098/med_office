package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.AssetDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.repository.AssetRepository;
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
        "spring.datasource.url=jdbc:h2:mem:asset-integration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AssetIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAllInBatch();
    }

    @AfterEach
    void tearDown() {
        assetRepository.deleteAllInBatch();
    }

    @Test
    void completeAssetLifecycleFlow() {
        // 1. Create a new asset item
        AssetUpsertRequest createReq = new AssetUpsertRequest(
                "TS-001",
                "Máy tính xách tay Dell Latitude",
                "Thiết bị văn phòng",
                "Cái",
                "Latitude 5420",
                "SN123456",
                "Dell",
                "Dell Inc.",
                "base64-image-string",
                "Intel Core i5, 16GB RAM, 512GB SSD",
                BigDecimal.valueOf(15000000),
                LocalDate.of(2026, 6, 1),
                "ACTIVE",
                "Máy cấp cho nhân viên hành chính"
        );

        AssetResponse created = assetService.createAsset(createReq);
        assertThat(created.id()).isNotNull();
        assertThat(created.code()).isEqualTo("TS-001");
        assertThat(created.name()).isEqualTo("Máy tính xách tay Dell Latitude");
        assertThat(created.status()).isEqualTo("ACTIVE");
        assertThat(created.model()).isEqualTo("Latitude 5420");
        assertThat(created.serialNumber()).isEqualTo("SN123456");
        assertThat(created.brand()).isEqualTo("Dell");
        assertThat(created.manufacturer()).isEqualTo("Dell Inc.");
        assertThat(created.image()).isEqualTo("base64-image-string");

        // 2. Fetch detail
        AssetResponse detail = assetService.getAssetDetail(created.id());
        assertThat(detail.name()).isEqualTo("Máy tính xách tay Dell Latitude");

        // 3. Update asset details
        AssetUpsertRequest updateReq = new AssetUpsertRequest(
                "TS-001",
                "Máy tính xách tay Dell Latitude 5420",
                "Thiết bị văn phòng",
                "Cái",
                "Latitude 5420 v2",
                "SN123456-updated",
                "Dell",
                "Dell Inc.",
                "base64-image-string-updated",
                "Intel Core i5, 16GB RAM, 512GB SSD - Cập nhật",
                BigDecimal.valueOf(15500000),
                LocalDate.of(2026, 6, 1),
                "ACTIVE",
                "Máy cấp cho nhân viên hành chính (Cập nhật)"
        );

        AssetResponse updated = assetService.updateAsset(created.id(), updateReq);
        assertThat(updated.name()).isEqualTo("Máy tính xách tay Dell Latitude 5420");
        assertThat(updated.purchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(15500000));
        assertThat(updated.model()).isEqualTo("Latitude 5420 v2");
        assertThat(updated.serialNumber()).isEqualTo("SN123456-updated");
        assertThat(updated.image()).isEqualTo("base64-image-string-updated");

        // 4. Try to create duplicate code asset (should fail)
        AssetUpsertRequest duplicateReq = new AssetUpsertRequest(
                "TS-001",
                "Máy tính khác",
                "Thiết bị văn phòng",
                "Cái",
                "Model X",
                "SNXXXX",
                "Brand X",
                "Mfg X",
                null,
                "Specs",
                BigDecimal.valueOf(10000000),
                LocalDate.now(),
                "ACTIVE",
                "Duplicate code"
        );

        assertThrows(ResponseStatusException.class, () ->
                assetService.createAsset(duplicateReq)
        );

        // 5. Query paginated and search
        Page<AssetResponse> searchRes = assetService.getAssets("Dell", "Thiết bị văn phòng", "ACTIVE", 0, 10);
        assertThat(searchRes.getContent()).hasSize(1);
        assertThat(searchRes.getContent().get(0).code()).isEqualTo("TS-001");

        // 6. Delete asset
        assetService.deleteAsset(created.id());
        assertThrows(ResponseStatusException.class, () ->
                assetService.getAssetDetail(created.id())
        );
    }
}
