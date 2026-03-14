INSERT INTO hotel_chain (chain_name, street_name, building_number, postal_code)
VALUES
    ('Maple Leaf Hotels', 'King Street West', '100', 'M5H 1J9'),
    ('Aurora Hospitality', 'Rue Sainte-Catherine Ouest', '2020', 'H3H 2T1');

INSERT INTO hotel_chain_email (central_office_id, email_address)
VALUES
    (1, 'info@mapleleafhotels.com'),
    (1, 'support@mapleleafhotels.com'),
    (2, 'contact@aurorahospitality.com'),
    (2, 'bookings@aurorahospitality.com');

INSERT INTO hotel_chain_phone (central_office_id, phone_number)
VALUES
    (1, '613-555-1000'),
    (1, '613-555-1001'),
    (2, '613-555-2020'),
    (2, '613-555-2021');