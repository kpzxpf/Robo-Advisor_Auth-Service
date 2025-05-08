CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       role_name VARCHAR(50) UNIQUE NOT NULL
);


CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       date_of_birth DATE,
                       address TEXT,
                       phone_number VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       role_id INTEGER REFERENCES roles(id)
);