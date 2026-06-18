package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.WarehouseInventoryListItemResponse;
import com.example.med_office.dto.WarehouseInventoryPageResponse;
import com.example.med_office.dto.WarehouseOutboundAction;
import com.example.med_office.dto.WarehouseOutboundApprovalRequest;
import com.example.med_office.dto.WarehouseOutboundCreateRequest;
import com.example.med_office.dto.WarehouseOutboundMutationResponse;
import com.example.med_office.dto.WarehouseOutboundListItemResponse;
import com.example.med_office.dto.WarehouseOutboundPageResponse;
import com.example.med_office.dto.WarehouseOutboundDetailResponse;
import com.example.med_office.dto.WarehouseOutboundUpsertRequest;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseInbound;
import com.example.med_office.entity.WarehouseInboundItem;
import com.example.med_office.entity.WarehouseInboundStatus;
import com.example.med_office.entity.WarehouseManager;
import com.example.med_office.entity.WarehouseManagerId;
import com.example.med_office.entity.WarehouseOutbound;
import com.example.med_office.entity.WarehouseOutboundItem;
import com.example.med_office.entity.WarehouseOutboundStatus;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.WarehouseInboundRepository;
import com.example.med_office.repository.WarehouseManagerRepository;
import com.example.med_office.repository.WarehouseInboundItemRepository;
import com.example.med_office.repository.WarehouseOutboundItemRepository;
import com.example.med_office.repository.WarehouseOutboundRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:warehouse-outbound-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class WarehouseOutboundInventoryIntegrationTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseInboundRepository warehouseInboundRepository;

    @Autowired
    private WarehouseInboundItemRepository warehouseInboundItemRepository;

    @Autowired
    private WarehouseOutboundRepository warehouseOutboundRepository;

    @Autowired
    private WarehouseOutboundItemRepository warehouseOutboundItemRepository;

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private WarehouseOutboundService warehouseOutboundService;

    @Autowired
    private WarehouseInventoryService warehouseInventoryService;

    private Warehouse warehouseKt;
    private Warehouse warehouseLe;
    private WarehouseOutbound outboundLeDraft;
    private WarehouseOutbound outboundKtDraftOver;

    @BeforeEach
    void setUp() {
        warehouseKt = warehouseRepository.save(newWarehouse("wh-kt", "KT", "Kho Tong"));
        warehouseLe = warehouseRepository.save(newWarehouse("wh-le", "LE", "Kho Le"));

        NguoiDung managerUser = nguoiDungRepository.save(newUser("user-kt", "manager.kt"));
        HoSoNhanVien managerProfile = hoSoNhanVienRepository.save(newProfile("profile-kt", managerUser.getId(), "NVKT"));
        WarehouseManager managerMapping = new WarehouseManager();
        managerMapping.setId(new WarehouseManagerId(warehouseKt.getId(), managerProfile.getId()));
        warehouseManagerRepository.save(managerMapping);

        nguoiDungRepository.save(newUser("admin-1", "admin.root"));

        warehouseInboundRepository.save(newInbound("inb-kt", warehouseKt, WarehouseInboundStatus.APPROVED, new BigDecimal("100")));
        warehouseInboundRepository.save(newInbound("inb-le", warehouseLe, WarehouseInboundStatus.COMPLETED, new BigDecimal("50")));

        warehouseOutboundRepository.save(newOutbound("out-kt-pending", "PX-KT-001", warehouseKt, WarehouseOutboundStatus.PENDING_APPROVAL, new BigDecimal("20")));
        warehouseOutboundRepository.save(newOutbound("out-le-pending", "PX-LE-001", warehouseLe, WarehouseOutboundStatus.PENDING_APPROVAL, new BigDecimal("5")));
        outboundLeDraft = warehouseOutboundRepository.save(newOutbound("out-le-draft", "PX-LE-DR", warehouseLe, WarehouseOutboundStatus.DRAFT, new BigDecimal("10")));
        outboundKtDraftOver = warehouseOutboundRepository.save(newOutbound("out-kt-draft", "PX-KT-OVER", warehouseKt, WarehouseOutboundStatus.DRAFT, new BigDecimal("90")));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        warehouseManagerRepository.deleteAllInBatch();
        warehouseOutboundItemRepository.deleteAllInBatch();
        warehouseInboundItemRepository.deleteAllInBatch();
        warehouseOutboundRepository.deleteAllInBatch();
        warehouseInboundRepository.deleteAllInBatch();
        warehouseRepository.deleteAllInBatch();
        hoSoNhanVienRepository.deleteAllInBatch();
        nguoiDungRepository.deleteAllInBatch();
    }

    @Test
    void managedUserOnlySeesManagedOutboundsAndInventories() {
        authenticate("manager.kt", "ROLE_USER");

        WarehouseOutboundPageResponse outboundPage = warehouseOutboundService.findAll(0, 20, null, null, null, null, null, null);
        WarehouseInventoryPageResponse inventoryPage = warehouseInventoryService.findAll(0, 20, null, null);

        assertThat(outboundPage.content())
                .extracting(WarehouseOutboundListItemResponse::code)
                .containsExactlyInAnyOrder("PX-KT-001", "PX-KT-OVER");
        assertThat(inventoryPage.content()).hasSize(1);
        WarehouseInventoryListItemResponse inventoryItem = inventoryPage.content().getFirst();
        assertThat(inventoryItem.warehouseId()).isEqualTo(warehouseKt.getId());
        assertThat(inventoryItem.totalQuantity()).isEqualByComparingTo("100");
        assertThat(inventoryItem.reservedQuantity()).isEqualByComparingTo("20");
        assertThat(inventoryItem.availableQuantity()).isEqualByComparingTo("80");
    }

    @Test
    void managedUserCannotCreateOrSubmitOutboundOutsideWarehouseScope() {
        authenticate("manager.kt", "ROLE_USER");

        WarehouseOutboundCreateRequest createRequest = new WarehouseOutboundCreateRequest(
                null,
                "PX-LE-NEW",
                LocalDate.of(2026, 6, 5),
                warehouseLe.getId(),
                warehouseKt.getId(),
                "Khoa Le",
                "Nguoi Nhan",
                "Nguoi Yeu Cau",
                null,
                List.of(itemRequest(new BigDecimal("10"))),
                WarehouseOutboundAction.SAVE_DRAFT
        );

        assertThatThrownBy(() -> warehouseOutboundService.create(createRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(403));

        assertThatThrownBy(() -> warehouseOutboundService.submit(outboundLeDraft.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(403));
    }

    @Test
    void outboundRequiresSufficientAvailableInventory() {
        authenticate("manager.kt", "ROLE_USER");

        WarehouseOutboundCreateRequest createRequest = new WarehouseOutboundCreateRequest(
                null,
                "PX-KT-NEW",
                LocalDate.of(2026, 6, 5),
                warehouseKt.getId(),
                warehouseLe.getId(),
                "Khoa Noi",
                "Nguoi Nhan",
                "Nguoi Yeu Cau",
                null,
                List.of(itemRequest(new BigDecimal("81"))),
                WarehouseOutboundAction.SAVE_DRAFT
        );

        assertThatThrownBy(() -> warehouseOutboundService.create(createRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode().value()).isEqualTo(400);
                    assertThat(exception.getReason()).contains("So luong ton kha dung khong du");
                });

        assertThatThrownBy(() -> warehouseOutboundService.submit(outboundKtDraftOver.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(400));
    }

    @Test
    void adminSeesAllOutboundsAndInventories() {
        authenticate("admin.root", "ROLE_ADMIN");

        WarehouseOutboundPageResponse outboundPage = warehouseOutboundService.findAll(0, 20, null, null, null, null, null, null);
        WarehouseInventoryPageResponse inventoryPage = warehouseInventoryService.findAll(0, 20, null, null);

        assertThat(outboundPage.content()).hasSize(4);
        assertThat(inventoryPage.content()).hasSize(2);
    }

    @Test
    void inventorySupportsSearchingBatchNumbersContainingHyphen() {
        authenticate("admin.root", "ROLE_ADMIN");

        warehouseInboundRepository.save(newInbound("inb-hyphen", warehouseKt, WarehouseInboundStatus.APPROVED, new BigDecimal("15"), "SAME-BATCH"));

        WarehouseInventoryPageResponse inventoryPage = warehouseInventoryService.findAll(0, 200, "SAME-BATCH", warehouseKt.getId());

        assertThat(inventoryPage.content()).hasSize(1);
        assertThat(inventoryPage.content().getFirst().batchNumber()).isEqualTo("SAME-BATCH");
    }

    @Test
    void approvedOutboundReducesInventoryAndCompletedKeepsDeduction() {
        authenticate("manager.kt", "ROLE_USER");

        WarehouseOutboundMutationResponse approvedResponse = warehouseOutboundService.approve(
                "out-kt-pending",
                new WarehouseOutboundApprovalRequest("Duyet xuat kho")
        );
        assertThat(approvedResponse.status()).isEqualTo(WarehouseOutboundStatus.APPROVED);

        WarehouseInventoryPageResponse approvedInventoryPage = warehouseInventoryService.findAll(0, 20, null, warehouseKt.getId());
        WarehouseInventoryListItemResponse approvedInventoryItem = approvedInventoryPage.content().getFirst();
        assertThat(approvedInventoryItem.totalQuantity()).isEqualByComparingTo("80");
        assertThat(approvedInventoryItem.reservedQuantity()).isEqualByComparingTo("0");
        assertThat(approvedInventoryItem.availableQuantity()).isEqualByComparingTo("80");

        WarehouseOutboundMutationResponse completedResponse = warehouseOutboundService.complete("out-kt-pending");
        assertThat(completedResponse.status()).isEqualTo(WarehouseOutboundStatus.COMPLETED);

        WarehouseInventoryPageResponse completedInventoryPage = warehouseInventoryService.findAll(0, 20, null, warehouseKt.getId());
        WarehouseInventoryListItemResponse completedInventoryItem = completedInventoryPage.content().getFirst();
        assertThat(completedInventoryItem.totalQuantity()).isEqualByComparingTo("80");
        assertThat(completedInventoryItem.reservedQuantity()).isEqualByComparingTo("0");
        assertThat(completedInventoryItem.availableQuantity()).isEqualByComparingTo("80");

        authenticate("admin.root", "ROLE_ADMIN");
        WarehouseInventoryPageResponse destinationInventoryPage = warehouseInventoryService.findAll(0, 20, null, warehouseLe.getId());
        WarehouseInventoryListItemResponse destinationInventoryItem = destinationInventoryPage.content().getFirst();
        assertThat(destinationInventoryItem.totalQuantity()).isEqualByComparingTo("70");
        assertThat(destinationInventoryItem.reservedQuantity()).isEqualByComparingTo("5");
        assertThat(destinationInventoryItem.availableQuantity()).isEqualByComparingTo("65");

        WarehouseOutboundDetailResponse detailResponse = warehouseOutboundService.findById("out-kt-pending");
        assertThat(detailResponse.destinationWarehouseId()).isEqualTo(warehouseLe.getId());
        assertThat(detailResponse.destinationWarehouseName()).isEqualTo(warehouseLe.getName());
    }

    @Test
    void internalTransferRequiresDifferentDestinationWarehouse() {
        authenticate("admin.root", "ROLE_ADMIN");

        WarehouseOutboundCreateRequest createRequest = new WarehouseOutboundCreateRequest(
                null,
                "PX-KT-SAME",
                LocalDate.of(2026, 6, 5),
                warehouseKt.getId(),
                warehouseKt.getId(),
                "Kho Tong",
                "Nguoi Nhan",
                "Nguoi Yeu Cau",
                null,
                List.of(itemRequest(new BigDecimal("10"))),
                WarehouseOutboundAction.SAVE_DRAFT
        );

        assertThatThrownBy(() -> warehouseOutboundService.create(createRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode().value()).isEqualTo(400);
                    assertThat(exception.getReason()).contains("Kho nhan phai khac kho xuat");
                });
    }

    @Test
    void approveUsesOwnPendingReservationWhenNoAvailableQuantityRemains() {
        authenticate("admin.root", "ROLE_ADMIN");

        Warehouse warehouseCap = warehouseRepository.save(newWarehouse("wh-cap", "CAP", "Kho Cap Cuu"));
        warehouseInboundRepository.save(newInbound("inb-cap", warehouseCap, WarehouseInboundStatus.APPROVED, new BigDecimal("20")));
        warehouseOutboundRepository.save(newOutbound("out-cap-pending", "PX-CAP-001", warehouseCap, WarehouseOutboundStatus.PENDING_APPROVAL, new BigDecimal("20")));

        WarehouseOutboundMutationResponse approvedResponse = warehouseOutboundService.approve(
                "out-cap-pending",
                new WarehouseOutboundApprovalRequest("Duyet xuat kho cap cuu")
        );

        assertThat(approvedResponse.status()).isEqualTo(WarehouseOutboundStatus.APPROVED);

        WarehouseInventoryPageResponse inventoryPage = warehouseInventoryService.findAll(0, 20, null, warehouseCap.getId());
        assertThat(inventoryPage.content()).isEmpty();
    }

    @Test
    void createOutboundWithBlankCodeGeneratesAutomaticCode() {
        authenticate("admin.root", "ROLE_ADMIN");

        warehouseOutboundItemRepository.deleteAllInBatch();
        warehouseOutboundRepository.deleteAllInBatch();

        WarehouseOutboundCreateRequest createRequest1 = new WarehouseOutboundCreateRequest(
                null,
                "", 
                LocalDate.now(),
                warehouseKt.getId(),
                warehouseLe.getId(),
                "Khoa Le",
                "Nguoi Nhan",
                "Nguoi Yeu Cau",
                "Note 1",
                List.of(itemRequest(new BigDecimal("10"))),
                WarehouseOutboundAction.SAVE_DRAFT
        );

        WarehouseOutboundMutationResponse response1 = warehouseOutboundService.create(createRequest1);
        String prefix = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        String expectedCode1 = prefix + "0001";
        assertThat(response1.code()).isEqualTo(expectedCode1);

        WarehouseOutboundCreateRequest createRequest2 = new WarehouseOutboundCreateRequest(
                null,
                "(Tự động)", 
                LocalDate.now(),
                warehouseKt.getId(),
                warehouseLe.getId(),
                "Khoa Le",
                "Nguoi Nhan",
                "Nguoi Yeu Cau",
                "Note 2",
                List.of(itemRequest(new BigDecimal("5"))),
                WarehouseOutboundAction.SAVE_DRAFT
        );

        WarehouseOutboundMutationResponse response2 = warehouseOutboundService.create(createRequest2);
        String expectedCode2 = prefix + "0002";
        assertThat(response2.code()).isEqualTo(expectedCode2);
    }

    private void authenticate(String username, String authority) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                "N/A",
                List.of(new SimpleGrantedAuthority(authority))
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
        warehouse.setCreatedAt(Instant.parse("2026-06-04T00:00:00Z"));
        warehouse.setUpdatedAt(Instant.parse("2026-06-04T00:00:00Z"));
        return warehouse;
    }

    private NguoiDung newUser(String id, String username) {
        NguoiDung user = new NguoiDung();
        user.setId(id);
        user.setTenDangNhap(username);
        user.setMatKhauMaHoa("secret");
        user.setTrangThai("ACTIVE");
        user.setNgayTao(LocalDateTime.of(2026, 6, 4, 0, 0));
        user.setNgayCapNhat(LocalDateTime.of(2026, 6, 4, 0, 0));
        return user;
    }

    private HoSoNhanVien newProfile(String id, String userId, String code) {
        HoSoNhanVien profile = new HoSoNhanVien();
        profile.setId(id);
        profile.setNguoiDungId(userId);
        profile.setCode(code);
        profile.setName("Manager " + code);
        profile.setActive(true);
        profile.setCreatedAt(LocalDateTime.of(2026, 6, 4, 0, 0));
        profile.setUpdatedAt(LocalDateTime.of(2026, 6, 4, 0, 0));
        return profile;
    }

    private WarehouseInbound newInbound(String id, Warehouse warehouse, WarehouseInboundStatus status, BigDecimal quantity) {
        return newInbound(id, warehouse, status, quantity, "LO001");
    }

    private WarehouseInbound newInbound(String id, Warehouse warehouse, WarehouseInboundStatus status, BigDecimal quantity, String batchNumber) {
        WarehouseInbound inbound = new WarehouseInbound();
        inbound.setId(id);
        inbound.setCode("NK-" + warehouse.getCode() + '-' + id);
        inbound.setReceiptDate(LocalDate.of(2026, 6, 4));
        inbound.setStatus(status);
        inbound.setWarehouseId(warehouse.getId());
        inbound.setWarehouseName(warehouse.getName());
        inbound.setCreatedAt(Instant.parse("2026-06-04T00:00:00Z"));
        inbound.setUpdatedAt(Instant.parse("2026-06-04T00:00:00Z"));

        WarehouseInboundItem item = new WarehouseInboundItem();
        item.setItemId("item-1");
        item.setItemCode("VT001");
        item.setItemName("Gang tay");
        item.setUnit("Hop");
        item.setQuantity(quantity);
        item.setUnitPrice(BigDecimal.TEN);
        item.setLineTotal(quantity.multiply(BigDecimal.TEN));
        item.setBatchNumber(batchNumber);
        item.setExpiryDate(LocalDate.of(2027, 1, 1));
        inbound.addItem(item);
        return inbound;
    }

    private WarehouseOutbound newOutbound(String id, String code, Warehouse warehouse, WarehouseOutboundStatus status, BigDecimal quantity) {
        WarehouseOutbound outbound = new WarehouseOutbound();
        outbound.setId(id);
        outbound.setCode(code);
        outbound.setOutboundDate(LocalDate.of(2026, 6, 5));
        outbound.setStatus(status);
        outbound.setWarehouseId(warehouse.getId());
        outbound.setWarehouseName(warehouse.getName());
        if ("out-kt-pending".equals(id)) {
            outbound.setDestinationWarehouseId(warehouseLe.getId());
            outbound.setDestinationName(warehouseLe.getName());
        } else {
            outbound.setDestinationName("Khoa Noi");
        }
        outbound.setReceivedBy("Nguoi Nhan");
        outbound.setRequestedBy("Nguoi Yeu Cau");
        outbound.setCreatedAt(Instant.parse("2026-06-05T00:00:00Z"));
        outbound.setUpdatedAt(Instant.parse("2026-06-05T00:00:00Z"));

        WarehouseOutboundItem item = new WarehouseOutboundItem();
        item.setItemId("item-1");
        item.setItemCode("VT001");
        item.setItemName("Gang tay");
        item.setUnit("Hop");
        item.setQuantity(quantity);
        item.setBatchNumber("LO001");
        item.setExpiryDate(LocalDate.of(2027, 1, 1));
        item.setNote("Note");
        outbound.addItem(item);
        return outbound;
    }

    private WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest itemRequest(BigDecimal quantity) {
        return new WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest(
                "item-1",
                "VT001",
                "Gang tay",
                quantity,
                BigDecimal.ZERO,
                "Hop",
                "LO001",
                LocalDate.of(2027, 1, 1),
                null
        );
    }
}
