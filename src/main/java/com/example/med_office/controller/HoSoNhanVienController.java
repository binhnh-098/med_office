package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.HoSoNhanVienRequest;
import com.example.med_office.dto.HoSoNhanVienResponse;
import com.example.med_office.dto.ImportResultResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.service.HoSoNhanVienService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@Validated
@RequestMapping("/api/ho-so-nhan-vien")
@Tag(name = "Ho so nhan vien", description = "Quan ly ho so nhan vien")
public class HoSoNhanVienController {

    private final HoSoNhanVienService hoSoNhanVienService;

    public HoSoNhanVienController(HoSoNhanVienService hoSoNhanVienService) {
        this.hoSoNhanVienService = hoSoNhanVienService;
    }

    @Operation(summary = "Lay danh sach ho so nhan vien")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<HoSoNhanVienResponse>>> findAll(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Size must be greater than 0") @Max(value = 100, message = "Size must be less than or equal to 100") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Boolean onlineBooking,
            @RequestParam(required = false) String nguoiDungId,
            @RequestParam(required = false) Boolean hasNguoiDungId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach ho so nhan vien thanh cong",
                hoSoNhanVienService.findAll(page, size, keyword, active, gender, onlineBooking, nguoiDungId, hasNguoiDungId)
        ));
    }

    @Operation(summary = "Xuat danh sach ho so nhan vien CSV")
    @GetMapping(path = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> export() {
        byte[] content = hoSoNhanVienService.exportCsv().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ho-so-nhan-vien.csv\"")
                .body(content);
    }

    @Operation(summary = "Import danh sach ho so nhan vien Excel")
    @PostMapping(
            path = {"/import", "/import-excel", "/upload"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ImportResultResponse>> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(
                "Import ho so nhan vien thanh cong",
                hoSoNhanVienService.importExcel(file)
        ));
    }

    @Operation(summary = "Lay chi tiet ho so nhan vien")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HoSoNhanVienResponse>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet ho so nhan vien thanh cong",
                hoSoNhanVienService.findById(id)
        ));
    }

    @Operation(summary = "Tao ho so nhan vien")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HoSoNhanVienResponse>> create(@Valid @RequestBody HoSoNhanVienRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao ho so nhan vien thanh cong",
                hoSoNhanVienService.create(request)
        ));
    }

    @Operation(summary = "Cap nhat ho so nhan vien")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HoSoNhanVienResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody HoSoNhanVienRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat ho so nhan vien thanh cong",
                hoSoNhanVienService.update(id, request)
        ));
    }

    @Operation(summary = "Xoa ho so nhan vien")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id) {
        hoSoNhanVienService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xoa ho so nhan vien thanh cong", null));
    }
}
