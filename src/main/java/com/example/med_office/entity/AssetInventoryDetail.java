package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "asset_inventory_details")
public class AssetInventoryDetail {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "inventory_id", nullable = false, length = 36)
    private String inventoryId;

    @Column(name = "asset_id", nullable = false, length = 36)
    private String assetId;

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent = true;

    @Column(name = "current_status", nullable = false, length = 50)
    private String currentStatus;

    @Column(name = "actual_status", nullable = false, length = 50)
    private String actualStatus;

    @Column(name = "note", length = 1000)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", insertable = false, updatable = false)
    private AssetInventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", insertable = false, updatable = false)
    private Asset asset;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UuidUtils.newUuid();
        }
    }
}
