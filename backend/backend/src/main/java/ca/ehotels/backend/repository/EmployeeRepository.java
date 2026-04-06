package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EmployeeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EmployeeRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EmployeeInfoDto getEmployeeInfo(String ssn) {
        String sql = """
            SELECT
                e.ssn,
                e.first_name || ' ' || e.last_name AS employee_name,
                h.hotel_id,
                h.hotel_name,
                h.area,
                hc.chain_name
            FROM employee e
            JOIN works_as w
              ON e.ssn = w.ssn
            JOIN hotel h
              ON w.hotel_id = h.hotel_id
            JOIN hotel_chain hc
              ON h.central_office_id = hc.central_office_id
            WHERE e.ssn = :ssn
            LIMIT 1
            """;

        List<EmployeeInfoDto> results = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("ssn", ssn),
                (rs, rowNum) -> {
                    EmployeeInfoDto dto = new EmployeeInfoDto();
                    dto.setSsn(rs.getString("ssn"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setHotelId(rs.getInt("hotel_id"));
                    dto.setHotelName(rs.getString("hotel_name"));
                    dto.setArea(rs.getString("area"));
                    dto.setChainName(rs.getString("chain_name"));
                    return dto;
                }
        );

        return results.isEmpty() ? null : results.get(0);
    }

    public List<AvailableRoomDto> getAvailableRoomsForEmployeeHotel(String ssn, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT
                h.hotel_id,
                h.hotel_name,
                hc.chain_name,
                h.area,
                h.rating,
                h.number_of_rooms AS total_rooms,
                h.street_name,
                h.street_number,
                h.postal_code,
                h.city,
                h.province_state,
                h.country,
                r.room_number,
                r.price,
                r.room_capacity,
                r.room_view_type,
                r.damage_status,
                r.has_tv,
                r.has_air_conditioner,
                r.has_fridge,
                r.room_extended_status
            FROM room r
            JOIN hotel h
              ON r.hotel_id = h.hotel_id
            JOIN hotel_chain hc
              ON h.central_office_id = hc.central_office_id
            JOIN works_as w
              ON w.hotel_id = h.hotel_id
            WHERE w.ssn = :ssn
              AND r.damage_status = 'none'
              AND NOT EXISTS (
                    SELECT 1
                    FROM booking b
                    WHERE b.hotel_id = r.hotel_id
                      AND b.room_number = r.room_number
                      AND b.start_day < :endDate
                      AND b.end_day > :startDate
              )
              AND NOT EXISTS (
                    SELECT 1
                    FROM renting rt
                    WHERE rt.hotel_id = r.hotel_id
                      AND rt.room_number = r.room_number
                      AND rt.start_datetime::date < :endDate
                      AND rt.end_datetime::date > :startDate
              )
            ORDER BY h.hotel_name, r.price, r.room_number
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ssn", ssn)
                .addValue("startDate", startDate)
                .addValue("endDate", endDate);

        return jdbcTemplate.query(sql, params, new AvailableRoomRowMapper());
    }

    public int createDirectRenting(CreateRentingRequest request) {
        String sql = """
            INSERT INTO renting (
                ssn,
                hotel_id,
                room_number,
                booking_id,
                driving_license_number,
                start_datetime,
                end_datetime,
                customer_name_snapshot,
                hotel_name_snapshot,
                area_snapshot,
                room_price_snapshot
            )
            SELECT
                :ssn,
                r.hotel_id,
                r.room_number,
                NULL,
                c.driving_license_number,
                :startDatetime,
                :endDatetime,
                c.first_name || ' ' || c.last_name,
                h.hotel_name,
                h.area,
                r.price
            FROM customer c
            JOIN room r
              ON r.hotel_id = :hotelId
             AND r.room_number = :roomNumber
            JOIN hotel h
              ON h.hotel_id = r.hotel_id
            JOIN works_as w
              ON w.hotel_id = h.hotel_id
             AND w.ssn = :ssn
            WHERE c.driving_license_number = :drivingLicenseNumber
            """;

        LocalDateTime startDateTime = request.getStartDate().atTime(15, 0);
        LocalDateTime endDateTime = request.getEndDate().atTime(11, 0);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ssn", request.getSsn())
                .addValue("drivingLicenseNumber", request.getDrivingLicenseNumber())
                .addValue("hotelId", request.getHotelId())
                .addValue("roomNumber", request.getRoomNumber())
                .addValue("startDatetime", Timestamp.valueOf(startDateTime))
                .addValue("endDatetime", Timestamp.valueOf(endDateTime));

        return jdbcTemplate.update(sql, params);
    }

    public List<BookingDto> getActiveBookingsForEmployeeHotel(String ssn) {
        String sql = """
            SELECT
                b.booking_id,
                b.driving_license_number,
                b.hotel_id,
                b.room_number,
                b.start_day,
                b.end_day,
                b.customer_name_snapshot,
                b.hotel_name_snapshot,
                b.area_snapshot,
                b.room_price_snapshot
            FROM booking b
            JOIN works_as w
              ON w.hotel_id = b.hotel_id
            WHERE w.ssn = :ssn
            ORDER BY b.start_day, b.booking_id
            """;

        return jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("ssn", ssn),
                (rs, rowNum) -> {
                    BookingDto dto = new BookingDto();
                    dto.setBookingId(rs.getInt("booking_id"));
                    dto.setDrivingLicenseNumber(rs.getString("driving_license_number"));
                    dto.setHotelId(rs.getInt("hotel_id"));
                    dto.setRoomNumber(rs.getInt("room_number"));
                    dto.setStartDay(rs.getDate("start_day").toLocalDate());
                    dto.setEndDay(rs.getDate("end_day").toLocalDate());
                    dto.setCustomerNameSnapshot(rs.getString("customer_name_snapshot"));
                    dto.setHotelNameSnapshot(rs.getString("hotel_name_snapshot"));
                    dto.setAreaSnapshot(rs.getString("area_snapshot"));
                    dto.setRoomPriceSnapshot(rs.getBigDecimal("room_price_snapshot"));
                    return dto;
                }
        );
    }

    @Transactional
    public int convertBookingToRenting(ConvertBookingRequest request) {
        String archiveBookingSql = """
            UPDATE booking
            SET check_in_time = CURRENT_TIMESTAMP
            WHERE booking_id = :bookingId
            """;

        String insertRentingSql = """
            INSERT INTO renting (
                ssn,
                hotel_id,
                room_number,
                booking_id,
                driving_license_number,
                start_datetime,
                end_datetime,
                actual_check_in_time,
                is_paid,
                paid_on,
                customer_name_snapshot,
                hotel_name_snapshot,
                area_snapshot,
                room_price_snapshot
            )
            SELECT
                :ssn,
                b.hotel_id,
                b.room_number,
                b.booking_id,
                b.driving_license_number,
                b.start_day::timestamp + INTERVAL '15 hours',
                b.end_day::timestamp + INTERVAL '11 hours',
                CURRENT_TIMESTAMP,
                TRUE,
                CURRENT_TIMESTAMP,
                b.customer_name_snapshot,
                b.hotel_name_snapshot,
                b.area_snapshot,
                b.room_price_snapshot
            FROM booking b
            JOIN works_as w
              ON w.hotel_id = b.hotel_id
            WHERE b.booking_id = :bookingId
              AND w.ssn = :ssn
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ssn", request.getSsn())
                .addValue("bookingId", request.getBookingId());

        int archived = jdbcTemplate.update(archiveBookingSql, params);

        if (archived == 0) {
            return 0;
        }

        int inserted = jdbcTemplate.update(insertRentingSql, params);

        if (inserted == 0) {
            throw new RuntimeException("Could not create renting from booking.");
        }

        return inserted;
    }

    public List<RentingDto> getActiveRentingsForEmployeeHotel(String ssn) {
        String sql = """
            SELECT
                r.renting_id,
                r.ssn,
                r.hotel_id,
                r.room_number,
                r.booking_id,
                r.driving_license_number,
                r.start_datetime,
                r.end_datetime,
                r.is_paid,
                r.customer_name_snapshot,
                r.hotel_name_snapshot,
                r.area_snapshot,
                r.room_price_snapshot
            FROM renting r
            JOIN works_as w
              ON w.hotel_id = r.hotel_id
            WHERE w.ssn = :ssn
            ORDER BY r.start_datetime, r.renting_id
            """;

        return jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("ssn", ssn),
                (rs, rowNum) -> {
                    RentingDto dto = new RentingDto();
                    dto.setRentingId(rs.getInt("renting_id"));
                    dto.setSsn(rs.getString("ssn"));
                    dto.setHotelId(rs.getInt("hotel_id"));
                    dto.setRoomNumber(rs.getInt("room_number"));
                    dto.setBookingId((Integer) rs.getObject("booking_id"));
                    dto.setDrivingLicenseNumber(rs.getString("driving_license_number"));
                    dto.setStartDatetime(rs.getTimestamp("start_datetime").toLocalDateTime());
                    dto.setEndDatetime(rs.getTimestamp("end_datetime").toLocalDateTime());
                    dto.setIsPaid(rs.getBoolean("is_paid"));
                    dto.setCustomerNameSnapshot(rs.getString("customer_name_snapshot"));
                    dto.setHotelNameSnapshot(rs.getString("hotel_name_snapshot"));
                    dto.setAreaSnapshot(rs.getString("area_snapshot"));
                    dto.setRoomPriceSnapshot(rs.getBigDecimal("room_price_snapshot"));
                    return dto;
                }
        );
    }

    public int checkoutRenting(CheckoutRentingRequest request) {
        String sql = """
            UPDATE renting
            SET actual_check_out_time = CURRENT_TIMESTAMP
            WHERE renting_id = :rentingId
              AND hotel_id IN (
                  SELECT hotel_id
                  FROM works_as
                  WHERE ssn = :ssn
              )
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ssn", request.getSsn())
                .addValue("rentingId", request.getRentingId());

        return jdbcTemplate.update(sql, params);
    }

    public EmployeeDto getEmployee(String ssn) {
        String sql = """
            SELECT *
            FROM employee
            WHERE ssn = :ssn
            """;

        return jdbcTemplate.queryForObject(
                sql,
                new MapSqlParameterSource("ssn", ssn),
                (rs, rowNum) -> {
                    EmployeeDto e = new EmployeeDto();
                    e.setSsn(rs.getString("ssn"));
                    e.setFirstName(rs.getString("first_name"));
                    e.setMiddleName(rs.getString("middle_name"));
                    e.setLastName(rs.getString("last_name"));
                    e.setStreetName(rs.getString("street_name"));
                    e.setStreetNumber(rs.getString("street_number"));
                    e.setPostalCode(rs.getString("postal_code"));
                    return e;
                }
        );
    }

    public int updateRoom(RoomDto room, String ssn) {
        String updateRoomSql = """
        UPDATE room
        SET price = :price,
            room_capacity = :capacity,
            room_view_type = :view,
            damage_status = :damage,
            has_tv = :tv,
            has_air_conditioner = :ac,
            has_fridge = :fridge,
            room_extended_status = :extended
        WHERE hotel_id = :hotelId
          AND room_number = :roomNumber
          AND hotel_id IN (
              SELECT hotel_id FROM works_as WHERE ssn = :ssn
          )
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("price", room.getPrice())
                .addValue("capacity", room.getRoomCapacity())
                .addValue("view", room.getRoomViewType())
                .addValue("damage", room.getDamageStatus())
                .addValue("tv", room.getHasTv())
                .addValue("ac", room.getHasAirConditioner())
                .addValue("fridge", room.getHasFridge())
                .addValue("extended", room.getRoomExtendedStatus())
                .addValue("hotelId", room.getHotelId())
                .addValue("roomNumber", room.getRoomNumber())
                .addValue("ssn", ssn);

        int rowsUpdated = jdbcTemplate.update(updateRoomSql, params);

        if (rowsUpdated == 0) {
            return 0;
        }

        if (room.getProblemDescription() != null && !room.getProblemDescription().trim().isEmpty()) {
            String insertProblemSql = """
            INSERT INTO room_problems_or_damages (
                hotel_id,
                room_number,
                problems_or_damages
            )
            VALUES (
                :hotelId,
                :roomNumber,
                :problemDescription
            )
            ON CONFLICT DO NOTHING
        """;

            MapSqlParameterSource problemParams = new MapSqlParameterSource()
                    .addValue("hotelId", room.getHotelId())
                    .addValue("roomNumber", room.getRoomNumber())
                    .addValue("problemDescription", room.getProblemDescription().trim());

            jdbcTemplate.update(insertProblemSql, problemParams);
        }

        return rowsUpdated;
    }

    public int updateEmployee(EmployeeDto request) {
        String sql = """
            UPDATE employee
            SET first_name = :firstName,
                middle_name = :middleName,
                last_name = :lastName,
                street_name = :streetName,
                street_number = :streetNumber,
                postal_code = :postalCode
            WHERE ssn = :ssn
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ssn", request.getSsn())
                .addValue("firstName", request.getFirstName())
                .addValue("middleName", request.getMiddleName())
                .addValue("lastName", request.getLastName())
                .addValue("streetName", request.getStreetName())
                .addValue("streetNumber", request.getStreetNumber())
                .addValue("postalCode", request.getPostalCode());

        return jdbcTemplate.update(sql, params);
    }
    public List<RoomDto> getRoomsForEmployeeHotel(String ssn) {
        String sql = """
        SELECT r.*
        FROM room r
        JOIN works_as w ON w.hotel_id = r.hotel_id
        WHERE w.ssn = :ssn
        ORDER BY r.room_number
    """;

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("ssn", ssn),
                (rs, rowNum) -> {
                    RoomDto dto = new RoomDto();
                    dto.setHotelId(rs.getInt("hotel_id"));
                    dto.setRoomNumber(rs.getInt("room_number"));
                    dto.setPrice(rs.getBigDecimal("price"));
                    dto.setRoomCapacity(rs.getInt("room_capacity"));
                    dto.setRoomViewType(rs.getString("room_view_type"));
                    dto.setDamageStatus(rs.getString("damage_status"));
                    dto.setHasTv(rs.getBoolean("has_tv"));
                    dto.setHasAirConditioner(rs.getBoolean("has_air_conditioner"));
                    dto.setHasFridge(rs.getBoolean("has_fridge"));
                    dto.setRoomExtendedStatus(rs.getBoolean("room_extended_status"));
                    return dto;
                }
        );
    }

    private static class AvailableRoomRowMapper implements RowMapper<AvailableRoomDto> {
        @Override
        public AvailableRoomDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            AvailableRoomDto dto = new AvailableRoomDto();
            dto.setHotelId(rs.getInt("hotel_id"));
            dto.setHotelName(rs.getString("hotel_name"));
            dto.setChainName(rs.getString("chain_name"));
            dto.setArea(rs.getString("area"));
            dto.setRating(rs.getInt("rating"));
            dto.setTotalRooms(rs.getInt("total_rooms"));

            dto.setStreetName(rs.getString("street_name"));
            dto.setStreetNumber(rs.getString("street_number"));
            dto.setPostalCode(rs.getString("postal_code"));
            dto.setCity(rs.getString("city"));
            dto.setProvinceState(rs.getString("province_state"));
            dto.setCountry(rs.getString("country"));

            dto.setRoomNumber(rs.getInt("room_number"));
            dto.setPrice(rs.getBigDecimal("price"));
            dto.setRoomCapacity(rs.getInt("room_capacity"));
            dto.setRoomViewType(rs.getString("room_view_type"));
            dto.setDamageStatus(rs.getString("damage_status"));

            dto.setHasTv(rs.getBoolean("has_tv"));
            dto.setHasAirConditioner(rs.getBoolean("has_air_conditioner"));
            dto.setHasFridge(rs.getBoolean("has_fridge"));
            dto.setRoomExtendedStatus(rs.getBoolean("room_extended_status"));

            return dto;
        }
    }

    //Used in Manager interface to see list of employees at a hotel
    public List<EmployeeDto> getEmployeesByHotel(Integer hotelId) {
        String sql = """
        SELECT e.* FROM employee e
        JOIN works_as w ON e.ssn = w.ssn
        WHERE w.hotel_id = :hotelId
    """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("hotelId", hotelId), (rs, rowNum) -> {
            EmployeeDto e = new EmployeeDto();
            e.setSsn(rs.getString("ssn"));
            e.setFirstName(rs.getString("first_name"));
            e.setLastName(rs.getString("last_name"));
            e.setStreetName(rs.getString("street_name"));
            e.setStreetNumber(rs.getString("street_number"));
            e.setPostalCode(rs.getString("postal_code"));
            return e;
        });
    }


    //Used in manager interface to delete employees
    public void deleteEmployee(String ssn) {
        jdbcTemplate.update("DELETE FROM employee WHERE ssn = :ssn", new MapSqlParameterSource("ssn", ssn));
    }



    //Used in Manager Interface to insert or update an employee
    @Transactional
    public void saveOrUpdateEmployee(EmployeeDto emp, Integer hotelId) {
        // 1. Bulletproof fix for the 'Staff' role error
        jdbcTemplate.update("INSERT INTO role (role_name) VALUES ('Staff') ON CONFLICT DO NOTHING", new MapSqlParameterSource());

        // 2. Upsert the Employee (matches your schema columns)
        String employeeSql = """
        INSERT INTO employee (ssn, first_name, middle_name, last_name, street_name, street_number, postal_code)
        VALUES (:ssn, :firstName, :middleName, :lastName, :streetName, :streetNumber, :postalCode)
        ON CONFLICT (ssn) DO UPDATE SET
            first_name = EXCLUDED.first_name,
            middle_name = EXCLUDED.middle_name,
            last_name = EXCLUDED.last_name,
            street_name = EXCLUDED.street_name,
            street_number = EXCLUDED.street_number,
            postal_code = EXCLUDED.postal_code
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ssn", emp.getSsn())
                .addValue("firstName", emp.getFirstName())
                .addValue("middleName", emp.getMiddleName())
                .addValue("lastName", emp.getLastName())
                .addValue("streetName", emp.getStreetName())
                .addValue("streetNumber", emp.getStreetNumber())
                .addValue("postalCode", emp.getPostalCode())
                .addValue("hotelId", hotelId);

        jdbcTemplate.update(employeeSql, params);

        // 3. Link to hotel in 'works_as' table
        String worksAsSql = """
        INSERT INTO works_as (ssn, role_name, hotel_id)
        VALUES (:ssn, 'Staff', :hotelId)
        ON CONFLICT (ssn, role_name, hotel_id) DO NOTHING
    """;
        jdbcTemplate.update(worksAsSql, params);
    }
}