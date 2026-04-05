package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.RentingDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RentingRepository {

    private final JdbcTemplate jdbcTemplate;

    public RentingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RentingDto findRentingById(Integer rentingId) {
        String sql = """
            SELECT *
            FROM renting
            WHERE renting_id = ?
        """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return null;

            RentingDto r = new RentingDto();
            r.setRentingId(rs.getInt("renting_id"));
            r.setSsn(rs.getString("ssn"));
            r.setHotelId(rs.getInt("hotel_id"));
            r.setRoomNumber(rs.getInt("room_number"));
            r.setBookingId(rs.getInt("booking_id"));
            r.setDrivingLicenseNumber(rs.getString("driving_license_number"));
            r.setStartDatetime(rs.getTimestamp("start_datetime").toLocalDateTime());
            r.setEndDatetime(rs.getTimestamp("end_datetime").toLocalDateTime());

            if (rs.getTimestamp("actual_check_in_time") != null)
                r.setActualCheckInTime(rs.getTimestamp("actual_check_in_time").toLocalDateTime());

            if (rs.getTimestamp("actual_check_out_time") != null)
                r.setActualCheckOutTime(rs.getTimestamp("actual_check_out_time").toLocalDateTime());

            r.setIsPaid(rs.getBoolean("is_paid"));

            if (rs.getTimestamp("paid_on") != null)
                r.setPaidOn(rs.getTimestamp("paid_on").toLocalDateTime());

            r.setCustomerNameSnapshot(rs.getString("customer_name_snapshot"));
            r.setHotelNameSnapshot(rs.getString("hotel_name_snapshot"));
            r.setAreaSnapshot(rs.getString("area_snapshot"));
            r.setRoomPriceSnapshot(rs.getBigDecimal("room_price_snapshot"));

            return r;
        }, rentingId);
    }

    public void deleteById(Integer rentingId) {
        String sql = "DELETE FROM renting WHERE renting_id = ?";
        jdbcTemplate.update(sql, rentingId);
    }
}