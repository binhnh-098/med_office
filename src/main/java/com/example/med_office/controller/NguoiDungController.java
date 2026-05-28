package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.NguoiDungResponse;
import com.example.med_office.dto.NguoiDungRoleUpdateRequest;
import com.example.med_office.service.NguoiDungService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nguoi-dung")
@Tag(name = "Nguoi dung", description = "Quan ly tai khoan va phan quyen")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    @Operation(summary = "Danh sach tai khoan")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<NguoiDungResponse>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success("Lay danh sach tai khoan thanh cong", nguoiDungService.getUsers()));
    }

    @Operation(summary = "Cap nhat chuc vu va role cua tai khoan")
    @PutMapping(path = "/{id}/chuc-vu", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NguoiDungResponse>> updateUserRole(
            @PathVariable String id,
            @Valid @RequestBody NguoiDungRoleUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cap nhat phan quyen thanh cong", nguoiDungService.updateUserRole(id, request)));
    }
}
