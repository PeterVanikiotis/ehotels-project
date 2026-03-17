-- schema.sql
-- This script only creates the tables/constraints.

-- Data reset + inserts live in populate.sql.

CREATE TABLE IF NOT EXISTS hotel_chain (
    central_office_id SERIAL PRIMARY KEY,
    chain_name VARCHAR(100) NOT NULL UNIQUE,
    street_name VARCHAR(100) NOT NULL,
    building_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS hotel_chain_email (
    central_office_id INT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    PRIMARY KEY (central_office_id, email_address),
    CONSTRAINT fk_hotel_chain_email
    FOREIGN KEY (central_office_id)
    REFERENCES hotel_chain(central_office_id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS hotel_chain_phone (
    central_office_id INT NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    PRIMARY KEY (central_office_id, phone_number),
    CONSTRAINT fk_hotel_chain_phone
    FOREIGN KEY (central_office_id)
    REFERENCES hotel_chain(central_office_id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS employee (
    ssn VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS manager (
    ssn VARCHAR(20) PRIMARY KEY,
    CONSTRAINT fk_manager_employee
    FOREIGN KEY (ssn)
    REFERENCES employee(ssn)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS role (
    role_name VARCHAR(100) PRIMARY KEY
    );

CREATE TABLE IF NOT EXISTS hotel (
    hotel_id SERIAL PRIMARY KEY,
    central_office_id INT NOT NULL,
    manager_ssn VARCHAR(20) NOT NULL UNIQUE,
    hotel_name VARCHAR(150) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    area VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    province_state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    number_of_rooms INT NOT NULL,
    rating INT NOT NULL,
    CONSTRAINT fk_hotel_chain
    FOREIGN KEY (central_office_id)
    REFERENCES hotel_chain(central_office_id)
    ON DELETE CASCADE,
    CONSTRAINT fk_hotel_manager
    FOREIGN KEY (manager_ssn)
    REFERENCES manager(ssn)
    ON DELETE RESTRICT,
    CONSTRAINT chk_hotel_number_of_rooms
    CHECK (number_of_rooms > 0),
    CONSTRAINT chk_hotel_rating
    CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uq_hotel_address
    UNIQUE (street_name, street_number, postal_code)
    );

CREATE TABLE IF NOT EXISTS hotel_email (
    hotel_id INT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    PRIMARY KEY (hotel_id, email_address),
    CONSTRAINT fk_hotel_email
    FOREIGN KEY (hotel_id)
    REFERENCES hotel(hotel_id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS hotel_phone (
    hotel_id INT NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    PRIMARY KEY (hotel_id, phone_number),
    CONSTRAINT fk_hotel_phone
    FOREIGN KEY (hotel_id)
    REFERENCES hotel(hotel_id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS works_as (
    ssn VARCHAR(20) NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    hotel_id INT NOT NULL,
    PRIMARY KEY (ssn, role_name, hotel_id),
    CONSTRAINT fk_works_as_employee
    FOREIGN KEY (ssn)
    REFERENCES employee(ssn)
    ON DELETE CASCADE,
    CONSTRAINT fk_works_as_role
    FOREIGN KEY (role_name)
    REFERENCES role(role_name)
    ON DELETE RESTRICT,
    CONSTRAINT fk_works_as_hotel
    FOREIGN KEY (hotel_id)
    REFERENCES hotel(hotel_id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS room (
    hotel_id INT NOT NULL,
    room_number INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    has_tv BOOLEAN NOT NULL DEFAULT TRUE,
    has_air_conditioner BOOLEAN NOT NULL DEFAULT TRUE,
    has_fridge BOOLEAN NOT NULL DEFAULT FALSE,
    room_extended_status BOOLEAN NOT NULL DEFAULT FALSE,
    room_capacity INT NOT NULL,
    room_view_type VARCHAR(20) NOT NULL,
    damage_status VARCHAR(20) NOT NULL DEFAULT 'none',
    PRIMARY KEY (hotel_id, room_number),
    CONSTRAINT fk_room_hotel
    FOREIGN KEY (hotel_id)
    REFERENCES hotel(hotel_id)
    ON DELETE CASCADE,
    CONSTRAINT chk_room_price
    CHECK (price > 0),
    CONSTRAINT chk_room_capacity
    CHECK (room_capacity > 0),
    CONSTRAINT chk_room_view_type
    CHECK (room_view_type IN ('sea', 'mountain', 'city', 'garden', 'pool')),
    CONSTRAINT chk_room_damage_status
    CHECK (damage_status IN ('none', 'minor', 'major', 'out_of_service'))
    );

CREATE TABLE IF NOT EXISTS room_problems_or_damages (
    hotel_id INT NOT NULL,
    room_number INT NOT NULL,
    problems_or_damages VARCHAR(255) NOT NULL,
    PRIMARY KEY (hotel_id, room_number, problems_or_damages),
    CONSTRAINT fk_room_problem_room
    FOREIGN KEY (hotel_id, room_number)
    REFERENCES room(hotel_id, room_number)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS customer (
    driving_license_number VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    date_of_registration DATE NOT NULL
    );

CREATE TABLE IF NOT EXISTS customer_phone (
    driving_license_number VARCHAR(50) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    PRIMARY KEY (driving_license_number, phone_number),
    CONSTRAINT fk_customer_phone_customer
    FOREIGN KEY (driving_license_number)
    REFERENCES customer(driving_license_number)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS booking (
    booking_id SERIAL PRIMARY KEY,
    driving_license_number VARCHAR(50),
    hotel_id INT,
    room_number INT,
    start_day DATE NOT NULL,
    end_day DATE NOT NULL,
    archive_status BOOLEAN NOT NULL DEFAULT FALSE,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    customer_name_snapshot VARCHAR(255) NOT NULL,
    hotel_name_snapshot VARCHAR(150) NOT NULL,
    area_snapshot VARCHAR(100) NOT NULL,
    room_price_snapshot NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_customer
    FOREIGN KEY (driving_license_number)
    REFERENCES customer(driving_license_number)
    ON DELETE SET NULL,
    CONSTRAINT fk_booking_room
    FOREIGN KEY (hotel_id, room_number)
    REFERENCES room(hotel_id, room_number)
    ON DELETE SET NULL,
    CONSTRAINT chk_booking_dates
    CHECK (end_day > start_day),
    CONSTRAINT chk_booking_room_price_snapshot
    CHECK (room_price_snapshot > 0),
    CONSTRAINT chk_booking_active_references
    CHECK (
              archive_status = TRUE
              OR (driving_license_number IS NOT NULL AND hotel_id IS NOT NULL AND room_number IS NOT NULL)
    )
    );

CREATE TABLE IF NOT EXISTS renting (
    renting_id SERIAL PRIMARY KEY,
    ssn VARCHAR(20),
    hotel_id INT,
    room_number INT,
    booking_id INT UNIQUE,
    driving_license_number VARCHAR(50),
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    archive_status BOOLEAN NOT NULL DEFAULT FALSE,
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_on TIMESTAMP,
    customer_name_snapshot VARCHAR(255) NOT NULL,
    hotel_name_snapshot VARCHAR(150) NOT NULL,
    area_snapshot VARCHAR(100) NOT NULL,
    room_price_snapshot NUMERIC(10,2) NOT NULL,
    CONSTRAINT fk_renting_employee
    FOREIGN KEY (ssn)
    REFERENCES employee(ssn)
    ON DELETE SET NULL,
    CONSTRAINT fk_renting_room
    FOREIGN KEY (hotel_id, room_number)
    REFERENCES room(hotel_id, room_number)
    ON DELETE SET NULL,
    CONSTRAINT fk_renting_booking
    FOREIGN KEY (booking_id)
    REFERENCES booking(booking_id)
    ON DELETE SET NULL,
    CONSTRAINT fk_renting_customer
    FOREIGN KEY (driving_license_number)
    REFERENCES customer(driving_license_number)
    ON DELETE SET NULL,
    CONSTRAINT chk_renting_dates
    CHECK (end_datetime > start_datetime),
    CONSTRAINT chk_renting_paid_on
    CHECK (paid_on IS NULL OR is_paid = TRUE),
    CONSTRAINT chk_renting_room_price_snapshot
    CHECK (room_price_snapshot > 0),
    CONSTRAINT chk_renting_active_references
    CHECK (
              archive_status = TRUE
              OR (driving_license_number IS NOT NULL AND hotel_id IS NOT NULL AND room_number IS NOT NULL)
    )
    );

-- =========================================
-- VIEW 1: Available Rooms per Area
-- Description:
-- Shows the number of available (on the day that you search) rooms grouped by hotel area.
-- =========================================
CREATE OR REPLACE VIEW available_rooms_per_area AS
SELECT h.area,
       COUNT(*) AS available_rooms
FROM room r
         JOIN hotel h ON r.hotel_id = h.hotel_id
WHERE NOT EXISTS (
    SELECT 1
    FROM renting rt
    WHERE rt.hotel_id = r.hotel_id
      AND rt.room_number = r.room_number
      AND CURRENT_DATE BETWEEN rt.start_datetime AND rt.end_datetime
)
  AND NOT EXISTS (
    SELECT 1
    FROM booking b
    WHERE b.hotel_id = r.hotel_id
      AND b.room_number = r.room_number
      AND CURRENT_DATE BETWEEN b.start_day AND b.end_day
)
GROUP BY h.area;

-- =========================================
-- VIEW 2: Total Capacity per Hotel
-- Description:
-- Shows the total room capacity (sum of all room capacities) for each hotel.
-- =========================================
CREATE OR REPLACE VIEW total_capacity_per_hotel AS
SELECT h.hotel_id,
       h.hotel_name,
       SUM(r.room_capacity) AS total_capacity
FROM hotel h
         JOIN room r ON h.hotel_id = r.hotel_id
GROUP BY h.hotel_id, h.hotel_name;