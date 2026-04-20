package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.RowboatChatRequest;
import com.example.med_office.dto.RowboatChatResponse;
import com.example.med_office.service.RowboatService;
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
@RequestMapping("/api/rowboat")
@Tag(name = "AI Chat", description = "Server-side integration with the NVIDIA chat API")
public class RowboatController {

    private final RowboatService rowboatService;

    public RowboatController(RowboatService rowboatService) {
        this.rowboatService = rowboatService;
    }

    @Operation(summary = "Send one chat turn to NVIDIA AI")
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RowboatChatResponse>> chat(@Valid @RequestBody RowboatChatRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "AI chat completed successfully",
                rowboatService.chat(request)
        ));
    }
}
