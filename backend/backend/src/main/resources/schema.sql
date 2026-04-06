-- schema.sql
-- This script only creates the tables/constraints.
-- Data reset + inserts live in populate.sql.

--Create Hotel Chain Table
CREATE TABLE IF NOT EXISTS hotel_chain (
    central_office_id SERIAL PRIMARY KEY,
    chain_name VARCHAR(100) NOT NULL UNIQUE,
    street_name VARCHAR(100) NOT NULL,
    building_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL
    );

--Create Hotel Chain Email Table
CREATE TABLE IF NOT EXISTS hotel_chain_email (
    central_office_id INT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    PRIMARY KEY (central_office_id, email_address),
    CONSTRAINT fk_hotel_chain_email
        FOREIGN KEY (central_office_id)
        REFERENCES hotel_chain(central_office_id)
        ON DELETE CASCADE,

    -- Constraint to ensure that email address must contain @
    CONSTRAINT chk_hotel_chain_email_format
        CHECK (email_address LIKE '%@%')
    );


-- Create Hotel Chain Phone Number Table
CREATE TABLE IF NOT EXISTS hotel_chain_phone (
    central_office_id INT NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    PRIMARY KEY (central_office_id, phone_number),
    CONSTRAINT fk_hotel_chain_phone
        FOREIGN KEY (central_office_id)
        REFERENCES hotel_chain(central_office_id)
        ON DELETE CASCADE,

    -- Constraint to ensure that phone number length is between 7 and 20 numbers
    CONSTRAINT chk_hotel_chain_phone_length
        CHECK (char_length(phone_number) BETWEEN 7 AND 20)
    );

-- Create Employee Table
CREATE TABLE IF NOT EXISTS employee (
    ssn VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,

    -- Constraint ensures ssn must be 9 digits (numeric)
    Constraint chk_ssn_format CHECK (ssn ~ '^[0-9]{9}$'),
    -- Constraint ensures employee_street_number > 0
    CONSTRAINT chk_employee_street_num CHECK (street_number ~ '^[1-9][0-9]*$')
    );

-- Create Manager Table
CREATE TABLE IF NOT EXISTS manager (
    ssn VARCHAR(20) PRIMARY KEY,

    -- Ensures that a manager must already exist as an employee
    CONSTRAINT fk_manager_employee
    FOREIGN KEY (ssn)
    REFERENCES employee(ssn)
    -- If the referenced employee is deleted, delete the corresponding manager record automatically
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS role (
    -- role_name stores the name of a role (e.g., 'Manager', 'Developer')
    role_name VARCHAR(100) PRIMARY KEY
    );

-- Create Hotel Table
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
    --Ensures that each hotel must belong to an existing hotel chain
    CONSTRAINT fk_hotel_chain
        FOREIGN KEY (central_office_id)
        REFERENCES hotel_chain(central_office_id)
        ON DELETE CASCADE,
    --Ensures that each hotel must have a valid manager
    CONSTRAINT fk_hotel_manager
        FOREIGN KEY (manager_ssn)
        REFERENCES manager(ssn)
        ON DELETE RESTRICT,
    -- Constraint ensures that number of hotel rooms is positive and non zero
    CONSTRAINT chk_hotel_number_of_rooms
        CHECK (number_of_rooms > 0),
    CONSTRAINT chk_hotel_rating
        CHECK (rating BETWEEN 1 AND 5),
    -- Constraint ensures employee_street_number > 0
    CONSTRAINT chk_hotel_street_num
        CHECK (street_number ~ '^[1-9][0-9]*$'),
    -- Constraint ensures hotel address must be unique
    CONSTRAINT uq_hotel_address
        UNIQUE (street_name, street_number, postal_code)
    );

--Create hotel email table
CREATE TABLE IF NOT EXISTS hotel_email (
    hotel_id INT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    PRIMARY KEY (hotel_id, email_address),
    --Each email must belong to a valid hotel
    CONSTRAINT fk_hotel_email
        FOREIGN KEY (hotel_id)
        REFERENCES hotel(hotel_id)
        ON DELETE CASCADE,
    --Makes sure that email address contains @
    CONSTRAINT chk_hotel_email_format
        CHECK (email_address LIKE '%@%')
    );

