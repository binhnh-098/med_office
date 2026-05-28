USE med_office;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS doctor_meal_registrations_uuid_map AS
SELECT id AS old_id, UUID() AS new_id
FROM doctor_meal_registrations;

CREATE TABLE IF NOT EXISTS doctor_meal_registration_items_uuid_map AS
SELECT id AS old_id, UUID() AS new_id
FROM doctor_meal_registration_items;

CREATE TABLE IF NOT EXISTS doctor_meal_registration_item_snapshots_uuid_map AS
SELECT id AS old_id, UUID() AS new_id
FROM doctor_meal_registration_item_snapshots;

CREATE TABLE IF NOT EXISTS doctor_meal_dishes_uuid_map AS
SELECT id AS old_id, UUID() AS new_id
FROM doctor_meal_dishes;

DROP TABLE IF EXISTS doctor_meal_registration_item_snapshots_new;
DROP TABLE IF EXISTS doctor_meal_registration_items_new;
DROP TABLE IF EXISTS doctor_meal_registrations_new;
DROP TABLE IF EXISTS doctor_meal_dishes_new;

CREATE TABLE doctor_meal_registrations_new (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    week_year INT NOT NULL,
    week_number INT NOT NULL,
    week_label VARCHAR(64) NULL,
    week_start_date DATE NULL,
    week_end_date DATE NULL,
    username VARCHAR(128) NOT NULL,
    requester_username VARCHAR(128) NULL,
    requester_full_name VARCHAR(255) NULL,
    requester_department VARCHAR(255) NULL,
    requester_role VARCHAR(128) NULL,
    total_quantity INT NULL,
    total_amount DECIMAL(12,2) NULL,
    payload_json LONGTEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_doctor_meal_registrations_week_user (week_year, week_number, username),
    KEY idx_doctor_meal_registrations_week_requester (week_year, week_number, requester_username),
    KEY idx_doctor_meal_registrations_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE doctor_meal_registration_items_new (
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
        FOREIGN KEY (registration_id) REFERENCES doctor_meal_registrations_new (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE doctor_meal_registration_item_snapshots_new (
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
        FOREIGN KEY (registration_item_id) REFERENCES doctor_meal_registration_items_new (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE doctor_meal_dishes_new (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    week_year INT NOT NULL,
    week_number INT NOT NULL,
    day_of_week VARCHAR(32) NOT NULL,
    meal_date DATE NOT NULL,
    meal_id VARCHAR(32) NOT NULL,
    meal_label VARCHAR(32) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    calories INT NULL,
    serving_time VARCHAR(32) NULL,
    note VARCHAR(1000) NULL,
    created_by VARCHAR(128) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_doctor_meal_dishes_week_day (week_year, week_number, day_of_week),
    KEY idx_doctor_meal_dishes_date_meal (meal_date, meal_id),
    KEY idx_doctor_meal_dishes_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO doctor_meal_registrations_new (
    id, week_year, week_number, week_label, week_start_date, week_end_date,
    username, requester_username, requester_full_name, requester_department,
    requester_role, total_quantity, total_amount, payload_json, created_at
)
SELECT
    map.new_id, src.week_year, src.week_number, src.week_label, src.week_start_date, src.week_end_date,
    src.username, src.requester_username, src.requester_full_name, src.requester_department,
    src.requester_role, src.total_quantity, src.total_amount, src.payload_json, src.created_at
FROM doctor_meal_registrations src
JOIN doctor_meal_registrations_uuid_map map ON map.old_id = src.id;

INSERT INTO doctor_meal_registration_items_new (
    id, registration_id, meal_date, day_of_week, meal_id, meal_label, meal_quantity, meal_amount
)
SELECT
    item_map.new_id, registration_map.new_id, src.meal_date, src.day_of_week,
    src.meal_id, src.meal_label, src.meal_quantity, src.meal_amount
FROM doctor_meal_registration_items src
JOIN doctor_meal_registration_items_uuid_map item_map ON item_map.old_id = src.id
JOIN doctor_meal_registrations_uuid_map registration_map ON registration_map.old_id = src.registration_id;

INSERT INTO doctor_meal_registration_item_snapshots_new (
    id, registration_item_id, name, serving_time, quantity, unit_price, line_total
)
SELECT
    snapshot_map.new_id, item_map.new_id, src.name, src.serving_time,
    src.quantity, src.unit_price, src.line_total
FROM doctor_meal_registration_item_snapshots src
JOIN doctor_meal_registration_item_snapshots_uuid_map snapshot_map ON snapshot_map.old_id = src.id
JOIN doctor_meal_registration_items_uuid_map item_map ON item_map.old_id = src.registration_item_id;

INSERT INTO doctor_meal_dishes_new (
    id, week_year, week_number, day_of_week, meal_date, meal_id, meal_label,
    name, price, unit_price, calories, serving_time, note, created_by, created_at
)
SELECT
    map.new_id, src.week_year, src.week_number, src.day_of_week, src.meal_date, src.meal_id,
    src.meal_label, src.name, src.price, src.unit_price, src.calories, src.serving_time,
    src.note, src.created_by, src.created_at
FROM doctor_meal_dishes src
JOIN doctor_meal_dishes_uuid_map map ON map.old_id = src.id;

DROP TABLE doctor_meal_registration_item_snapshots;
DROP TABLE doctor_meal_registration_items;
DROP TABLE doctor_meal_registrations;
DROP TABLE doctor_meal_dishes;

RENAME TABLE
    doctor_meal_registrations_new TO doctor_meal_registrations,
    doctor_meal_registration_items_new TO doctor_meal_registration_items,
    doctor_meal_registration_item_snapshots_new TO doctor_meal_registration_item_snapshots,
    doctor_meal_dishes_new TO doctor_meal_dishes;

DROP TABLE IF EXISTS doctor_meal_registrations_uuid_map;
DROP TABLE IF EXISTS doctor_meal_registration_items_uuid_map;
DROP TABLE IF EXISTS doctor_meal_registration_item_snapshots_uuid_map;
DROP TABLE IF EXISTS doctor_meal_dishes_uuid_map;

SET FOREIGN_KEY_CHECKS = 1;
