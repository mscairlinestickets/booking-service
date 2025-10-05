CREATE TABLE booking_orders (
    booking_id BIGSERIAL PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    origin VARCHAR(10),
    destination VARCHAR(10),
    departure_date_time TIMESTAMP,
    price DECIMAL(10, 2)  CHECK (price > 0),
    quantity INTEGER  CHECK (quantity >= 1),
    airline_name VARCHAR(100),
    status VARCHAR(20),
    payment_type VARCHAR(20) NOT NULL,
    icao_code VARCHAR(10),
    aircraft_model VARCHAR(100),
    created_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);
