package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

public class IntegrationDTOs {

    @Getter
    @Setter
    public static class IntegrationChannelResponse {
        private String id;
        private String providerId;
        private String name;
        private String status;
        private String email;
        private String webhookUrl;
        private String webhookSecret;
        private String lastSync;
        private ClientConfigInfo config;
    }

    @Getter
    @Setter
    public static class ClientConfigInfo {
        private String email;
        private String clientId;
        private String clientSecret;
    }

    @Getter
    @Setter
    public static class IntegrationChannelConfigRequest {
        private String name;
        private String email;
        private String appPassword;
        private String clientId;
        private String clientSecret;
    }

    @Getter
    @Setter
    public static class IntegrationSyncLogResponse {
        private String id;
        private String accountName;
        private String action;
        private String status;
        private Integer responseCode;
        private Integer executionTime;
        private String errorMessage;
        private LocalDateTime createdAt;
    }
}
