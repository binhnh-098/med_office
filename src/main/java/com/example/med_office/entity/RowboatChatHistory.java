package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lich_su_chat_rowboat")
public class RowboatChatHistory {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "ten_dang_nhap", length = 100)
    private String username;

    @Column(name = "tin_nhan_nguoi_dung", length = 4000)
    private String userMessage;

    @Column(name = "tin_nhan_tro_ly", length = 4000)
    private String assistantMessage;

    @Lob
    @Column(name = "trang_thai_yeu_cau_json")
    private String requestStateJson;

    @Lob
    @Column(name = "trang_thai_phan_hoi_json")
    private String responseStateJson;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String status;

    @Column(name = "thong_bao_loi", length = 2000)
    private String errorMessage;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
