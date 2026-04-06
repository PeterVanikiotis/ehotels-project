package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.HotelDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HotelRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public HotelRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateHotel(HotelDto hotel) {
        String sql = """
            UPDATE hotel 
            SET hotel_name = :name,
                street_name = :street,
                street_number = :num,
                postal_code = :pc,
                rating = :rating
            WHERE hotel_id = :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", hotel.getHotelId())
                .addValue("name", hotel.getHotelName())
                .addValue("street", hotel.getStreetName())
                .addValue("num", hotel.getStreetNumber())
                .addValue("pc", hotel.getPostalCode())
                .addValue("rating", hotel.getRating());

        jdbcTemplate.update(sql, params);
    }
}