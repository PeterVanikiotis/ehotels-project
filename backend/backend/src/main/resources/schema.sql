CREATE TABLE IF NOT EXISTS hotel_chain (
    central_office_id SERIAL PRIMARY KEY,
    chain_name VARCHAR(100) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    building_number VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS hotel_chain_email (
    email_id SERIAL PRIMARY KEY,
    central_office_id INT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    CONSTRAINT fk_hotel_chain_email
        FOREIGN KEY (central_office_id)
        REFERENCES hotel_chain(central_office_id)
        ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS hotel_chain_phone (
    phone_id SERIAL PRIMARY KEY,
    central_office_id INT NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    CONSTRAINT fk_hotel_chain_phone
        FOREIGN KEY (central_office_id)
        REFERENCES hotel_chain(central_office_id)
        ON DELETE CASCADE
    );