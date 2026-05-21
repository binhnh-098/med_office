package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowboatChatResponse {

    private List<RowboatMessage> messages = new ArrayList<>();

    private Object state;

    private Long historyId;

    private String status;

    private String requestedBy;

    private LocalDateTime createdAt;
}
