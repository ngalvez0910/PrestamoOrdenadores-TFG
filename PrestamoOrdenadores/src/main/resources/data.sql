CREATE OR REPLACE FUNCTION generate_guid() RETURNS VARCHAR(11) AS '
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


-- Eliminar la tabla si existe
DROP TABLE IF EXISTS usuarios CASCADE;

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
    foto_carnet VARCHAR(255) NULL DEFAULT NULL,
    avatar VARCHAR(255) NOT NULL,
    is_activo BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_password_reset_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));


-- Insertar usuarios de prueba
INSERT INTO usuarios (guid, email, password, rol, numero_identificacion, nombre, apellidos, curso, tutor, foto_carnet, avatar, is_activo, created_date, updated_date, last_login_date, last_password_reset_date)
VALUES
    ('3854b5ba26c', 'juan@colegio.com', 'password123', 'ALUMNO', '12345678A', 'Juan', 'Pérez', '1º Bachillerato', 'María Gómez', 'foto1.jpg', 'avatar1.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c1f40bb1900', 'maria@profesor.colegio.com', 'password456', 'PROFESOR', '87654321B', 'María', 'Gómez', '1º Bachillerato', NULL, 'foto2.jpg', 'avatar2.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('0d6d031ad0a', 'admin@admin.colegio.com', 'adminpassword', 'ADMIN', '00000000X', 'Admin', 'User', NULL, NULL, NULL, 'avatar3.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
