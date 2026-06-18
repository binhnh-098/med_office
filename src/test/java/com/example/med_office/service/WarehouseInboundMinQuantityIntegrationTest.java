package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.WarehouseInboundAction;
import com.example.med_office.dto.WarehouseInboundApprovalRequest;
import com.example.med_office.dto.WarehouseInboundCreateRequest;
import com.example.med_office.dto.WarehouseInboundDetailResponse;
import com.example.med_office.dto.WarehouseInboundMutationResponse;
import com.example.med_office.dto.WarehouseInboundUpsertRequest;
import com.example.med_office.dto.WarehouseInventoryListItemResponse;
import com.example.med_office.dto.WarehouseInventoryPageResponse;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseInboundStatus;
import com.example.med_office.repository.WarehouseInboundItemRepository;
import com.example.med_office.repository.WarehouseInboundRepository;
import com.example.med_office.repository.WarehouseInventoryMinQuantityRepository;
import com.example.med_office.repository.WarehouseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:warehouse-inbound-min-quantity-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class WarehouseInboundMinQuantityIntegrationTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseInboundRepository warehouseInboundRepository;

    @Autowired
    private WarehouseInboundItemRepository warehouseInboundItemRepository;

    @Autowired
    private WarehouseInventoryMinQuantityRepository warehouseInventoryMinQuantityRepository;

    @Autowired
    private WarehouseInboundService warehouseInboundService;

    @Autowired
    private WarehouseInventoryService warehouseInventoryService;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = warehouseRepository.save(newWarehouse("wh-min", "MIN", "Kho Min"));
        authenticateAdmin();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        warehouseInventoryMinQuantityRepository.deleteAllInBatch();
        warehouseInboundItemRepository.deleteAllInBatch();
        warehouseInboundRepository.deleteAllInBatch();
        warehouseRepository.deleteAllInBatch();
    }

    @Test
    void minQuantityFlowsFromInboundDraftToDetailAndInventoryAfterApproval() {
        WarehouseInboundMutationResponse created = warehouseInboundService.create(new WarehouseInboundCreateRequest(
                null,
                "PNK-001",
                LocalDate.of(2026, 6, 12),
                warehouse.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(itemRequest(new BigDecimal("10"), new BigDecimal("20"))),
                WarehouseInboundAction.SAVE_DRAFT
        ));

        WarehouseInboundDetailResponse createdDetail = warehouseInboundService.findById(created.id());
        assertThat(createdDetail.status()).isEqualTo(WarehouseInboundStatus.DRAFT);
        assertThat(createdDetail.items()).hasSize(1);
        assertThat(createdDetail.items().getFirst().minQuantity()).isEqualByComparingTo("20");

        WarehouseInboundMutationResponse updated = warehouseInboundService.updateDraft(
                created.id(),
                new WarehouseInboundUpsertRequest(
                        "PNK-001",
                        LocalDate.of(2026, 6, 12),
                        warehouse.getId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of(itemRequest(new BigDecimal("10"), new BigDecimal("25")))
                )
        );

        assertThat(updated.status()).isEqualTo(WarehouseInboundStatus.DRAFT);

        WarehouseInboundDetailResponse updatedDetail = warehouseInboundService.findById(created.id());
        assertThat(updatedDetail.items().getFirst().minQuantity()).isEqualByComparingTo("25");

        warehouseInboundService.submit(created.id());

        WarehouseInboundMutationResponse approved = warehouseInboundService.approve(
                created.id(),
                new WarehouseInboundApprovalRequest("Duyet phieu co min quantity")
        );

        assertThat(approved.status()).isEqualTo(WarehouseInboundStatus.APPROVED);

        WarehouseInventoryPageResponse approvedInventory = warehouseInventoryService.findAll(0, 20, null, warehouse.getId());
        assertThat(approvedInventory.content()).hasSize(1);
        WarehouseInventoryListItemResponse approvedInventoryItem = approvedInventory.content().getFirst();
        assertThat(approvedInventoryItem.totalQuantity()).isEqualByComparingTo("10");
        assertThat(approvedInventoryItem.minQuantity()).isEqualByComparingTo("25");

        WarehouseInboundMutationResponse completed = warehouseInboundService.complete(created.id());
        assertThat(completed.status()).isEqualTo(WarehouseInboundStatus.COMPLETED);

        WarehouseInventoryPageResponse completedInventory = warehouseInventoryService.findAll(0, 20, null, warehouse.getId());
        assertThat(completedInventory.content()).hasSize(1);
        assertThat(completedInventory.content().getFirst().minQuantity()).isEqualByComparingTo("25");
    }

    private void authenticateAdmin() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin.root",
                "N/A",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Warehouse newWarehouse(String id, String code, String name) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setCode(code);
        warehouse.setName(name);
        warehouse.setLocation(name + " Location");
        warehouse.setStatus("ACTIVE");
        warehouse.setCreatedAt(Instant.parse("2026-06-12T00:00:00Z"));
        warehouse.setUpdatedAt(Instant.parse("2026-06-12T00:00:00Z"));
        return warehouse;
    }

    private WarehouseInboundUpsertRequest.WarehouseInboundItemRequest itemRequest(
            BigDecimal quantity,
            BigDecimal minQuantity
    ) {
        return new WarehouseInboundUpsertRequest.WarehouseInboundItemRequest(
                "item-1",
                "BOM-1",
                "Bom 5",
                "Hop",
                quantity,
                new BigDecimal("5000"),
                "01254",
                LocalDate.of(2026, 12, 31),
                minQuantity
        );
    }
}
