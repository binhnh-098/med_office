package com.example.med_office.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rowboat_chat_histories")
public class RowboatChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "user_message", length = 4000)
    private String userMessage;

    @Column(name = "assistant_message", length = 4000)
    private String assistantMessage;

    @Lob
    @Column(name = "request_state_json")
    private String requestStateJson;

    @Lob
    @Column(name = "response_state_json")
    private String responseStateJson;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
