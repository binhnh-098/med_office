package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.WarehouseHierarchyItem;
import com.example.med_office.dto.WarehouseInboundAction;
import com.example.med_office.dto.WarehouseInboundCreateRequest;
import com.example.med_office.dto.WarehouseInboundDetailResponse;
import com.example.med_office.dto.WarehouseInboundListItemResponse;
import com.example.med_office.dto.WarehouseInboundPageResponse;
import com.example.med_office.dto.WarehouseInboundUpsertRequest;
import com.example.med_office.dto.WarehouseOptionResponse;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseInbound;
import com.example.med_office.entity.WarehouseInboundItem;
import com.example.med_office.entity.WarehouseInboundStatus;
import com.example.med_office.entity.WarehouseManager;
import com.example.med_office.entity.WarehouseManagerId;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.WarehouseInboundRepository;
import com.example.med_office.repository.WarehouseManagerRepository;
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
        "spring.datasource.url=jdbc:h2:mem:warehouse-permission-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class WarehousePermissionIntegrationTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Autowired
    private WarehouseInboundRepository warehouseInboundRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private WarehouseInboundService warehouseInboundService;

    @Autowired
    private CatalogOptionService catalogOptionService;

    @Autowired
    private WarehouseService warehouseService;

    private Warehouse warehouseKt;
    private Warehouse warehouseLe;
    private WarehouseInbound inboundKt;
    private WarehouseInbound inboundLe;

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

        inboundKt = warehouseInboundRepository.save(newInbound("inb-kt", "NK-KT-001", warehouseKt));
        inboundLe = warehouseInboundRepository.save(newInbound("inb-le", "NK-LE-001", warehouseLe));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void managedUserOnlySeesManagedWarehouseOptionsAndInbounds() {
        authenticate("manager.kt", "ROLE_USER");

        List<WarehouseOptionResponse> warehouseOptions = catalogOptionService.getWarehouseOptions();
        WarehouseInboundPageResponse inboundPage = warehouseInboundService.findAll(0, 20, null, null, null, null, null);
        List<WarehouseHierarchyItem> hierarchy = warehouseService.getHierarchy();

        assertThat(warehouseOptions)
                .extracting(WarehouseOptionResponse::code)
                .containsExactly("KT");
        assertThat(inboundPage.content())
                .extracting(WarehouseInboundListItemResponse::code)
                .containsExactly("NK-KT-001");
        assertThat(hierarchy)
                .extracting(WarehouseHierarchyItem::code)
                .containsExactly("KT");
        assertThat(hierarchy.getFirst().managers())
                .extracting(manager -> manager.code())
                .containsExactly("NVKT");
    }

    @Test
    void managedUserCannotViewInboundOfOtherWarehouse() {
        authenticate("manager.kt", "ROLE_USER");

        assertThatThrownBy(() -> warehouseInboundService.findById(inboundLe.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode().value()).isEqualTo(403);
                    assertThat(exception.getReason()).isEqualTo("Ban khong co quyen truy cap phieu nhap cua kho nay");
                });
    }

    @Test
    void managedUserCannotCreateUpdateOrSubmitInboundForOtherWarehouse() {
        authenticate("manager.kt", "ROLE_USER");

        WarehouseInboundCreateRequest createRequest = new WarehouseInboundCreateRequest(
                null,
                "NK-LE-NEW",
                LocalDate.of(2026, 6, 4),
                warehouseLe.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(itemRequest("ITEM-01", "VT001")),
                WarehouseInboundAction.SAVE_DRAFT
        );

        assertThatThrownBy(() -> warehouseInboundService.create(createRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(403));

        WarehouseInboundUpsertRequest updateRequest = new WarehouseInboundUpsertRequest(
                inboundLe.getCode(),
                LocalDate.of(2026, 6, 4),
                warehouseLe.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(itemRequest("ITEM-02", "VT002"))
        );

        assertThatThrownBy(() -> warehouseInboundService.updateDraft(inboundLe.getId(), updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(403));

        assertThatThrownBy(() -> warehouseInboundService.submit(inboundLe.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(403));
    }

    @Test
    void adminStillSeesAllWarehousesAndAllInbounds() {
        authenticate("admin.root", "ROLE_ADMIN");

        List<WarehouseOptionResponse> warehouseOptions = catalogOptionService.getWarehouseOptions();
        WarehouseInboundPageResponse inboundPage = warehouseInboundService.findAll(0, 20, null, null, null, null, null);
        WarehouseInboundDetailResponse detailResponse = warehouseInboundService.findById(inboundLe.getId());
        List<WarehouseHierarchyItem> hierarchy = warehouseService.getHierarchy();

        assertThat(warehouseOptions).hasSize(2);
        assertThat(inboundPage.content()).hasSize(2);
        assertThat(detailResponse.code()).isEqualTo("NK-LE-001");
        assertThat(hierarchy).hasSize(2);
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

    private WarehouseInbound newInbound(String id, String code, Warehouse warehouse) {
        WarehouseInbound inbound = new WarehouseInbound();
        inbound.setId(id);
        inbound.setCode(code);
        inbound.setReceiptDate(LocalDate.of(2026, 6, 4));
        inbound.setStatus(WarehouseInboundStatus.DRAFT);
        inbound.setWarehouseId(warehouse.getId());
        inbound.setWarehouseName(warehouse.getName());
        inbound.setCreatedAt(Instant.parse("2026-06-04T00:00:00Z"));
        inbound.setUpdatedAt(Instant.parse("2026-06-04T00:00:00Z"));

        WarehouseInboundItem item = new WarehouseInboundItem();
        item.setItemId("ITEM-" + code);
        item.setItemCode("VT-" + code);
        item.setItemName("Vat tu " + code);
        item.setUnit("Hop");
        item.setQuantity(BigDecimal.valueOf(10));
        item.setUnitPrice(BigDecimal.valueOf(1000));
        item.setLineTotal(BigDecimal.valueOf(10000));
        inbound.addItem(item);
        return inbound;
    }

    private WarehouseInboundUpsertRequest.WarehouseInboundItemRequest itemRequest(String itemId, String itemCode) {
        return new WarehouseInboundUpsertRequest.WarehouseInboundItemRequest(
                itemId,
                itemCode,
                "Vat tu test",
                "Hop",
                BigDecimal.ONE,
                BigDecimal.TEN,
                null,
                null,
                null
        );
    }
}
