-- =====================================================
-- TRIGGER TEST SCRIPT
-- Purpose:
-- This script demonstrates that the database correctly
-- enforces constraints preventing overlapping bookings
-- and rentings using triggers.
-- =====================================================


-- =====================================================
-- TEST 1: VALID BOOKING (SHOULD SUCCEED)
-- This inserts a booking for a room with no overlapping
-- reservations. Expected: SUCCESS
-- =====================================================
INSERT INTO booking (
    driving_license_number,
    hotel_id,
    room_number,
    start_day,
    end_day,
    archive_status,
    check_in_time,
    check_out_time,
    customer_name_snapshot,
    hotel_name_snapshot,
    area_snapshot,
    room_price_snapshot
)
VALUES (
           'DL00007ON',
           2,
           103,
           '2026-07-01',
           '2026-07-03',
           FALSE,
           NULL,
           NULL,
           'Isabella Martin',
           'Maple ByWard Hotel',
           'ByWard Market',
           230
       );



-- =====================================================
-- TEST 2: INVALID BOOKING (OVERLAPPING)
-- This attempts to insert a booking that overlaps with
-- TEST 1 for the same room.
-- Expected: FAILURE (trigger blocks insert)
-- =====================================================
INSERT INTO booking (
    driving_license_number,
    hotel_id,
    room_number,
    start_day,
    end_day,
    archive_status,
    check_in_time,
    check_out_time,
    customer_name_snapshot,
    hotel_name_snapshot,
    area_snapshot,
    room_price_snapshot
)
VALUES (
           'DL00008ON',
           2,
           103,
           '2026-07-02',
           '2026-07-04',
           FALSE,
           NULL,
           NULL,
           'Lucas Thompson',
           'Maple ByWard Hotel',
           'ByWard Market',
           230
       );



-- =====================================================
-- TEST 3: VALID RENTING (SHOULD SUCCEED)
-- This inserts a renting for a room with no conflicts.
-- Expected: SUCCESS
-- =====================================================
INSERT INTO renting (
    ssn,
    hotel_id,
    room_number,
    booking_id,
    driving_license_number,
    start_datetime,
    end_datetime,
    archive_status,
    is_paid,
    paid_on,
    customer_name_snapshot,
    hotel_name_snapshot,
    area_snapshot,
    room_price_snapshot
)
VALUES (
           '900000030',
           3,
           104,
           NULL,
           'DL00009ON',
           '2026-07-10 15:00:00',
           '2026-07-12 11:00:00',
           FALSE,
           FALSE,
           NULL,
           'Mia Moore',
           'Maple Kanata Hotel',
           'Kanata',
           260
       );



-- =====================================================
-- TEST 4: INVALID RENTING (OVERLAPPING)
-- This attempts to insert a renting that overlaps with
-- TEST 3 for the same room.
-- Expected: FAILURE (trigger blocks insert)
-- =====================================================
INSERT INTO renting (
    ssn,
    hotel_id,
    room_number,
    booking_id,
    driving_license_number,
    start_datetime,
    end_datetime,
    archive_status,
    is_paid,
    paid_on,
    customer_name_snapshot,
    hotel_name_snapshot,
    area_snapshot,
    room_price_snapshot
)
VALUES (
           '900000034',
           3,
           104,
           NULL,
           'DL00010ON',
           '2026-07-11 10:00:00',
           '2026-07-13 11:00:00',
           FALSE,
           FALSE,
           NULL,
           'Ethan Allen',
           'Maple Kanata Hotel',
           'Kanata',
           260
       );



-- =====================================================
-- TEST 5: INVALID RENTING (CONFLICT WITH BOOKING)
-- This attempts to rent a room during a time it is booked.
-- Expected: FAILURE (cross-table trigger check)
-- =====================================================
INSERT INTO renting (
    ssn,
    hotel_id,
    room_number,
    booking_id,
    driving_license_number,
    start_datetime,
    end_datetime,
    archive_status,
    is_paid,
    paid_on,
    customer_name_snapshot,
    hotel_name_snapshot,
    area_snapshot,
    room_price_snapshot
)
VALUES (
           '900000050',
           2,
           103,
           NULL,
           'DL00011ON',
           '2026-07-02 10:00:00',
           '2026-07-03 11:00:00',
           FALSE,
           FALSE,
           NULL,
           'Test Customer',
           'Maple ByWard Hotel',
           'ByWard Market',
           230
       );

-- =====================================================
-- TEST 6: INVALID BOOKING (DAMAGED ROOM)
-- This attempts to book a room that is marked as damaged.
-- Expected: FAILURE (trigger blocks insert and shows damage)
-- =====================================================
INSERT INTO booking (
    driving_license_number,
    hotel_id,
    room_number,
    start_day,
    end_day,
    archive_status,
    check_in_time,
    check_out_time,
    customer_name_snapshot,
    hotel_name_snapshot,
    area_snapshot,
    room_price_snapshot
)
VALUES (
           'DL00012ON',
           13,
           105,
           '2026-08-01',
           '2026-08-03',
           FALSE,
           NULL,
           NULL,
           'Benjamin Hill',
           'Maple ByWard Hotel',
           'ByWard Market',
           215
       );



-- =====================================================
-- TEST 7: INVALID RENTING (DAMAGED ROOM)
-- This attempts to rent a room that is marked as damaged.
-- Expected: FAILURE (trigger blocks insert and shows damage)
-- =====================================================
INSERT INTO renting (
    ssn,
    hotel_id,
    room_number,
    booking_id,
    driving_license_number,
    start_datetime,
    end_datetime,
    archive_status,
    is_paid,
    paid_on,
    customer_name_snapshot,
    hotel_name_snapshot,
    area_snapshot,
    room_price_snapshot
)
VALUES (
           '900000054',
           17,
           103,
           NULL,
           'DL00013ON',
           '2026-08-01 15:00:00',
           '2026-08-03 11:00:00',
           FALSE,
           FALSE,
           NULL,
           'Amelia Adams',
           'Maple ByWard Hotel',
           'ByWard Market',
           215
       );