--Hotel Phone number table
CREATE TABLE IF NOT EXISTS hotel_phone (
    hotel_id INT NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    PRIMARY KEY (hotel_id, phone_number),
    --Phone number must be associated with a valid hotel
    CONSTRAINT fk_hotel_phone
        FOREIGN KEY (hotel_id)
        REFERENCES hotel(hotel_id)
        ON DELETE CASCADE,
    -- Phone number length must be between 7 and 20 nums
    CONSTRAINT chk_hotel_phone_length
        CHECK (char_length(phone_number) BETWEEN 7 AND 20)
    );


-- Table linking employees to roles in hotels
CREATE TABLE IF NOT EXISTS works_as (
    ssn VARCHAR(20) NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    hotel_id INT NOT NULL,
    PRIMARY KEY (ssn, role_name, hotel_id),

    -- Employee must exist
    CONSTRAINT fk_works_as_employee
    FOREIGN KEY (ssn)
    REFERENCES employee(ssn)
    ON DELETE CASCADE,

    -- Role must exist
    CONSTRAINT fk_works_as_role
    FOREIGN KEY (role_name)
    REFERENCES role(role_name)
    ON DELETE RESTRICT,

    -- Hotel must exist
    CONSTRAINT fk_works_as_hotel
    FOREIGN KEY (hotel_id)
    REFERENCES hotel(hotel_id)
    ON DELETE CASCADE
    );

-- Table for rooms in hotels
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

    -- Hotel must exist
    CONSTRAINT fk_room_hotel
    FOREIGN KEY (hotel_id)
    REFERENCES hotel(hotel_id)
    ON DELETE CASCADE,

    -- Price must be positive
    CONSTRAINT chk_room_price CHECK (price > 0),

    -- Capacity must be positive
    CONSTRAINT chk_room_capacity CHECK (room_capacity > 0),

    -- Room view must be valid
    CONSTRAINT chk_room_view_type
    CHECK (room_view_type IN ('sea', 'mountain', 'city', 'garden', 'pool')),

    -- Damage status must be valid
    CONSTRAINT chk_room_damage_status
    CHECK (damage_status IN ('none', 'minor', 'major', 'out_of_service'))
    );

-- Table for room problems or damages
CREATE TABLE IF NOT EXISTS room_problems_or_damages (
    hotel_id INT NOT NULL,
    room_number INT NOT NULL,
    problems_or_damages VARCHAR(255) NOT NULL,
    PRIMARY KEY (hotel_id, room_number, problems_or_damages),

    -- Room must exist
    CONSTRAINT fk_room_problem_room
    FOREIGN KEY (hotel_id, room_number)
    REFERENCES room(hotel_id, room_number)
    ON DELETE CASCADE
    );

-- Table for customers
CREATE TABLE IF NOT EXISTS customer (
    driving_license_number VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    date_of_registration DATE NOT NULL,

    -- Street number must be positive
    CONSTRAINT chk_customer_street_num CHECK (street_number ~ '^[1-9][0-9]*$')
    );

--Customer phone table
CREATE TABLE IF NOT EXISTS customer_phone (
    driving_license_number VARCHAR(50) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    PRIMARY KEY (driving_license_number, phone_number),
    -- Phone number must be linked to customer
    CONSTRAINT fk_customer_phone_customer
        FOREIGN KEY (driving_license_number)
        REFERENCES customer(driving_license_number)
        ON DELETE CASCADE,
    -- Length restriction on phone number
    CONSTRAINT chk_customer_phone_length
        CHECK (char_length(phone_number) BETWEEN 7 AND 20)
    );

-- Booking table
CREATE TABLE IF NOT EXISTS booking (
    booking_id SERIAL PRIMARY KEY,
    driving_license_number VARCHAR(50) NOT NULL,
    hotel_id INT NOT NULL,
    room_number INT NOT NULL,
    start_day DATE NOT NULL,
    end_day DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    customer_name_snapshot VARCHAR(255) NOT NULL,
    hotel_name_snapshot VARCHAR(150) NOT NULL,
    area_snapshot VARCHAR(100) NOT NULL,
    room_price_snapshot NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Customer must exist
    CONSTRAINT fk_booking_customer
    FOREIGN KEY (driving_license_number)
    REFERENCES customer(driving_license_number)
    ON DELETE RESTRICT,

    -- Room must exist
    CONSTRAINT fk_booking_room
    FOREIGN KEY (hotel_id, room_number)
    REFERENCES room(hotel_id, room_number)
    ON DELETE RESTRICT,

    -- End date must be after start date
    CONSTRAINT chk_booking_dates
    CHECK (end_day > start_day),

    -- Price must be positive
    CONSTRAINT chk_booking_room_price_snapshot
    CHECK (room_price_snapshot > 0)
    );

