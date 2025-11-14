CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'ADMIN'))
);

CREATE TABLE tables (
    id SERIAL PRIMARY KEY,
    capacity INT NOT NULL CHECK (capacity IN (2, 4, 6)),
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    reservation_datetime TIMESTAMP NOT NULL,
    people_count INT NOT NULL CHECK (people_count BETWEEN 1 AND 8),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    user_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_table FOREIGN KEY (table_id) REFERENCES tables (id),
    CONSTRAINT uk_table_datetime UNIQUE (table_id, reservation_datetime)
);
