package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.CreateCustomerRequest;
import ca.ehotels.backend.model.CustomerDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CustomerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int updateCustomer(CustomerDto request) {
        String sql = """
        UPDATE customer
        SET first_name = :firstName,
            last_name = :lastName,
            street_name = :streetName,
            street_number = :streetNumber,
            postal_code = :postalCode
        WHERE driving_license_number = :license
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("license", request.getDrivingLicenseNumber())
                .addValue("firstName", request.getFirstName())
                .addValue("lastName", request.getLastName())
                .addValue("streetName", request.getStreetName())
                .addValue("streetNumber", request.getStreetNumber())
                .addValue("postalCode", request.getPostalCode());

        return jdbcTemplate.update(sql, params);
    }

    public CustomerDto getCustomer(String license) {
        String sql = "SELECT * FROM customer WHERE driving_license_number = :license";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    new MapSqlParameterSource("license", license),
                    (rs, rowNum) -> {
                        CustomerDto c = new CustomerDto();
                        c.setDrivingLicenseNumber(rs.getString("driving_license_number"));
                        c.setFirstName(rs.getString("first_name"));
                        c.setLastName(rs.getString("last_name"));
                        c.setStreetName(rs.getString("street_name"));
                        c.setStreetNumber(rs.getString("street_number"));
                        c.setPostalCode(rs.getString("postal_code"));
                        return c;
                    }
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null; // Return null so the Controller can send a clean 404
        }
    }

    @Transactional
    public int createCustomer(CreateCustomerRequest request) {
        String insertCustomerSql = """
            INSERT INTO customer (
                driving_license_number,
                first_name,
                last_name,
                street_name,
                street_number,
                postal_code,
                date_of_registration
            )
            VALUES (
                :drivingLicenseNumber,
                :firstName,
                :lastName,
                :streetName,
                :streetNumber,
                :postalCode,
                CURRENT_DATE
            )
            """;

        String insertPhoneSql = """
            INSERT INTO customer_phone (
                driving_license_number,
                phone_number
            )
            VALUES (
                :drivingLicenseNumber,
                :phoneNumber
            )
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("drivingLicenseNumber", request.getDrivingLicenseNumber())
                .addValue("firstName", request.getFirstName())
                .addValue("lastName", request.getLastName())
                .addValue("streetName", request.getStreetName())
                .addValue("streetNumber", request.getStreetNumber())
                .addValue("postalCode", request.getPostalCode())
                .addValue("phoneNumber", request.getPhoneNumber());

        int rows1 = jdbcTemplate.update(insertCustomerSql, params);
        int rows2 = jdbcTemplate.update(insertPhoneSql, params);

        return rows1 + rows2;
    }


    //Used in manager interface in order to see the customer list
    public List<CustomerDto> getCustomersByHotel(Integer hotelId) {
        String sql = """
        SELECT DISTINCT c.driving_license_number,
                        c.first_name,
                        c.last_name
        FROM customer c
        JOIN (
            SELECT driving_license_number
            FROM booking
            WHERE hotel_id = :hotelId

            UNION

            SELECT driving_license_number
            FROM renting
            WHERE hotel_id = :hotelId
        ) hc
        ON c.driving_license_number = hc.driving_license_number
        ORDER BY c.last_name ASC, c.first_name ASC
    """;

        return jdbcTemplate.query(sql, new MapSqlParameterSource("hotelId", hotelId), (rs, rowNum) -> {
            CustomerDto c = new CustomerDto();
            c.setDrivingLicenseNumber(rs.getString("driving_license_number"));
            c.setFirstName(rs.getString("first_name"));
            c.setLastName(rs.getString("last_name"));
            return c;
        });
    }
    //Used in manager interface in order to remove profiles
    public void deleteCustomer(String license) {
        // Note: This will fail if the customer has active bookings due to ON DELETE RESTRICT
        String sql = "DELETE FROM customer WHERE driving_license_number = :license";
        jdbcTemplate.update(sql, new MapSqlParameterSource("license", license));
    }
}