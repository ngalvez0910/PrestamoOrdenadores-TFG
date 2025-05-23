-- Crear tabla usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    campo_password VARCHAR(255) NOT NULL,
    rol VARCHAR(8) NOT NULL,
    numero_identificacion VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    curso VARCHAR(100) NULL DEFAULT NULL,
    tutor VARCHAR(100) NULL DEFAULT NULL,
    avatar VARCHAR(255) NOT NULL,
    is_activo BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_password_reset_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false,
    is_olvidado BOOLEAN DEFAULT false
);

-- Insertar usuarios de prueba
INSERT INTO usuarios (guid, email, campo_password, rol, numero_identificacion, nombre, apellidos, curso, tutor, avatar, is_activo, created_date, updated_date, last_login_date, last_password_reset_date, is_deleted, is_olvidado)
VALUES
    ('3854b5ba26c', 'juan.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'ALUMNO', '2015LT849', 'Juan', 'Pérez', '1º Bachillerato', 'María Gómez', '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('c1f40bb1900', 'maria.profesor.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'PROFESOR', '2023LT044', 'María', 'Gómez', '1º Bachillerato', NULL, '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('a1b2c3d4e5f', 'ana.lopez.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'ALUMNO', '2016LT101', 'Ana', 'López', '2º Bachillerato', 'Laura Vidal', '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('f5e4d3c2b1a', 'david.garcia.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'ALUMNO', '2017LT202', 'David', 'García', '1º ESO', 'Pedro Ramos', '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('abcdef12345', 'sofia.rodriguez.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'ALUMNO', '2018LT303', 'Sofía', 'Rodríguez', '3º ESO', 'Laura Vidal', '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('9z8y7x6w5v4', 'martin.sanz.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'ALUMNO', '2015LT404', 'Martín', 'Sanz', '1º Bachillerato', 'María Gómez', '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('fedcba98765', 'laura.vidal.profesor.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'PROFESOR', '2020LTP01', 'Laura', 'Vidal', '2º Bachillerato', NULL, '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('54321abcde', 'pedro.ramos.profesor.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'PROFESOR', '2019LTP02', 'Pedro', 'Ramos', '1º ESO, 3º ESO', NULL, '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('1a2b3c4d5e6', 'elena.diaz.profesor.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'PROFESOR', '2021LTP03', 'Elena', 'Díaz', 'FP Grado Medio', NULL, '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('0d6d031ad0a', 'admin.admin.loantech@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'ADMIN', '2010LT295', 'Admin', 'User', NULL, NULL, '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false),
    ('f768ece2a79', 'ailatan0910@gmail.com', '$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2', 'ADMIN', '2000LT214', 'Aila', 'Tan', NULL, NULL, '/assets/avatars/avatarAzul.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false);

-- Crear tabla incidencias
CREATE TABLE IF NOT EXISTS incidencias (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE,
    asunto VARCHAR(500) NOT NULL,
    descripcion VARCHAR(999) NOT NULL,
    estado_incidencia VARCHAR(9) NOT NULL,
    user_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false,

    CONSTRAINT fk_incidencia_user FOREIGN KEY (user_id) REFERENCES usuarios(id)
);

-- Insertar incidencias de prueba
INSERT INTO incidencias (guid, asunto, descripcion, estado_incidencia, user_id, created_date, updated_date, is_deleted)
VALUES
    ('INC000003', 'Cargador roto', 'El cargador esta despeluchado', 'PENDIENTE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    ('INC000006', 'Tecla W rota', 'La tecla W esta levantada y no se puede volver a colocar', 'PENDIENTE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    ('INC000010', 'Virus', 'El ordenador tiene un virus', 'PENDIENTE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);


-- Crear tabla dispositivos
CREATE TABLE IF NOT EXISTS dispositivos (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE,
    numero_serie VARCHAR(255) NOT NULL UNIQUE,
    componentes VARCHAR(255) NOT NULL,
    estado_dispositivo VARCHAR(13) NOT NULL,
    incidencia_id BIGINT UNIQUE,
    is_deleted BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_dispositivo_incidencia FOREIGN KEY (incidencia_id) REFERENCES incidencias(id)
);


-- Insertar dispositivos de prueba
INSERT INTO dispositivos (guid, numero_serie, componentes, estado_dispositivo, incidencia_id, is_deleted, created_date, updated_date)
VALUES
    ('ed472271676', '1AB123WXYZ', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a1b2c3d4e5f', '5CD456QWER', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('x9y8z7w6v5u', '9EF789TYUI', 'ratón, cargador', 'NO_DISPONIBLE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('m3n4o5p6q7r', '3GH012ASDF', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('l8k9j0h1g2f', '7JK345ZXCV', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('t5s4r3q2p1o', '2LM678POIU', 'ratón, cargador', 'NO_DISPONIBLE', 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d7e8f9g0h1i', '8NO901LKJH', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('v2w3x4y5z6a', '4PQ234MNBV', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c6b5a4z3y2x', '6RS567QAZX', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('n0m9l8k7j6i', '0TU890WSXC', 'ratón, cargador', 'NO_DISPONIBLE', 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('q8p7o6n5m4l', '5VW123ERTY', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b1a2z3y4x5w', '1XY234CDEF', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('k6l5m4n3o2p', '6ZT345GHIJ', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('f9e8d7c6b5a', '9UA678BCDE', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('r2s1t0u9v8w', '2VB789CDEF', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('j5i4h3g2f1e', '5WC012EFGH', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('h8g7f6e5d4c', '8XD345FGHI', 'ratón, cargador', 'PRESTADO', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('g1f2e3d4c5b', '1YE678HIJK', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('e4d5c6b7a8z', '4ZF901IJKL', 'ratón, cargador', 'DISPONIBLE', null, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



-- Crear tabla prestamos
CREATE TABLE IF NOT EXISTS prestamos (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    dispositivo_id BIGINT UNIQUE NOT NULL,
    estado_prestamo VARCHAR(20) NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false,

    CONSTRAINT fk_prestamo_user FOREIGN KEY (user_id) REFERENCES usuarios(id),
    CONSTRAINT fk_prestamo_dispositivo FOREIGN KEY (dispositivo_id) REFERENCES dispositivos(id)
);


-- Insertar prestamos de prueba
INSERT INTO prestamos (guid, user_id, dispositivo_id, estado_prestamo, fecha_prestamo, fecha_devolucion, created_date, updated_date, is_deleted)
VALUES
    ('938012eccbd', 1, 2, 'VENCIDO', '2025-04-01', '2025-04-15', '2025-04-01 10:00:00', '2025-04-16 09:00:00', false),
    ('456789abcde', 1, 4, 'VENCIDO', '2025-04-10', '2025-04-24', '2025-04-10 10:00:00', '2025-04-25 09:00:00', false),
    ('123456789ab', 2, 5, 'VENCIDO', '2024-12-01', '2024-12-15', '2024-12-01 10:00:00', '2024-12-16 09:00:00', false),
    ('987654321cd', 2, 7, 'VENCIDO', '2024-12-10', '2024-12-24', '2024-12-10 10:00:00', '2024-12-25 09:00:00', false),
    ('abcdefg123', 2, 8, 'VENCIDO', '2025-02-15', '2025-03-01', '2025-02-15 10:00:00', '2025-03-02 09:00:00', false),
    ('hijklmn456', 2, 9, 'VENCIDO', '2025-02-20', '2025-03-06', '2025-02-20 10:00:00', '2025-03-07 09:00:00', false),
    ('opqrsuv789', 3, 11, 'VENCIDO', '2025-04-20', '2025-05-04', '2025-04-20 10:00:00', '2025-05-05 09:00:00', false),
    ('zxcvbnm012', 4, 14, 'DEVUELTO', '2025-04-01', '2025-04-15', '2025-04-01 10:00:00', '2025-04-14 09:00:00', false),
    ('asdfghj345', 5, 17, 'EN_CURSO', '2025-05-10', '2025-05-24', '2025-05-10 10:00:00', '2025-05-10 09:00:00', false);

-- Crear tabla sanciones
CREATE TABLE IF NOT EXISTS sanciones (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    prestamo_id BIGINT NOT NULL,
    tipo_sancion VARCHAR(20) NOT NULL,
    fecha_sancion DATE NOT NULL,
    fecha_fin DATE NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false,

    CONSTRAINT fk_sancion_user FOREIGN KEY (user_id) REFERENCES usuarios(id),
    CONSTRAINT fk_sancion_prestamo FOREIGN KEY (prestamo_id) REFERENCES prestamos(id)
);


-- Insertar sanciones de prueba
INSERT INTO sanciones (guid, user_id, prestamo_id, tipo_sancion, fecha_sancion, fecha_fin, created_date, updated_date, is_deleted)
VALUES
    ('SANC000001', 2, 3, 'ADVERTENCIA', '2024-12-20', NULL, '2024-12-20 10:00:00', '2024-12-20 10:00:00', false),
    ('SANC000002', 2, 4, 'ADVERTENCIA', '2024-12-29', NULL, '2024-12-29 11:00:00', '2024-12-29 11:00:00', false),
    ('SANC000003', 2, 4, 'BLOQUEO_TEMPORAL', '2024-12-29', '2025-02-28', '2024-12-29 12:00:00', '2024-12-29 12:00:00', false),
    ('SANC000004', 2, 5, 'ADVERTENCIA', '2025-03-06', NULL, '2025-03-06 10:00:00', '2025-03-06 10:00:00', false),
    ('SANC000005', 2, 6, 'ADVERTENCIA', '2025-03-11', NULL, '2025-03-11 11:00:00', '2025-03-11 11:00:00', false),
    ('SANC000006', 2, 6, 'BLOQUEO_TEMPORAL', '2025-03-11', '2025-05-11', '2025-03-11 12:00:00', '2025-03-11 12:00:00', false);