ALTER TABLE doctor_meal_registrations
    ADD COLUMN week_label VARCHAR(64) NULL,
    ADD COLUMN week_start_date DATE NULL,
    ADD COLUMN week_end_date DATE NULL,
    ADD COLUMN requester_username VARCHAR(128) NULL,
    ADD COLUMN requester_full_name VARCHAR(255) NULL,
    ADD COLUMN requester_department VARCHAR(255) NULL,
    ADD COLUMN requester_role VARCHAR(128) NULL,
    ADD COLUMN total_quantity INT NULL,
    ADD COLUMN total_amount DECIMAL(12,2) NULL,
    ADD KEY idx_doctor_meal_registrations_week_requester (week_year, week_number, requester_username);

CREATE TABLE IF NOT EXISTS doctor_meal_registration_items (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    registration_id CHAR(36) NOT NULL,
    meal_date DATE NOT NULL,
    day_of_week VARCHAR(32) NULL,
    meal_id VARCHAR(32) NULL,
    meal_label VARCHAR(32) NULL,
    meal_quantity INT NOT NULL,
    meal_amount DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_doctor_meal_registration_items_registration (registration_id),
    KEY idx_doctor_meal_registration_items_date_meal (meal_date, meal_id),
    CONSTRAINT fk_doctor_meal_registration_items_registration
        FOREIGN KEY (registration_id) REFERENCES doctor_meal_registrations (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS doctor_meal_registration_item_snapshots (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    registration_item_id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    serving_time VARCHAR(32) NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_doctor_meal_registration_item_snapshots_item (registration_item_id),
    CONSTRAINT fk_doctor_meal_registration_item_snapshots_item
        FOREIGN KEY (registration_item_id) REFERENCES doctor_meal_registration_items (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
