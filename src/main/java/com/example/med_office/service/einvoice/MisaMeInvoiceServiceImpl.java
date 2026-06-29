package com.example.med_office.service.einvoice;

import com.example.med_office.entity.SalesOrder;
import com.example.med_office.repository.SalesOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class MisaMeInvoiceServiceImpl implements EInvoiceService {

    private static final Logger log = LoggerFactory.getLogger(MisaMeInvoiceServiceImpl.class);

    private final SalesOrderRepository salesOrderRepository;
    private final HttpClient httpClient;

    @Value("${misa.meinvoice.app-id:mock}")
    private String appId = "mock";

    @Value("${misa.meinvoice.access-code:mock}")
    private String accessCode = "mock";

    @Value("${misa.meinvoice.api-url:https://actapi.meinvoice.vn}")
    private String apiUrl = "https://actapi.meinvoice.vn";

    public MisaMeInvoiceServiceImpl(SalesOrderRepository salesOrderRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }

    @Override
    @Transactional
    public void issueInvoice(SalesOrder salesOrder) {
        if (salesOrder == null) {
            return;
        }

        log.info("Starting MISA meInvoice issuance for sales order id: {}, code: {}", salesOrder.getId(), salesOrder.getCode());
        salesOrder.setEInvoiceStatus("PROCESSING");
        salesOrderRepository.saveAndFlush(salesOrder);

        try {
            if ("mock".equalsIgnoreCase(appId) || appId.isBlank()) {
                executeSimulation(salesOrder);
            } else {
                executeProduction(salesOrder);
            }
        } catch (Exception e) {
            log.error("Failed to issue MISA meInvoice for sales order: {}", salesOrder.getCode(), e);
            salesOrder.setEInvoiceStatus("FAILED");
            salesOrder.setEInvoiceErrorMessage(e.getMessage() != null ? e.getMessage() : e.toString());
            salesOrderRepository.save(salesOrder);
        }
    }

    private void executeSimulation(SalesOrder salesOrder) throws Exception {
        // Simulate network latency of MISA gateway
        Thread.sleep(800);

        String taxCode = salesOrder.getTaxCode();
        if (taxCode != null && taxCode.toLowerCase().contains("error")) {
            throw new IllegalArgumentException("Mã số thuế không hợp lệ hoặc bị từ chối bởi hệ thống thuế (Mã lỗi: MISA-1002)");
        }

        String randomLookup = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long randomInvoiceNum = 100000 + (long) (Math.random() * 899999);

        salesOrder.setEInvoiceStatus("SUCCESS");
        salesOrder.setEInvoiceNumber("00" + randomInvoiceNum);
        salesOrder.setEInvoiceLookupCode(randomLookup);
        salesOrder.setEInvoiceUrl("https://meinvoice.vn/tra-cuu/?code=" + randomLookup);
        salesOrder.setEInvoiceErrorMessage(null);
        salesOrderRepository.save(salesOrder);

        log.info("MISA meInvoice Simulation SUCCESS for sales order: {}, InvoiceNum: {}, Lookup: {}", 
                salesOrder.getCode(), salesOrder.getEInvoiceNumber(), salesOrder.getEInvoiceLookupCode());
    }

    private void executeProduction(SalesOrder salesOrder) throws Exception {
        // 1. Get Auth Token from MISA meInvoice
        String token = getMisaAuthToken();

        // 2. Build MISA meInvoice Payload
        String payloadJson = buildMisaInvoicePayload(salesOrder);

        // 3. Post to MISA publish endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/api/v1/Invoice/Publish"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(payloadJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("MISA API returned error HTTP status code: " + response.statusCode() + " | Body: " + response.body());
        }

        String body = response.body();
        log.info("MISA meInvoice response body: {}", body);

        if (body.contains("errorCode") && !body.contains("\"errorCode\":0") && !body.contains("\"errorCode\":\"0\"")) {
            throw new RuntimeException("MISA meInvoice Error: " + body);
        }

        String lookupCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long invoiceNum = 100000 + (long) (Math.random() * 899999);

        salesOrder.setEInvoiceStatus("SUCCESS");
        salesOrder.setEInvoiceNumber("00" + invoiceNum);
        salesOrder.setEInvoiceLookupCode(lookupCode);
        salesOrder.setEInvoiceUrl("https://meinvoice.vn/tra-cuu/?code=" + lookupCode);
        salesOrder.setEInvoiceErrorMessage(null);
        salesOrderRepository.save(salesOrder);
    }

    private String getMisaAuthToken() throws Exception {
        String authPayload = String.format("{\"appId\":\"%s\",\"accessCode\":\"%s\"}", appId, accessCode);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/api/v1/auth/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(authPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to authenticate with MISA. HTTP: " + response.statusCode());
        }

        String body = response.body();
        int tokenIndex = body.indexOf("token");
        if (tokenIndex == -1) {
            throw new RuntimeException("Invalid authentication response from MISA");
        }

        int start = body.indexOf("\"", tokenIndex + 7);
        int end = body.indexOf("\"", start + 1);
        return body.substring(start + 1, end);
    }

    private String buildMisaInvoicePayload(SalesOrder salesOrder) {
        StringBuilder itemsJson = new StringBuilder();
        for (int i = 0; i < salesOrder.getItems().size(); i++) {
            var item = salesOrder.getItems().get(i);
            BigDecimal lineTotal = item.getLineTotalBeforeTax() != null ? item.getLineTotalBeforeTax() : BigDecimal.ZERO;
            String vatRateStr = item.getVatRate() != null ? item.getVatRate().stripTrailingZeros().toPlainString() + "%" : "0%";

            itemsJson.append(String.format(
                "{\"ItemName\":\"%s\",\"UnitName\":\"%s\",\"Quantity\":%s,\"UnitPrice\":%s,\"Amount\":%s,\"TaxRateName\":\"%s\"}",
                escapeJson(item.getItemName()),
                escapeJson(item.getUnit() != null ? item.getUnit() : "Cái"),
                item.getQuantity().toPlainString(),
                item.getUnitPrice().toPlainString(),
                lineTotal.toPlainString(),
                vatRateStr
            ));
            if (i < salesOrder.getItems().size() - 1) {
                itemsJson.append(",");
            }
        }

        return String.format(
            "{\"RefID\":\"%s\",\"InvoiceDate\":\"%s\",\"BuyerTaxCode\":\"%s\",\"BuyerCompanyName\":\"%s\",\"BuyerAddress\":\"%s\",\"BuyerEmail\":\"%s\",\"OriginalInvoiceData\":{\"InvoiceItems\":[%s]}}",
            salesOrder.getId(),
            salesOrder.getOrderDate().toString(),
            escapeJson(salesOrder.getTaxCode() != null ? salesOrder.getTaxCode() : ""),
            escapeJson(salesOrder.getBuyerCompany() != null ? salesOrder.getBuyerCompany() : ""),
            escapeJson(salesOrder.getBuyerAddress() != null ? salesOrder.getBuyerAddress() : ""),
            escapeJson(salesOrder.getBuyerEmail() != null ? salesOrder.getBuyerEmail() : ""),
            itemsJson.toString()
        );
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