-- Booking archive table
CREATE TABLE IF NOT EXISTS booking_archive (
    booking_id INT PRIMARY KEY,
    driving_license_number VARCHAR(50) NOT NULL,
    hotel_id INT NOT NULL,
    room_number INT NOT NULL,
    start_day DATE NOT NULL,
    end_day DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    customer_name_snapshot VARCHAR(255) NOT NULL,
    hotel_name_snapshot VARCHAR(150) NOT NULL,
    area_snapshot VARCHAR(100) NOT NULL,
    room_price_snapshot NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- End date must be after start date
    CONSTRAINT chk_booking_archive_dates
    CHECK (end_day > start_day),

    -- Price must be positive
    CONSTRAINT chk_booking_archive_room_price_snapshot
    CHECK (room_price_snapshot > 0)
    );

-- Renting table
CREATE TABLE IF NOT EXISTS renting (
    renting_id SERIAL PRIMARY KEY,
    ssn VARCHAR(20) NOT NULL,
    hotel_id INT NOT NULL,
    room_number INT NOT NULL,
    booking_id INT UNIQUE,
    driving_license_number VARCHAR(50) NOT NULL,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    actual_check_in_time TIMESTAMP,
    actual_check_out_time TIMESTAMP,
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_on TIMESTAMP,
    customer_name_snapshot VARCHAR(255) NOT NULL,
    hotel_name_snapshot VARCHAR(150) NOT NULL,
    area_snapshot VARCHAR(100) NOT NULL,
    room_price_snapshot NUMERIC(10,2) NOT NULL,

    -- Employee must exist
    CONSTRAINT fk_renting_employee
    FOREIGN KEY (ssn)
    REFERENCES employee(ssn)
    ON DELETE RESTRICT,

    -- Room must exist
    CONSTRAINT fk_renting_room
    FOREIGN KEY (hotel_id, room_number)
    REFERENCES room(hotel_id, room_number)
    ON DELETE RESTRICT,

    -- Booking must exist
    CONSTRAINT fk_renting_booking
    FOREIGN KEY (booking_id)
    REFERENCES booking(booking_id)
    ON DELETE RESTRICT,

    -- Customer must exist
    CONSTRAINT fk_renting_customer
    FOREIGN KEY (driving_license_number)
    REFERENCES customer(driving_license_number)
    ON DELETE RESTRICT,

    -- End time must be after start time
    CONSTRAINT chk_renting_dates
    CHECK (end_datetime > start_datetime),

    -- Paid date only if paid
    CONSTRAINT chk_renting_paid_on
    CHECK (paid_on IS NULL OR is_paid = TRUE),

    -- Price must be positive
    CONSTRAINT chk_renting_room_price_snapshot
    CHECK (room_price_snapshot > 0)
    );

-- Renting archive table
CREATE TABLE IF NOT EXISTS renting_archive (
    renting_id INT PRIMARY KEY,
    ssn VARCHAR(20) NOT NULL,
    hotel_id INT NOT NULL,
    room_number INT NOT NULL,
    booking_id INT NOT NULL,
    driving_license_number VARCHAR(50) NOT NULL,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    actual_check_in_time TIMESTAMP,
    actual_check_out_time TIMESTAMP,
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_on TIMESTAMP,
    customer_name_snapshot VARCHAR(255) NOT NULL,
    hotel_name_snapshot VARCHAR(150) NOT NULL,
    area_snapshot VARCHAR(100) NOT NULL,
    room_price_snapshot NUMERIC(10,2) NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- End time must be after start time
    CONSTRAINT chk_renting_archive_dates
    CHECK (end_datetime > start_datetime),

    -- Paid date only if paid
    CONSTRAINT chk_renting_archive_paid_on
    CHECK (paid_on IS NULL OR is_paid = TRUE),

    -- Price must be positive
    CONSTRAINT chk_renting_archive_room_price_snapshot
    CHECK (room_price_snapshot > 0)
    );

