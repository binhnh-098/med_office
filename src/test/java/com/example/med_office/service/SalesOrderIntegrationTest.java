package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.SalesOrderDetailResponse;
import com.example.med_office.dto.SalesOrderMutationResponse;
import com.example.med_office.dto.SalesOrderPageResponse;
import com.example.med_office.dto.SalesOrderUpsertRequest;
import com.example.med_office.dto.WarehouseInventoryPageResponse;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.NguoiDung;
import com.example.med_office.entity.SalesOrder;
import com.example.med_office.entity.SalesOrderStatus;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseInbound;
import com.example.med_office.entity.WarehouseInboundItem;
import com.example.med_office.entity.WarehouseInboundStatus;
import com.example.med_office.entity.WarehouseManager;
import com.example.med_office.entity.WarehouseManagerId;
import com.example.med_office.entity.WarehouseOutbound;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.SalesOrderRepository;
import com.example.med_office.repository.WarehouseInboundItemRepository;
import com.example.med_office.repository.WarehouseInboundRepository;
import com.example.med_office.repository.WarehouseManagerRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:sales-order-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class SalesOrderIntegrationTest {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Autowired
    private WarehouseInboundRepository warehouseInboundRepository;

    @Autowired
    private WarehouseInboundItemRepository inboundItemRepository;

    @Autowired
    private WarehouseOutboundRepository outboundRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Autowired
    private WarehouseInventoryService warehouseInventoryService;

    private Warehouse warehouse;
    private NguoiDung user;
    private HoSoNhanVien employee;

    @BeforeEach
    public void setUp() {
        // Clean database
        salesOrderRepository.deleteAll();
        outboundRepository.deleteAll();
        inboundItemRepository.deleteAll();
        warehouseInboundRepository.deleteAll();
        warehouseManagerRepository.deleteAll();
        warehouseRepository.deleteAll();
        nguoiDungRepository.deleteAll();
        hoSoNhanVienRepository.deleteAll();

        // Create warehouse
        warehouse = new Warehouse();
        warehouse.setName("Kho Thuốc H2");
        warehouse.setCode("KTH2");
        warehouse.setLocation("Hà Nội");
        warehouse.setStatus("ACTIVE");
        warehouse.setCreatedAt(Instant.now());
        warehouse.setUpdatedAt(Instant.now());
        warehouse = warehouseRepository.save(warehouse);

        // Create User
        user = new NguoiDung();
        user.setTenDangNhap("test@tuanchaugroup.com.vn");
        user.setMatKhauMaHoa("encoded-pass");
        user.setTrangThai("ACTIVE");
        user = nguoiDungRepository.save(user);

        // Create Employee
        employee = new HoSoNhanVien();
        employee.setCode("NV001");
        employee.setName("Nguyên Bán Hàng");
        employee.setEmail(user.getTenDangNhap());
        employee.setNguoiDungId(user.getId());
        employee = hoSoNhanVienRepository.save(employee);

        // Make user a manager of the warehouse so they can access it
        WarehouseManager manager = new WarehouseManager();
        WarehouseManagerId managerId = new WarehouseManagerId(warehouse.getId(), employee.getId());
        manager.setId(managerId);
        warehouseManagerRepository.save(manager);

        // Mock Security Context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getTenDangNhap(),
                null,
                List.of(new SimpleGrantedAuthority("sales.orders.view"), new SimpleGrantedAuthority("sales.orders.manage"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Transactional
    public void testCreateAndCompleteSalesOrder_Success() {
        // 1. Setup inventory by receiving 100 units of a product
        WarehouseInbound inbound = new WarehouseInbound();
        inbound.setCode("PNK0001");
        inbound.setStatus(WarehouseInboundStatus.COMPLETED);
        inbound.setWarehouseId(warehouse.getId());
        inbound.setWarehouseName(warehouse.getName());
        inbound.setReceiptDate(LocalDate.now());
        inbound.setCreatedAt(Instant.now());
        
        WarehouseInboundItem item = new WarehouseInboundItem();
        item.setItemId("med-001");
        item.setItemCode("MED-CODE");
        item.setItemName("Paracetamol 500mg");
        item.setQuantity(BigDecimal.valueOf(100));
        item.setUnitPrice(BigDecimal.valueOf(1000));
        item.setLineTotal(BigDecimal.valueOf(100000));
        item.setUnit("Hộp");
        item.setBatchNumber("BATCH123");
        item.setExpiryDate(LocalDate.now().plusYears(1));
        inbound.addItem(item);
        
        warehouseInboundRepository.save(inbound);

        // Verify available stock is 100
        WarehouseInventoryPageResponse inv = warehouseInventoryService.findAll(0, 10, "MED-CODE", warehouse.getId());
        BigDecimal avail = inv.content().isEmpty() ? BigDecimal.ZERO : inv.content().get(0).availableQuantity();
        assertThat(avail).isEqualByComparingTo(BigDecimal.valueOf(100));

        // 2. Create a Sales Order Draft for 10 units
        SalesOrderUpsertRequest.SalesOrderItemRequest orderItem = new SalesOrderUpsertRequest.SalesOrderItemRequest(
                "med-001",
                "MED-CODE",
                "Paracetamol 500mg",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(8),
                "Hộp",
                "BATCH123",
                LocalDate.now().plusYears(1),
                "Bán lẻ"
        );

        SalesOrderUpsertRequest request = new SalesOrderUpsertRequest(
                "DH-MOCK-001",
                LocalDate.now(),
                warehouse.getId(),
                "Nguyễn Văn A",
                "0102030405",
                "Công ty TNHH A",
                "Hà Nội",
                "a@gmail.com",
                "TRANSFER",
                "UNPAID",
                "Đơn giao ngay",
                List.of(orderItem)
        );

        SalesOrderMutationResponse createRes = salesOrderService.create(request);
        assertThat(createRes.id()).isNotNull();
        assertThat(createRes.status()).isEqualTo(SalesOrderStatus.DRAFT);

        // Verify list contains order
        SalesOrderPageResponse listRes = salesOrderService.findAll(0, 10, null, "DRAFT", warehouse.getId(), "UNPAID", null, null);
        assertThat(listRes.content()).hasSize(1);
        assertThat(listRes.content().get(0).totalAmountAfterTax()).isEqualByComparingTo(BigDecimal.valueOf(12960)); // (10 * 1200) * 1.08 = 12960

        // 3. Complete the Sales Order
        SalesOrderMutationResponse completeRes = salesOrderService.complete(createRes.id());
        assertThat(completeRes.status()).isEqualTo(SalesOrderStatus.COMPLETED);

        // 4. Verify stock availability drops to 90
        WarehouseInventoryPageResponse inv2 = warehouseInventoryService.findAll(0, 10, "MED-CODE", warehouse.getId());
        BigDecimal newAvail = inv2.content().isEmpty() ? BigDecimal.ZERO : inv2.content().get(0).availableQuantity();
        assertThat(newAvail).isEqualByComparingTo(BigDecimal.valueOf(90));

        // 5. Verify a completed warehouse outbound was automatically generated
        SalesOrderDetailResponse detail = salesOrderService.findById(createRes.id());
        assertThat(detail.warehouseOutboundId()).isNotNull();
        assertThat(detail.paymentStatus()).isEqualTo("PAID");

        WarehouseOutbound outbound = outboundRepository.findById(detail.warehouseOutboundId()).orElse(null);
        assertThat(outbound).isNotNull();
        assertThat(outbound.getDestinationName()).contains("Công ty TNHH A");
        assertThat(outbound.getItems()).hasSize(1);
        assertThat(outbound.getItems().get(0).getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    @Transactional
    public void testCompleteSalesOrder_InsufficientStock_ThrowsException() {
        // Create draft sales order for 5 units without receiving any stock
        SalesOrderUpsertRequest.SalesOrderItemRequest orderItem = new SalesOrderUpsertRequest.SalesOrderItemRequest(
                "med-001",
                "MED-CODE",
                "Paracetamol 500mg",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(8),
                "Hộp",
                "BATCH123",
                LocalDate.now().plusYears(1),
                "Bán lẻ"
        );

        SalesOrderUpsertRequest request = new SalesOrderUpsertRequest(
                "DH-MOCK-002",
                LocalDate.now(),
                warehouse.getId(),
                "Nguyễn Văn A",
                null,
                null,
                null,
                null,
                "TRANSFER",
                "UNPAID",
                null,
                List.of(orderItem)
        );

        SalesOrderMutationResponse createRes = salesOrderService.create(request);

        // Attempting to complete order should fail with ResponseStatusException due to insufficient stock
        assertThatThrownBy(() -> salesOrderService.complete(createRes.id()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("So luong ton kha dung khong du");
    }
}
