package com.example.med_office.controller;

import java.util.Map;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.service.DoctorMealsService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@RequestMapping("/api/doctor-meals")
@Tag(name = "Doctor meals", description = "Dang ky va lich su suat an")
public class DoctorMealsController {

    private final DoctorMealsService doctorMealsService;

    public DoctorMealsController(DoctorMealsService doctorMealsService) {
        this.doctorMealsService = doctorMealsService;
    }

    @Operation(summary = "Lay menu va lua chon da luu theo tuan")
    @GetMapping(path = "/week-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWeekData(
            @RequestParam int weekYear,
            @RequestParam int weekNumber,
            @RequestParam(required = false) String username,
            Authentication authentication
    ) {
        String user = resolveUser(authentication, username);
        Map<String, Object> data = doctorMealsService.getWeekData(weekYear, weekNumber, user);
        return ResponseEntity.ok(ApiResponse.success("Lay du lieu tuan thanh cong", data));
    }

    @Operation(summary = "Tao phieu dang ky suat an")
    @PostMapping(path = "/registrations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> createRegistration(
            @RequestBody JsonNode body,
            Authentication authentication
    ) {
        if (body == null || body.isNull() || !body.has("week")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing week in body");
        }
        String user = authentication.getName();
        Map<String, Object> data = doctorMealsService.createRegistration(user, body);
        return ResponseEntity.ok(ApiResponse.success("Luu dang ky thanh cong", data));
    }

    @Operation(summary = "Danh sach phieu dang ky theo tuan")
    @GetMapping(path = "/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> listRegistrations(
            @RequestParam int weekYear,
            @RequestParam int weekNumber,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset,
            Authentication authentication
    ) {
        String user = resolveUser(authentication, username);
        Map<String, Object> data = doctorMealsService.listRegistrations(weekYear, weekNumber, user, limit, offset);
        return ResponseEntity.ok(ApiResponse.success("Lay danh sach thanh cong", data));
    }

    @Operation(summary = "Chi tiet phieu dang ky")
    @GetMapping(path = "/registrations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRegistration(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Map<String, Object> data = doctorMealsService.getRegistrationDetail(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Lay chi tiet thanh cong", data));
    }

    @Operation(summary = "Huy phieu dang ky")
    @DeleteMapping(path = "/registrations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> deleteRegistration(
            @PathVariable Long id,
            Authentication authentication
    ) {
        doctorMealsService.deleteRegistration(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Da huy dang ky", null));
    }

    private static String resolveUser(Authentication authentication, String requestedUsername) {
        String principal = authentication.getName();
        if (requestedUsername != null && !requestedUsername.isBlank() && !principal.equals(requestedUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access another user's meal data");
        }
        return principal;
    }
}
