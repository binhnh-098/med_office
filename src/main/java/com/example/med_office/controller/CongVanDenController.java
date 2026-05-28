package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.CongVanDenCreateRequest;
import com.example.med_office.dto.CongVanDenResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.service.CongVanDenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping("/api/cong-van-den")
@Tag(name = "Cong van den", description = "Quan ly cong van den")
public class CongVanDenController {

    private final CongVanDenService congVanDenService;

    public CongVanDenController(CongVanDenService congVanDenService) {
        this.congVanDenService = congVanDenService;
    }

    @Operation(summary = "Lay danh sach cong van den")
    @GetMapping(path = "/danh-sach-cong-van-den", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<CongVanDenResponse>>> findAll(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Size must be greater than 0") @Max(value = 100, message = "Size must be less than or equal to 100") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String donViGuiId,
            @RequestParam(required = false) Boolean daXuLy,
            @RequestParam(required = false) Boolean daDoc,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayNhanFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayNhanTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayVanBanFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayVanBanTo
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach cong van den thanh cong",
                congVanDenService.findAll(
                        page,
                        size,
                        keyword,
                        trangThai,
                        donViGuiId,
                        daXuLy,
                        daDoc,
                        ngayNhanFrom,
                        ngayNhanTo,
                        ngayVanBanFrom,
                        ngayVanBanTo
                )
        ));
    }

    @Operation(summary = "Tao cong van den")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CongVanDenResponse>> create(@Valid @RequestBody CongVanDenCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tao cong van den thanh cong", congVanDenService.create(request)));
    }
}
