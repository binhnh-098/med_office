package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

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

    public List<RowboatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<RowboatMessage> messages) {
        this.messages = messages;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getTestProfileId() {
        return testProfileId;
    }

    public void setTestProfileId(String testProfileId) {
        this.testProfileId = testProfileId;
    }
}
