CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    guid UUID NOT NULL DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(8) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP,
    last_password_reset_date TIMESTAMP
);

INSERT INTO users (username, password, roles, enabled, created_date, updated_date, last_login_date, last_password_reset_date)
VALUES
    ('juan', 'password123', 'ALUMNO', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('maria', 'password456', 'PROFESOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('admin', 'adminpassword', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
