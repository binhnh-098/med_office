package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.CongVanDenCreateRequest;
import com.example.med_office.dto.CongVanDenResponse;
import com.example.med_office.service.CongVanDenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cong-van-den")
@Tag(name = "Cong van den", description = "Quan ly cong van den")
public class CongVanDenController {

    private final CongVanDenService congVanDenService;

    public CongVanDenController(CongVanDenService congVanDenService) {
        this.congVanDenService = congVanDenService;
    }

    @Operation(summary = "Tao cong van den")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CongVanDenResponse>> create(@Valid @RequestBody CongVanDenCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tao cong van den thanh cong", congVanDenService.create(request)));
    }
}
