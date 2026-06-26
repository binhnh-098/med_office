package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelResponse;
import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelConfigRequest;
import com.example.med_office.dto.IntegrationDTOs.IntegrationSyncLogResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.service.GmailIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/integration")
@Tag(name = "Omnichannel Integration", description = "Endpoints for managing integrations and sync logs")
public class IntegrationController {

    private final GmailIngestionService gmailIngestionService;

    public IntegrationController(GmailIngestionService gmailIngestionService) {
        this.gmailIngestionService = gmailIngestionService;
    }

    @Operation(summary = "Get list of all integration channels")
    @GetMapping("/channels")
    public ResponseEntity<ApiResponse<List<IntegrationChannelResponse>>> getChannels() {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched integration channels successfully",
                gmailIngestionService.getChannels()
        ));
    }

    @Operation(summary = "Toggle connection status of a channel")
    @PostMapping("/channels/{id}/toggle")
    public ResponseEntity<ApiResponse<IntegrationChannelResponse>> toggleChannel(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Toggled channel connection status successfully",
                gmailIngestionService.toggleChannel(id)
        ));
    }

    @Operation(summary = "Save credentials config for a channel")
    @PostMapping("/channels/{id}/config")
    public ResponseEntity<ApiResponse<IntegrationChannelResponse>> configureChannel(
            @PathVariable String id,
            @Valid @RequestBody IntegrationChannelConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Saved channel configuration successfully",
                gmailIngestionService.configureChannel(id, request)
        ));
    }

    @Operation(summary = "Trigger manual sync of a channel")
    @PostMapping("/channels/{id}/sync")
    public ResponseEntity<ApiResponse<Void>> syncChannel(@PathVariable String id) {
        gmailIngestionService.syncChannel(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Triggered integration channel sync successfully",
                null
        ));
    }

    @Operation(summary = "Get integration sync log history")
    @GetMapping("/sync-logs")
    public ResponseEntity<ApiResponse<PagedResponse<IntegrationSyncLogResponse>>> getSyncLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<IntegrationSyncLogResponse> logsPage = gmailIngestionService.getSyncLogs(page, size);
        
        PagedResponse<IntegrationSyncLogResponse> pagedResponse = new PagedResponse<>(
                logsPage.getContent(),
                logsPage.getNumber(),
                logsPage.getSize(),
                logsPage.getTotalElements(),
                logsPage.getTotalPages(),
                logsPage.hasNext(),
                logsPage.hasPrevious()
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Fetched sync logs successfully",
                pagedResponse
        ));
    }
}
