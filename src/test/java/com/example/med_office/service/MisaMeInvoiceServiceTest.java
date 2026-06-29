package com.example.med_office.service;

import com.example.med_office.entity.SalesOrder;
import com.example.med_office.repository.SalesOrderRepository;
import com.example.med_office.service.einvoice.MisaMeInvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MisaMeInvoiceServiceTest {

    private SalesOrderRepository salesOrderRepository;
    private MisaMeInvoiceServiceImpl eInvoiceService;

    @BeforeEach
    public void setUp() {
        salesOrderRepository = mock(SalesOrderRepository.class);
        eInvoiceService = new MisaMeInvoiceServiceImpl(salesOrderRepository);
    }

    @Test
    public void testMockInvoiceIssuance_Success() {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId("test-sales-order-id");
        salesOrder.setCode("DH-TEST-001");
        salesOrder.setTaxCode("0102030405");
        salesOrder.setBuyerCompany("Công ty TNHH Antigravity");
        salesOrder.setOrderDate(LocalDate.now());
        salesOrder.setItems(new ArrayList<>());

        eInvoiceService.issueInvoice(salesOrder);

        ArgumentCaptor<SalesOrder> captor = ArgumentCaptor.forClass(SalesOrder.class);
        verify(salesOrderRepository, atLeastOnce()).save(captor.capture());

        SalesOrder saved = captor.getValue();
        assertEquals("SUCCESS", saved.getEInvoiceStatus());
        assertNotNull(saved.getEInvoiceNumber());
        assertNotNull(saved.getEInvoiceLookupCode());
        assertTrue(saved.getEInvoiceUrl().contains(saved.getEInvoiceLookupCode()));
        assertNull(saved.getEInvoiceErrorMessage());
    }

    @Test
    public void testMockInvoiceIssuance_ErrorTaxCode() {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId("test-sales-order-error-id");
        salesOrder.setCode("DH-TEST-ERROR");
        salesOrder.setTaxCode("0102030405-ERROR"); // triggers mock error
        salesOrder.setItems(new ArrayList<>());

        eInvoiceService.issueInvoice(salesOrder);

        ArgumentCaptor<SalesOrder> captor = ArgumentCaptor.forClass(SalesOrder.class);
        verify(salesOrderRepository, atLeastOnce()).save(captor.capture());

        SalesOrder saved = captor.getValue();
        assertEquals("FAILED", saved.getEInvoiceStatus());
        assertNull(saved.getEInvoiceNumber());
        assertNull(saved.getEInvoiceLookupCode());
        assertNotNull(saved.getEInvoiceErrorMessage());
        assertTrue(saved.getEInvoiceErrorMessage().contains("Mã số thuế không hợp lệ"));
    }
}
