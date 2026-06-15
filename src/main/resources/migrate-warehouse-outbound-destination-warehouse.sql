ALTER TABLE warehouse_outbounds
    ADD COLUMN destination_warehouse_id CHAR(36) NULL AFTER warehouse_name;

ALTER TABLE warehouse_outbounds
    ADD KEY idx_warehouse_outbounds_destination_warehouse_id (destination_warehouse_id);

ALTER TABLE warehouse_outbounds
    ADD CONSTRAINT fk_warehouse_outbounds_destination_warehouse
        FOREIGN KEY (destination_warehouse_id) REFERENCES warehouses (id)
        ON DELETE RESTRICT;
