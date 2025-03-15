/*CREATE OR REPLACE FUNCTION generate_guid() RETURNS VARCHAR(11) AS '
DECLARE
    chars TEXT := ''ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'';
    result TEXT := '''';
    i INT := 0;
BEGIN
    WHILE i < 11 LOOP
        result := result || substring(chars FROM floor(random() * length(chars) + 1)::int FOR 1);
        i := i + 1;
    END LOOP;
    RETURN result;
END;
' LANGUAGE plpgsql;

-- Crear tabla usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE DEFAULT generate_guid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(8) NOT NULL,
    numero_identificacion VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    curso VARCHAR(100) NULL DEFAULT NULL,
    tutor VARCHAR(100) NULL DEFAULT NULL,
    foto_carnet VARCHAR(255) NOT NULL,
    avatar VARCHAR(255) NOT NULL,
    is_activo BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_password_reset_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

 */

-- Insertar usuarios de prueba
INSERT INTO usuarios (guid, email, password, rol, numero_identificacion, nombre, apellidos, curso, tutor, foto_carnet, avatar, is_activo, created_date, updated_date, last_login_date, last_password_reset_date)
VALUES
    ('3854b5ba26c', 'juan@colegio.com', 'password123', 'ALUMNO', '12345678A', 'Juan', 'Pérez', '1º Bachillerato', 'María Gómez', 'foto1.jpg', 'avatar1.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c1f40bb1900', 'maria@profesor.colegio.com', 'password456', 'PROFESOR', '87654321B', 'María', 'Gómez', '1º Bachillerato', NULL, 'foto2.jpg', 'avatar2.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('0d6d031ad0a', 'admin@admin.colegio.com', 'adminpassword', 'ADMIN', '00000000X', 'Admin', 'User', NULL, NULL, 'foto3.jpg', 'avatar3.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

/*
-- Crear tabla dispositivos
CREATE TABLE IF NOT EXISTS dispositivos (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE DEFAULT generate_guid(),
    numero_serie VARCHAR(255) NOT NULL UNIQUE,
    componentes VARCHAR(255) NOT NULL,
    estado_dispositivo VARCHAR(13) NOT NULL,
    incidencia_guid VARCHAR(11) NULL DEFAULT NULL,
    stock INTEGER NOT NULL,
    is_activo BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

 */


-- Insertar dispositivos de prueba
INSERT INTO dispositivos (guid, numero_serie, componentes, estado_dispositivo, incidencia_guid, stock, is_activo, created_date, updated_date)
VALUES
    ('ed472271676', '1AB123WXYZ', 'CPU, RAM, SSD', 'NUEVO', null, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a1b2c3d4e5f', '5CD456QWER', 'CPU, RAM, HDD', 'PRESTADO', null, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('x9y8z7w6v5u', '9EF789TYUI', 'CPU, GPU, RAM', 'NO_DISPONIBLE', 'INC000003', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('m3n4o5p6q7r', '3GH012ASDF', 'CPU, RAM, SSD, GPU', 'NUEVO', null, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('l8k9j0h1g2f', '7JK345ZXCV', 'CPU, RAM, HDD', 'PRESTADO', null, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('t5s4r3q2p1o', '2LM678POIU', 'CPU, RAM, SSD', 'NO_DISPONIBLE', 'INC000006', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d7e8f9g0h1i', '8NO901LKJH', 'CPU, GPU, RAM, SSD', 'NUEVO', null, 7, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('v2w3x4y5z6a', '4PQ234MNBV', 'CPU, RAM', 'PRESTADO', null, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c6b5a4z3y2x', '6RS567QAZX', 'CPU, RAM, SSD, GPU', 'NUEVO', null, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('n0m9l8k7j6i', '0TU890WSXC', 'CPU, RAM, HDD', 'NO_DISPONIBLE', 'INC000010', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
