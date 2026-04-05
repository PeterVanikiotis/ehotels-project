package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.BookingDto;
import ca.ehotels.backend.model.CreateBookingRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BookingRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteById(Integer bookingId) {
        String sql = "DELETE FROM booking WHERE booking_id = :bookingId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookingId", bookingId);

        jdbcTemplate.update(sql, params);
    }

    public BookingDto findBookingById(Integer bookingId) {
        String sql = """
        SELECT *
        FROM booking
        WHERE booking_id = :bookingId
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookingId", bookingId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            BookingDto b = new BookingDto();
            b.setBookingId(rs.getInt("booking_id"));
            b.setDrivingLicenseNumber(rs.getString("driving_license_number"));
            b.setHotelId(rs.getInt("hotel_id"));
            b.setRoomNumber(rs.getInt("room_number"));
            b.setStartDay(rs.getDate("start_day").toLocalDate());
            b.setEndDay(rs.getDate("end_day").toLocalDate());

            if (rs.getTimestamp("check_in_time") != null) {
                b.setCheckInTime(rs.getTimestamp("check_in_time").toLocalDateTime());
            }

            if (rs.getTimestamp("check_out_time") != null) {
                b.setCheckOutTime(rs.getTimestamp("check_out_time").toLocalDateTime());
            }

            b.setCustomerNameSnapshot(rs.getString("customer_name_snapshot"));
            b.setHotelNameSnapshot(rs.getString("hotel_name_snapshot"));
            b.setAreaSnapshot(rs.getString("area_snapshot"));
            b.setRoomPriceSnapshot(rs.getBigDecimal("room_price_snapshot"));

            if (rs.getTimestamp("created_at") != null) {
                b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            return b;
        }).stream().findFirst().orElse(null);
    }

    public int createBooking(CreateBookingRequest request) {
        String sql = """
            INSERT INTO booking (
                driving_license_number,
                hotel_id,
                room_number,
                start_day,
                end_day,
                customer_name_snapshot,
                hotel_name_snapshot,
                area_snapshot,
                room_price_snapshot
            )
            SELECT
                c.driving_license_number,
                r.hotel_id,
                r.room_number,
                :startDate,
                :endDate,
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
            WHERE c.driving_license_number = :drivingLicenseNumber
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("drivingLicenseNumber", request.getDrivingLicenseNumber())
                .addValue("hotelId", request.getHotelId())
                .addValue("roomNumber", request.getRoomNumber())
                .addValue("startDate", request.getStartDate())
                .addValue("endDate", request.getEndDate());

        return jdbcTemplate.update(sql, params);
    }
}