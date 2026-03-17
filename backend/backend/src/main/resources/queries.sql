-- =========================================
-- Hotel Database - Query Examples (Requirement 2c)
--
-- Description:
-- This file contains 4 queries:
-- - 2 regular queries
-- - 1 aggregation query
-- - 1 nested query
--
-- =========================================



-- =========================================
-- QUERY 1: Sea View Rooms in a Hotel Chain
-- Type: Regular Query
-- Description:
-- Returns all rooms with a sea view belonging to a specific hotel chain.
-- =========================================
SELECT hc.chain_name,
       h.hotel_name,
       r.hotel_id,
       r.room_number,
       r.price,
       r.room_capacity,
       r.room_view_type
FROM room r
         JOIN hotel h ON r.hotel_id = h.hotel_id
         JOIN hotel_chain hc ON h.central_office_id = hc.central_office_id
WHERE r.room_view_type = 'sea'
  AND hc.chain_name = 'Maple Leaf Hotels'
ORDER BY h.hotel_name, r.room_number;



-- =========================================
-- QUERY 2: Managers in a Hotel Chain
-- Type: Regular Query
-- Description:
-- Returns all employees with the role "Manager" working in a specific hotel chain,
-- along with the hotel they are assigned to.
-- =========================================
SELECT e.ssn,
       e.first_name,
       e.last_name,
       h.hotel_name,
       hc.chain_name
FROM employee e
         JOIN works_as w ON e.ssn = w.ssn
         JOIN role r ON w.role_name = r.role_name
         JOIN hotel h ON w.hotel_id = h.hotel_id
         JOIN hotel_chain hc ON h.central_office_id = hc.central_office_id
WHERE LOWER(r.role_name) = 'manager'
  AND hc.chain_name = 'Aurora Hospitality'
ORDER BY h.hotel_name, e.last_name, e.first_name;



-- =========================================
-- QUERY 3: Number of Rooms per Hotel
-- Type: Aggregation Query
-- Description:
-- Counts the total number of rooms available in each hotel.
-- =========================================
SELECT h.hotel_id,
       h.hotel_name,
       hc.chain_name,
       COUNT(r.room_number) AS total_rooms
FROM hotel h
         JOIN hotel_chain hc ON h.central_office_id = hc.central_office_id
         LEFT JOIN room r ON h.hotel_id = r.hotel_id
GROUP BY h.hotel_id, h.hotel_name, hc.chain_name
ORDER BY hc.chain_name, h.hotel_name;



-- =========================================
-- QUERY 4: Customers Who Booked the Most Expensive Room
-- Type: Nested Query
-- Description:
-- Returns all customers who booked a room whose price is equal to the maximum
-- room price in the entire database.
-- =========================================
SELECT c.driving_license_number,
       c.first_name,
       c.last_name,
       b.booking_id,
       b.hotel_id,
       b.room_number
FROM customer c
         JOIN booking b ON c.driving_license_number = b.driving_license_number
         JOIN room r ON b.hotel_id = r.hotel_id
    AND b.room_number = r.room_number
WHERE r.price = (
    SELECT MAX(price)
    FROM room
)
ORDER BY c.last_name, c.first_name;