package com.example.med_office.service;

import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelResponse;
import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelConfigRequest;
import com.example.med_office.dto.IntegrationDTOs.IntegrationSyncLogResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GmailIngestionService {

    List<IntegrationChannelResponse> getChannels();

    IntegrationChannelResponse toggleChannel(String id);

    IntegrationChannelResponse configureChannel(String id, IntegrationChannelConfigRequest request);

    void syncChannel(String id);

    Page<IntegrationSyncLogResponse> getSyncLogs(int page, int size);

    void triggerScheduledSync();
}
