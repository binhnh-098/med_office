package com.example.med_office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseInboundRejectRequest(
        @NotBlank(message = "Ly do tu choi khong duoc de trong")
        @Size(max = 2000, message = "Ly do tu choi khong duoc vuot qua 2000 ky tu")
        String reason
) {
}
