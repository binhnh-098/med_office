package com.example.med_office.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
@EnableConfigurationProperties(RowboatProperties.class)
public class RowboatConfig {

    @Bean
    RestClient.Builder rowboatRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    RestClient rowboatRestClient(RestClient.Builder builder, RowboatProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getConnectTimeout())
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());

        return builder
                .baseUrl(properties.getHost())
                .requestFactory(requestFactory)
                .build();
    }
}
