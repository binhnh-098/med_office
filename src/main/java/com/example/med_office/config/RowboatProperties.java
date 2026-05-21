package com.example.med_office.config;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ai")
public class RowboatProperties {

    @NotBlank
    private String host = "https://integrate.api.nvidia.com/v1";

    @NotBlank
    private String provider = "nvidia";

    @NotBlank
    private String model = "meta/llama-3.1-70b-instruct";

    private String apiKey = "";

    private boolean mockOnError;

    private int maxTokens = 512;

    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    @NotNull
    private Duration readTimeout = Duration.ofSeconds(60);
}