-- =========================================
-- VIEW 1: Available Rooms per Area
-- Description:
-- Shows the number of available (on the day that you search) rooms grouped by hotel area.
-- =========================================
CREATE OR REPLACE VIEW available_rooms_per_area_base AS
SELECT
    r.hotel_id,
    r.room_number,
    h.area
FROM room r
         JOIN hotel h
              ON r.hotel_id = h.hotel_id
WHERE r.damage_status = 'none';

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

-- =========================================
-- INDEX 1: room(hotel_id)
-- Expected queries/data updates:
-- This database frequently searches for all rooms belonging to a specific hotel,
-- especially when joining the room table with the hotel table or when displaying
-- rooms for a selected hotel in the UI.
-- Why this index is useful:
-- This index accelerates queries that filter or join on hotel_id by allowing the
-- DBMS to find matching rooms more quickly instead of scanning the entire room table.
-- =========================================
CREATE INDEX IF NOT EXISTS idx_room_hotel_id
    ON room(hotel_id);

-- =========================================
-- INDEX 2: hotel(area)
-- Expected queries/data updates:
-- This database is expected to support searches by geographic area, such as finding
-- hotels in Downtown Ottawa, Gatineau Core, or other regions. It is also useful for
-- the view that groups room availability by area.
-- Why this index is useful:
-- This index accelerates queries that filter hotels by area and helps grouping/querying
-- by area perform faster, especially when the hotel table grows larger.
-- =========================================
CREATE INDEX IF NOT EXISTS idx_hotel_area
    ON hotel(area);

-- =========================================
-- INDEX 3: booking(hotel_id, room_number, start_day, end_day)
-- Expected queries/data updates:
-- This database will frequently check whether a specific room is already booked during
-- a given date range when customers search for available rooms or create a booking.
-- New bookings will also be inserted regularly.
-- Why this index is useful:
-- This composite index accelerates availability queries by helping the DBMS quickly
-- locate bookings for a particular room and compare the requested dates against existing
-- booking intervals, rather than scanning the entire booking table.
-- =========================================
CREATE INDEX IF NOT EXISTS idx_booking_room_dates
    ON booking(hotel_id, room_number, start_day, end_day);

-- =========================================
-- INDEX 4: renting(hotel_id, room_number, start_datetime, end_datetime)
-- Expected queries/data updates:
-- This database will frequently check whether a room is currently being rented during
-- a given time period, especially when converting bookings into rentings and when
-- determining room availability in the application.
-- Why this index is useful:
-- This composite index accelerates queries that search for active or overlapping rentings
-- for a specific room by narrowing the search using hotel_id, room_number, and the rental
-- time interval.
-- =========================================
CREATE INDEX IF NOT EXISTS idx_renting_room_dates
    ON renting(hotel_id, room_number, start_datetime, end_datetime);

-- =========================================
-- INDEX 5: hotel_chain(chain_name)
-- Expected queries/data updates:
-- This database supports searches and filters by hotel chain name, such as finding all
-- hotels or all managers belonging to a given chain. Chain names are also used in joins
-- and reporting queries.
-- Why this index is useful:
-- This index accelerates queries that search or filter by chain_name, allowing faster
-- retrieval of the corresponding hotel chain rows.
-- =========================================
CREATE INDEX IF NOT EXISTS idx_hotel_chain_name
    ON hotel_chain(chain_name);

-- =========================================
-- TRIGGER FUNCTION 1:
-- Prevent overlapping bookings for the same room
-- Also ensures bookings do not overlap with existing rentings
-- and prevents booking damaged rooms
-- =========================================
CREATE OR REPLACE FUNCTION prevent_overlapping_bookings()
RETURNS TRIGGER
AS '
BEGIN
    IF EXISTS (
        SELECT 1
        FROM booking b
        WHERE b.hotel_id = NEW.hotel_id
          AND b.room_number = NEW.room_number
          AND b.booking_id <> COALESCE(NEW.booking_id, -1)
          AND NEW.start_day < b.end_day
          AND NEW.end_day > b.start_day
    ) THEN
        RAISE EXCEPTION ''This room is already booked during the selected dates.'';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM renting r
        WHERE r.hotel_id = NEW.hotel_id
          AND r.room_number = NEW.room_number
          AND NEW.start_day < r.end_datetime::date
          AND NEW.end_day > r.start_datetime::date
    ) THEN
        RAISE EXCEPTION ''This room is currently rented during the selected dates.'';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM room rm
        WHERE rm.hotel_id = NEW.hotel_id
          AND rm.room_number = NEW.room_number
          AND rm.damage_status <> ''none''
    ) THEN
        RAISE EXCEPTION ''This room cannot be booked because it is marked as damaged.'';
    END IF;

    RETURN NEW;
