package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.ChucVuRequest;
import com.example.med_office.dto.ChucVuResponse;
import com.example.med_office.service.ChucVuService;
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
@RequestMapping("/api/chuc-vu")
@Tag(name = "Chuc vu", description = "Quan ly chuc vu")
public class ChucVuController {

    private final ChucVuService chucVuService;

    public ChucVuController(ChucVuService chucVuService) {
        this.chucVuService = chucVuService;
    }

    @Operation(summary = "Lay danh sach chuc vu")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ChucVuResponse>>> findAll(
            @RequestParam(required = false) String userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach chuc vu thanh cong",
                chucVuService.findAll(userId)
        ));
    }

    @Operation(summary = "Lay chi tiet chuc vu")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChucVuResponse>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet chuc vu thanh cong",
                chucVuService.findById(id)
        ));
    }

    @Operation(summary = "Tao chuc vu")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChucVuResponse>> create(@Valid @RequestBody ChucVuRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao chuc vu thanh cong",
                chucVuService.create(request)
        ));
    }

    @Operation(summary = "Cap nhat chuc vu")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChucVuResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody ChucVuRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat chuc vu thanh cong",
                chucVuService.update(id, request)
        ));
    }

    @Operation(summary = "Xoa chuc vu")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id) {
        chucVuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xoa chuc vu thanh cong", null));
    }
}
