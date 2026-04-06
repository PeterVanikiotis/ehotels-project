package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.HotelDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ManagerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ManagerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public HotelDto getManagedHotel(String managerSsn) {
        // Checks if this SSN is assigned as a manager_ssn in the hotel table
        String sql = "SELECT * FROM hotel WHERE manager_ssn = :ssn";

        try {
            return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("ssn", managerSsn), (rs, rowNum) -> {
                HotelDto h = new HotelDto();
                h.setHotelId(rs.getInt("hotel_id"));
                h.setHotelName(rs.getString("hotel_name"));
                h.setStreetName(rs.getString("street_name"));
                h.setStreetNumber(rs.getString("street_number"));
                h.setPostalCode(rs.getString("postal_code"));
                h.setRating(rs.getInt("rating"));
                return h;
            });
        } catch (Exception e) {
            return null; // Not a manager or SSN doesn't exist
        }
    }
}