package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.AvailableRoomDto;
import ca.ehotels.backend.model.CreateRentingRequest;
import ca.ehotels.backend.model.EmployeeInfoDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
                      AND b.archive_status = FALSE
                      AND b.start_day < :endDate
                      AND b.end_day > :startDate
              )
              AND NOT EXISTS (
                    SELECT 1
                    FROM renting rt
                    WHERE rt.hotel_id = r.hotel_id
                      AND rt.room_number = r.room_number
                      AND rt.archive_status = FALSE
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
}