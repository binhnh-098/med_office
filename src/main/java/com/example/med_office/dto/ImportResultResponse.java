package com.example.med_office.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ImportResultResponse")
public record ImportResultResponse(
        int created,
        int updated,
        int skipped
) {
}
