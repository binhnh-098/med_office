package com.example.med_office.dto;

import jakarta.validation.constraints.Size;

public record WarehouseOutboundApprovalRequest(
        @Size(max = 2000, message = "Ghi chu duyet khong duoc vuot qua 2000 ky tu")
        String note
) {
}
