package com.example.med_office.service;

import com.example.med_office.config.RowboatProperties;
import com.example.med_office.dto.RowboatChatRequest;
import com.example.med_office.dto.RowboatChatResponse;
import com.example.med_office.dto.RowboatMessage;
import com.example.med_office.entity.RowboatChatHistory;
import com.example.med_office.repository.RowboatChatHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RowboatServiceImpl implements RowboatService {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_MOCK = "MOCK";

    private final RestClient rowboatRestClient;
    private final RowboatProperties rowboatProperties;
    private final RowboatChatHistoryRepository rowboatChatHistoryRepository;
    private final ObjectMapper objectMapper;

    public RowboatServiceImpl(
            RestClient rowboatRestClient,
            RowboatProperties rowboatProperties,
            RowboatChatHistoryRepository rowboatChatHistoryRepository
    ) {
        this.rowboatRestClient = rowboatRestClient;
        this.rowboatProperties = rowboatProperties;
        this.rowboatChatHistoryRepository = rowboatChatHistoryRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public RowboatChatResponse chat(RowboatChatRequest request) {
        ensureConfigured();
        String username = currentUsername();
        String userMessage = extractLatestUserMessage(request);

        try {
            Map<?, ?> responseBody = rowboatRestClient.post()
                    .uri("/chat/completions")
                    .headers(headers -> headers.setBearerAuth(rowboatProperties.getApiKey()))
                    .body(buildNvidiaRequestBody(request))
                    .retrieve()
                    .body(Map.class);

            RowboatChatResponse response = toChatResponse(responseBody, request.getState());

            RowboatChatHistory history = saveHistory(
                    username,
                    userMessage,
                    request.getState(),
                    extractAssistantMessage(response),
                    request.getState(),
                    STATUS_SUCCESS,
                    null
            );
            enrichResponse(response, history);
            return response;
        } catch (RestClientResponseException ex) {
            if (rowboatProperties.isMockOnError()) {
                return buildMockResponse(request, username, userMessage, ex.getResponseBodyAsString());
            }
            saveHistory(
                    username,
                    userMessage,
                    request.getState(),
                    null,
                    null,
                    STATUS_FAILED,
                    "NVIDIA request failed with status %s".formatted(ex.getStatusCode().value())
            );
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "NVIDIA request failed with status %s".formatted(ex.getStatusCode().value()),
                    ex
            );
        } catch (RestClientException ex) {
            if (rowboatProperties.isMockOnError()) {
                return buildMockResponse(request, username, userMessage, ex.getMessage());
            }
            saveHistory(username, userMessage, request.getState(), null, null, STATUS_FAILED, "Could not reach NVIDIA AI");
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not reach NVIDIA AI", ex);
        }
    }

    private void ensureConfigured() {
        if (!"nvidia".equalsIgnoreCase(rowboatProperties.getProvider())) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Only NVIDIA provider is supported");
        }

        if (!StringUtils.hasText(rowboatProperties.getApiKey())) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "NVIDIA API key is missing");
        }
    }

    private void enrichResponse(RowboatChatResponse response, RowboatChatHistory history) {
        response.setHistoryId(history.getId());
        response.setStatus(history.getStatus());
        response.setRequestedBy(history.getUsername());
        response.setCreatedAt(history.getCreatedAt());
    }

    private RowboatChatHistory saveHistory(
            String username,
            String userMessage,
            Object requestState,
            String assistantMessage,
            Object responseState,
            String status,
            String errorMessage
    ) {
        RowboatChatHistory history = new RowboatChatHistory();
        history.setUsername(username);
        history.setUserMessage(truncate(userMessage, 4000));
        history.setAssistantMessage(truncate(assistantMessage, 4000));
        history.setRequestStateJson(toJson(requestState));
        history.setResponseStateJson(toJson(responseState));
        history.setStatus(status);
        history.setErrorMessage(truncate(errorMessage, 2000));
        return rowboatChatHistoryRepository.save(history);
    }

    private Map<String, Object> buildNvidiaRequestBody(RowboatChatRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", rowboatProperties.getModel());
        body.put("messages", request.getMessages().stream()
                .map(message -> Map.of(
                        "role", normalizeRole(message.getRole()),
                        "content", StringUtils.hasText(message.getContent()) ? message.getContent() : ""
                ))
                .toList());
        body.put("temperature", 0.2);
        body.put("top_p", 0.7);
        body.put("max_tokens", rowboatProperties.getMaxTokens());
        body.put("stream", false);
        return body;
    }

    private RowboatChatResponse toChatResponse(Map<?, ?> responseBody, Object state) {
        if (responseBody == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "NVIDIA returned an empty response");
        }

        String assistantContent = extractAssistantContent(responseBody);
        if (!StringUtils.hasText(assistantContent)) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot extract NVIDIA response text");
        }

        RowboatMessage assistantMessage = new RowboatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(assistantContent);
        assistantMessage.setAgenticResponseType("external");
        assistantMessage.setAgenticSender("nvidia");

        RowboatChatResponse response = new RowboatChatResponse();
        response.setMessages(List.of(assistantMessage));
        response.setState(state);
        return response;
    }

    private String extractAssistantContent(Map<?, ?> responseBody) {
        Object choicesObject = responseBody.get("choices");
        if (!(choicesObject instanceof List<?> choices)) {
            return null;
        }

        for (Object choiceObject : choices) {
            if (!(choiceObject instanceof Map<?, ?> choice)) {
                continue;
            }

            Object messageObject = choice.get("message");
            if (!(messageObject instanceof Map<?, ?> message)) {
                continue;
            }

            Object contentObject = message.get("content");
            if (contentObject instanceof String content && StringUtils.hasText(content)) {
                return content;
            }
        }
        return null;
    }

    private RowboatChatResponse buildMockResponse(
            RowboatChatRequest request,
            String username,
            String userMessage,
            String errorMessage
    ) {
        RowboatMessage assistantMessage = new RowboatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent("Mock AI response: NVIDIA service is unavailable. Reason: " + truncate(errorMessage, 300));
        assistantMessage.setAgenticResponseType("mock");
        assistantMessage.setAgenticSender("nvidia");

        RowboatChatResponse response = new RowboatChatResponse();
        response.setMessages(List.of(assistantMessage));
        response.setState(request.getState());

        RowboatChatHistory history = saveHistory(
                username,
                userMessage,
                request.getState(),
                assistantMessage.getContent(),
                request.getState(),
                STATUS_MOCK,
                truncate(errorMessage, 2000)
        );
        enrichResponse(response, history);
        return response;
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return "user";
        }
        return switch (role.toLowerCase()) {
            case "assistant", "system", "user" -> role.toLowerCase();
            default -> "user";
        };
    }

    private String extractLatestUserMessage(RowboatChatRequest request) {
        for (int index = request.getMessages().size() - 1; index >= 0; index--) {
            RowboatMessage message = request.getMessages().get(index);
            if ("user".equalsIgnoreCase(message.getRole()) && StringUtils.hasText(message.getContent())) {
                return message.getContent();
            }
        }
        return null;
    }

    private String extractAssistantMessage(RowboatChatResponse response) {
        for (int index = response.getMessages().size() - 1; index >= 0; index--) {
            RowboatMessage message = response.getMessages().get(index);
            if ("assistant".equalsIgnoreCase(message.getRole()) && StringUtils.hasText(message.getContent())) {
                return message.getContent();
            }
        }
        return null;
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            return null;
        }
        return authentication.getName();
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return truncate(String.valueOf(value), 4000);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
