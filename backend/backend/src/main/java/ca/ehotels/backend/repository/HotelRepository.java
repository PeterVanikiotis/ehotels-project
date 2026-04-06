package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.HotelDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HotelRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public HotelRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void updateHotel(HotelDto hotel) {
        // 1. Update the main hotel table
        String sql = "UPDATE hotel SET hotel_name = :name, rating = :rating WHERE hotel_id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("id", hotel.getHotelId())
                .addValue("name", hotel.getHotelName())
                .addValue("rating", hotel.getRating()));

        // 2. Sync Emails (Delete existing and replace)
        jdbcTemplate.update("DELETE FROM hotel_email WHERE hotel_id = :id", new MapSqlParameterSource("id", hotel.getHotelId()));
        if (hotel.getEmailAddresses() != null) {
            for (String email : hotel.getEmailAddresses()) {
                if (!email.isBlank()) {
                    jdbcTemplate.update("INSERT INTO hotel_email (hotel_id, email_address) VALUES (:id, :email)",
                            new MapSqlParameterSource("id", hotel.getHotelId()).addValue("email", email.trim()));
                }
            }
        }
    }
}