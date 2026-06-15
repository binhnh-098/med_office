ALTER TABLE warehouse_inbound_items
    ADD COLUMN IF NOT EXISTS min_quantity DECIMAL(18,2) NULL AFTER expiry_date;

CREATE TABLE IF NOT EXISTS warehouse_inventory_min_quantities (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    inventory_key VARCHAR(512) NOT NULL,
    warehouse_id CHAR(36) NOT NULL,
    item_id VARCHAR(100) NULL,
    item_code VARCHAR(100) NULL,
    batch_number VARCHAR(100) NULL,
    expiry_date DATE NULL,
    unit VARCHAR(100) NULL,
    min_quantity DECIMAL(18,2) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_inventory_min_quantities_inventory_key (inventory_key),
    KEY idx_warehouse_inventory_min_quantities_warehouse_id (warehouse_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