END;
'
LANGUAGE plpgsql;

-- =========================================
-- TRIGGER 1:
-- Fires before inserting or updating a booking
-- =========================================
DROP TRIGGER IF EXISTS trg_prevent_overlapping_bookings ON booking;

CREATE TRIGGER trg_prevent_overlapping_bookings
    BEFORE INSERT OR UPDATE ON booking
                         FOR EACH ROW
                         EXECUTE FUNCTION prevent_overlapping_bookings();

-- =========================================
-- TRIGGER FUNCTION 2:
-- Prevent overlapping rentings for the same room
-- Also ensures rentings do not overlap with existing bookings
-- and prevents renting damaged rooms
-- =========================================
CREATE OR REPLACE FUNCTION prevent_overlapping_rentings()
RETURNS TRIGGER
AS '
BEGIN
    IF EXISTS (
        SELECT 1
        FROM renting r
        WHERE r.hotel_id = NEW.hotel_id
          AND r.room_number = NEW.room_number
          AND r.renting_id <> COALESCE(NEW.renting_id, -1)
          AND NEW.start_datetime < r.end_datetime
          AND NEW.end_datetime > r.start_datetime
    ) THEN
        RAISE EXCEPTION ''This room is already rented during the selected time period.'';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM booking b
        WHERE b.hotel_id = NEW.hotel_id
          AND b.room_number = NEW.room_number
          AND b.booking_id <> COALESCE(NEW.booking_id, -1)
          AND NEW.start_datetime::date < b.end_day
          AND NEW.end_datetime::date > b.start_day
    ) THEN
        RAISE EXCEPTION ''This room is already booked during the selected time period.'';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM room rm
        WHERE rm.hotel_id = NEW.hotel_id
          AND rm.room_number = NEW.room_number
          AND rm.damage_status <> ''none''
    ) THEN
        RAISE EXCEPTION ''This room cannot be rented because it is marked as damaged.'';
    END IF;

    RETURN NEW;
END;
'
LANGUAGE plpgsql;

-- =========================================
-- TRIGGER 2:
-- Fires before inserting or updating a renting
-- =========================================
DROP TRIGGER IF EXISTS trg_prevent_overlapping_rentings ON renting;

CREATE TRIGGER trg_prevent_overlapping_rentings
    BEFORE INSERT OR UPDATE ON renting
                         FOR EACH ROW
                         EXECUTE FUNCTION prevent_overlapping_rentings();

-- =========================================
-- TRIGGER FUNCTION 3:
-- Ensure a hotel always has at least one contact method
-- =========================================
CREATE OR REPLACE FUNCTION check_hotel_has_contact_method()
RETURNS TRIGGER
AS '
DECLARE
    target_hotel_id INT;
BEGIN
    IF TG_TABLE_NAME = ''hotel_email'' OR TG_TABLE_NAME = ''hotel_phone'' THEN
        target_hotel_id := COALESCE(NEW.hotel_id, OLD.hotel_id);

        IF NOT EXISTS (
            SELECT 1
            FROM hotel_email he
            WHERE he.hotel_id = target_hotel_id
        )
        AND NOT EXISTS (
            SELECT 1
            FROM hotel_phone hp
            WHERE hp.hotel_id = target_hotel_id
        ) THEN
            RAISE EXCEPTION ''Hotel must have at least one contact method.'';
        END IF;
    END IF;

    RETURN COALESCE(NEW, OLD);
END;
'
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_check_hotel_contact_email ON hotel_email;
DROP TRIGGER IF EXISTS trg_check_hotel_contact_phone ON hotel_phone;

CREATE TRIGGER trg_check_hotel_contact_email
    AFTER INSERT OR DELETE OR UPDATE ON hotel_email
FOR EACH ROW
EXECUTE FUNCTION check_hotel_has_contact_method();

CREATE TRIGGER trg_check_hotel_contact_phone
    AFTER INSERT OR DELETE OR UPDATE ON hotel_phone
FOR EACH ROW
EXECUTE FUNCTION check_hotel_has_contact_method();