package ca.ehotels.backend.repository;

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