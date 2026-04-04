package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.AvailableRoomDto;
import ca.ehotels.backend.model.HotelCapacityDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class CustomerSearchRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomerSearchRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AvailableRoomDto> searchAvailableRooms(
            LocalDate startDate,
            LocalDate endDate,
            Integer capacity,
            String area,
            Integer hotelId,
            Integer rating,
            Integer minTotalRooms,
            Double maxPrice
    ) {
        StringBuilder sql = new StringBuilder("""
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
            WHERE r.damage_status = 'none'
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
            """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("startDate", startDate)
                .addValue("endDate", endDate);

        if (capacity != null) {
            sql.append(" AND r.room_capacity >= :capacity");
            params.addValue("capacity", capacity);
        }

        if (area != null && !area.isBlank()) {
            sql.append(" AND LOWER(TRIM(h.area)) = LOWER(TRIM(:area))");
            params.addValue("area", area.trim());
        }

        if (rating != null) {
            sql.append(" AND h.rating = :rating");
            params.addValue("rating", rating);
        }

        if (minTotalRooms != null) {
            sql.append(" AND h.number_of_rooms >= :minTotalRooms");
            params.addValue("minTotalRooms", minTotalRooms);
        }

        if (maxPrice != null) {
            sql.append(" AND r.price <= :maxPrice");
            params.addValue("maxPrice", maxPrice);
        }

        if (hotelId != null) {
            sql.append(" AND h.hotel_id = :hotelId ");
            params.addValue("hotelId", hotelId);
        }

        sql.append("""
            ORDER BY hc.chain_name, h.hotel_name, r.price, r.room_number
            """);

        return jdbcTemplate.query(sql.toString(), params, new AvailableRoomRowMapper());
    }

    public List<HotelCapacityDto> getHotelsWithCapacity(String area) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            tcp.hotel_id,
            tcp.hotel_name,
            hc.chain_name,
            tcp.total_capacity
        FROM total_capacity_per_hotel tcp
        JOIN hotel h
          ON tcp.hotel_id = h.hotel_id
        JOIN hotel_chain hc
          ON h.central_office_id = hc.central_office_id
        WHERE 1=1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (area != null && !area.isBlank()) {
            sql.append(" AND LOWER(TRIM(h.area)) = LOWER(TRIM(:area))");
            params.addValue("area", area.trim());
        }

        sql.append(" ORDER BY hc.chain_name, tcp.hotel_name");

        return jdbcTemplate.query(sql.toString(), params, new HotelCapacityRowMapper());
    }

    public List<String> getAreas() {
        String sql = """
            SELECT DISTINCT TRIM(area) AS area
            FROM hotel
            WHERE area IS NOT NULL
              AND TRIM(area) <> ''
            ORDER BY area
            """;

        return jdbcTemplate.query(
                sql,
                Collections.emptyMap(),
                (rs, rowNum) -> rs.getString("area")
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
    private static class HotelCapacityRowMapper implements RowMapper<HotelCapacityDto> {
        @Override
        public HotelCapacityDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            HotelCapacityDto dto = new HotelCapacityDto();
            dto.setHotelId(rs.getInt("hotel_id"));
            dto.setHotelName(rs.getString("hotel_name"));
            dto.setChainName(rs.getString("chain_name"));
            dto.setTotalCapacity(rs.getInt("total_capacity"));
            return dto;
        }
    }
}