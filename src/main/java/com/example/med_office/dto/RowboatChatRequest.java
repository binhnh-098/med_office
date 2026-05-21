package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowboatChatRequest {

    @Valid
    @NotEmpty(message = "Messages must not be empty")
    @Schema(description = "Complete conversation history that Rowboat requires for each turn")
    private List<RowboatMessage> messages = new ArrayList<>();

    @Schema(description = "State returned by Rowboat from the previous turn; null for the first turn")
    private Object state;

    private String workflowId;

    private String testProfileId;
}
