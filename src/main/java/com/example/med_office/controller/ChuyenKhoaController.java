package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.ChuyenKhoaRequest;
import com.example.med_office.dto.ChuyenKhoaResponse;
import com.example.med_office.service.ChuyenKhoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/chuyen-khoa")
@Tag(name = "Chuyen khoa", description = "Quan ly chuyen khoa")
public class ChuyenKhoaController {

    private final ChuyenKhoaService chuyenKhoaService;

    public ChuyenKhoaController(ChuyenKhoaService chuyenKhoaService) {
        this.chuyenKhoaService = chuyenKhoaService;
    }

    @Operation(summary = "Lay danh sach chuyen khoa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ChuyenKhoaResponse>>> findAll(
            @RequestParam(required = false) String userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach chuyen khoa thanh cong",
                chuyenKhoaService.findAll(userId)
        ));
    }

    @Operation(summary = "Lay chi tiet chuyen khoa")
    @GetMapping(path = "/{idChuyenKhoa}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChuyenKhoaResponse>> findById(@PathVariable String idChuyenKhoa) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet chuyen khoa thanh cong",
                chuyenKhoaService.findById(idChuyenKhoa)
        ));
    }

    @Operation(summary = "Tao chuyen khoa")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChuyenKhoaResponse>> create(@Valid @RequestBody ChuyenKhoaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao chuyen khoa thanh cong",
                chuyenKhoaService.create(request)
        ));
    }

    @Operation(summary = "Cap nhat chuyen khoa")
    @PutMapping(path = "/{idChuyenKhoa}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChuyenKhoaResponse>> update(
            @PathVariable String idChuyenKhoa,
            @Valid @RequestBody ChuyenKhoaRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat chuyen khoa thanh cong",
                chuyenKhoaService.update(idChuyenKhoa, request)
        ));
    }

    @Operation(summary = "Xoa chuyen khoa")
    @DeleteMapping(path = "/{idChuyenKhoa}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String idChuyenKhoa) {
        chuyenKhoaService.delete(idChuyenKhoa);
        return ResponseEntity.ok(ApiResponse.success("Xoa chuyen khoa thanh cong", null));
    }
}
