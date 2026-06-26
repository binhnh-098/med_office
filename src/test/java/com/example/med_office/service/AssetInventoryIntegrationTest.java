package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.AssetDTOs.AssetUpsertRequest;
import com.example.med_office.dto.AssetDTOs.AssetResponse;
import com.example.med_office.dto.AssetInventoryDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.repository.AssetRepository;
import com.example.med_office.repository.AssetInventoryRepository;
import com.example.med_office.repository.AssetInventoryDetailRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:asset-inventory-integration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AssetInventoryIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetInventoryRepository assetInventoryRepository;

    @Autowired
    private AssetInventoryDetailRepository assetInventoryDetailRepository;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetInventoryService assetInventoryService;

    private String assetId1;
    private String assetId2;

    @BeforeEach
    void setUp() {
        assetInventoryDetailRepository.deleteAllInBatch();
        assetInventoryRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();

        // Setup some assets for inventory
        AssetUpsertRequest createReq1 = new AssetUpsertRequest(
                "TS-001", "Laptop Dell Latitude", "Thiết bị văn phòng", "Cái",
                "Latitude 5420", "SN123", "Dell", "Dell Inc.", null, "Core i5",
                BigDecimal.valueOf(15000000), LocalDate.now(), "ACTIVE", "Active asset"
        );
        AssetResponse a1 = assetService.createAsset(createReq1);
        assetId1 = a1.id();

        AssetUpsertRequest createReq2 = new AssetUpsertRequest(
                "TS-002", "Monitor Dell", "Thiết bị văn phòng", "Cái",
                "U2422H", "SN456", "Dell", "Dell Inc.", null, "24 inch",
                BigDecimal.valueOf(5000000), LocalDate.now(), "ACTIVE", "Active asset 2"
        );
        AssetResponse a2 = assetService.createAsset(createReq2);
        assetId2 = a2.id();
    }

    @AfterEach
    void tearDown() {
        assetInventoryDetailRepository.deleteAllInBatch();
        assetInventoryRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();
    }

    @Test
    void testInventoryLifecycle() {
        // 1. Create a DRAFT inventory session
        AssetInventoryDetailRequest det1 = new AssetInventoryDetailRequest(assetId1, true, "ACTIVE", "Khớp");
        AssetInventoryDetailRequest det2 = new AssetInventoryDetailRequest(assetId2, false, "BROKEN", "Hỏng, không có mặt");
        
        AssetInventorySaveRequest draftReq = new AssetInventorySaveRequest(
                null,
                "PKK-20260625-001",
                LocalDate.of(2026, 6, 25),
                "DRAFT",
                "Đợt kiểm kê nháp đầu tiên",
                List.of(det1, det2)
        );

        AssetInventoryResponse draftRes = assetInventoryService.saveInventory(draftReq);
        assertThat(draftRes.id()).isNotNull();
        assertThat(draftRes.documentNumber()).isEqualTo("PKK-20260625-001");
        assertThat(draftRes.status()).isEqualTo("DRAFT");
        assertThat(draftRes.details()).hasSize(2);

        // Assets status should not be updated yet
        AssetResponse asset1 = assetService.getAssetDetail(assetId1);
        AssetResponse asset2 = assetService.getAssetDetail(assetId2);
        assertThat(asset1.status()).isEqualTo("ACTIVE");
        assertThat(asset2.status()).isEqualTo("ACTIVE");

        // 2. Search and list inventories
        Page<AssetInventoryResponse> listRes = assetInventoryService.getInventories("DRAFT", "PKK-202606", 0, 10);
        assertThat(listRes.getContent()).hasSize(1);
        assertThat(listRes.getContent().get(0).id()).isEqualTo(draftRes.id());

        // 3. Edit draft session
        AssetInventoryDetailRequest editDet1 = new AssetInventoryDetailRequest(assetId1, true, "ACTIVE", "Khớp - Đã cập nhật");
        AssetInventoryDetailRequest editDet2 = new AssetInventoryDetailRequest(assetId2, true, "MAINTENANCE", "Đem đi bảo hành");
        AssetInventorySaveRequest updateReq = new AssetInventorySaveRequest(
                draftRes.id(),
                "PKK-20260625-001",
                LocalDate.of(2026, 6, 25),
                "DRAFT",
                "Đợt kiểm kê nháp đầu tiên - Cập nhật",
                List.of(editDet1, editDet2)
        );

        AssetInventoryResponse updatedRes = assetInventoryService.saveInventory(updateReq);
        assertThat(updatedRes.id()).isEqualTo(draftRes.id());
        assertThat(updatedRes.notes()).isEqualTo("Đợt kiểm kê nháp đầu tiên - Cập nhật");
        assertThat(updatedRes.details()).hasSize(2);

        // Assets status should still be ACTIVE
        asset1 = assetService.getAssetDetail(assetId1);
        asset2 = assetService.getAssetDetail(assetId2);
        assertThat(asset1.status()).isEqualTo("ACTIVE");
        assertThat(asset2.status()).isEqualTo("ACTIVE");

        // 4. Complete / Finalize inventory session
        AssetInventorySaveRequest finalizeReq = new AssetInventorySaveRequest(
                draftRes.id(),
                "PKK-20260625-001",
                LocalDate.of(2026, 6, 25),
                "COMPLETED",
                "Hoàn thành kiểm kê thực tế",
                List.of(editDet1, editDet2)
        );

        AssetInventoryResponse finalizedRes = assetInventoryService.saveInventory(finalizeReq);
        assertThat(finalizedRes.status()).isEqualTo("COMPLETED");

        // Now assets status should be synchronized
        asset1 = assetService.getAssetDetail(assetId1);
        asset2 = assetService.getAssetDetail(assetId2);
        assertThat(asset1.status()).isEqualTo("ACTIVE");
        assertThat(asset2.status()).isEqualTo("MAINTENANCE"); // synced actualStatus

        // 5. Try to modify completed inventory (should fail)
        AssetInventorySaveRequest illegalReq = new AssetInventorySaveRequest(
                draftRes.id(),
                "PKK-20260625-001",
                LocalDate.of(2026, 6, 25),
                "DRAFT",
                "Sửa trái phép biên bản đã đóng",
                List.of(editDet1, editDet2)
        );
        assertThrows(ResponseStatusException.class, () -> assetInventoryService.saveInventory(illegalReq));

        // 6. Try to delete completed inventory (should fail)
        assertThrows(ResponseStatusException.class, () -> assetInventoryService.deleteInventory(draftRes.id()));
    }

    @Test
    void testDeleteDraftInventory() {
        AssetInventoryDetailRequest det1 = new AssetInventoryDetailRequest(assetId1, true, "ACTIVE", "Khớp");
        AssetInventorySaveRequest draftReq = new AssetInventorySaveRequest(
                null,
                "PKK-20260625-999",
                LocalDate.now(),
                "DRAFT",
                "Để xóa",
                List.of(det1)
        );

        AssetInventoryResponse draftRes = assetInventoryService.saveInventory(draftReq);
        assertThat(draftRes.id()).isNotNull();

        // Delete draft
        assetInventoryService.deleteInventory(draftRes.id());

        // Verify not found
        assertThrows(ResponseStatusException.class, () -> assetInventoryService.getInventoryDetail(draftRes.id()));
    }
}
