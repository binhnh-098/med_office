package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.CongVanDiResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.service.CongVanDiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping("/api/cong-van-di")
@Tag(name = "Cong van di", description = "Quan ly cong van di")
public class CongVanDiController {

    private final CongVanDiService congVanDiService;

    public CongVanDiController(CongVanDiService congVanDiService) {
        this.congVanDiService = congVanDiService;
    }

    @Operation(summary = "Lay danh sach cong van di")
    @GetMapping(path = "/danh-sach-cong-van-di", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<CongVanDiResponse>>> findAll(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Size must be greater than 0") @Max(value = 100, message = "Size must be less than or equal to 100") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String nguoiKyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayBanHanhFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayBanHanhTo
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach cong van di thanh cong",
                congVanDiService.findAll(page, size, keyword, trangThai, nguoiKyId, ngayBanHanhFrom, ngayBanHanhTo)
        ));
    }
}
