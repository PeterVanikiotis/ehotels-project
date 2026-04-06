package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.HotelDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ManagerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ManagerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public HotelDto getManagedHotel(String managerSsn) {
        String hotelSql = "SELECT * FROM hotel WHERE manager_ssn = :ssn";
        try {
            HotelDto hotel = jdbcTemplate.queryForObject(hotelSql, new MapSqlParameterSource("ssn", managerSsn), (rs, rowNum) -> {
                HotelDto h = new HotelDto();
                h.setHotelId(rs.getInt("hotel_id"));
                h.setHotelName(rs.getString("hotel_name"));
                h.setStreetName(rs.getString("street_name"));
                h.setStreetNumber(rs.getString("street_number"));
                h.setPostalCode(rs.getString("postal_code"));
                h.setRating(rs.getInt("rating"));
                return h;
            });

            // Fetch Emails
            List<String> emails = jdbcTemplate.queryForList(
                    "SELECT email_address FROM hotel_email WHERE hotel_id = :id",
                    new MapSqlParameterSource("id", hotel.getHotelId()), String.class);
            hotel.setEmailAddresses(emails);

            // Fetch Phones
            List<String> phones = jdbcTemplate.queryForList(
                    "SELECT phone_number FROM hotel_phone WHERE hotel_id = :id",
                    new MapSqlParameterSource("id", hotel.getHotelId()), String.class);
            hotel.setPhoneNumbers(phones);

            return hotel;
        } catch (Exception e) {
            return null;
        }
    }
}