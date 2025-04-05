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
    foto_carnet VARCHAR(255) NOT NULL,
    avatar VARCHAR(255) NOT NULL,
    is_activo BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_password_reset_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar usuarios de prueba
INSERT INTO usuarios (guid, email, campo_password, rol, numero_identificacion, nombre, apellidos, curso, tutor, foto_carnet, avatar, is_activo, created_date, updated_date, last_login_date, last_password_reset_date)
VALUES
    ('3854b5ba26c', 'juan@colegio.com', '$2a$12$UPgYQkpzWilCyxOY7Wfo9Ob/x6t12blKi4fK2jzaBeWf3rhen.L6m', 'ALUMNO', '12345678A', 'Juan', 'Pérez', '1º Bachillerato', 'María Gómez', 'foto1.jpg', 'avatar1.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c1f40bb1900', 'maria@profesor.colegio.com', '$2a$12$UPgYQkpzWilCyxOY7Wfo9Ob/x6t12blKi4fK2jzaBeWf3rhen.L6m', 'PROFESOR', '87654321B', 'María', 'Gómez', '1º Bachillerato', NULL, 'foto2.jpg', 'avatar2.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('0d6d031ad0a', 'admin@admin.colegio.com', '$2a$12$UPgYQkpzWilCyxOY7Wfo9Ob/x6t12blKi4fK2jzaBeWf3rhen.L6m', 'ADMIN', '00000000X', 'Admin', 'User', NULL, NULL, 'foto3.jpg', 'avatar3.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Crear tabla incidencias
CREATE TABLE IF NOT EXISTS incidencias (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE,
    asunto VARCHAR(500) NOT NULL,
    descripcion VARCHAR(999) NOT NULL,
    estado_incidencia VARCHAR(9) NOT NULL,
    user_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar incidencias de prueba
INSERT INTO incidencias (guid, asunto, descripcion, estado_incidencia, user_id, created_date, updated_date)
VALUES
    ('INC000003', 'Cargador roto', 'El cargador esta despeluchado', 'PENDIENTE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('INC000006', 'Tecla W rota', 'La tecla W esta levantada y no se puede volver a colocar', 'PENDIENTE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('INC000010', 'Virus', 'El ordenador tiene un virus', 'PENDIENTE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- Crear tabla dispositivos
CREATE TABLE IF NOT EXISTS dispositivos (
    id SERIAL PRIMARY KEY,
    guid VARCHAR(11) NOT NULL UNIQUE,
    numero_serie VARCHAR(255) NOT NULL UNIQUE,
    componentes VARCHAR(255) NOT NULL,
    estado_dispositivo VARCHAR(13) NOT NULL,
    incidencia_id BIGINT UNIQUE,
    is_activo BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Insertar dispositivos de prueba
INSERT INTO dispositivos (guid, numero_serie, componentes, estado_dispositivo, incidencia_id, is_activo, created_date, updated_date)
VALUES
    ('ed472271676', '1AB123WXYZ', 'ratón, cargador', 'DISPONIBLE', null, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a1b2c3d4e5f', '5CD456QWER', 'ratón, cargador', 'PRESTADO', null, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('x9y8z7w6v5u', '9EF789TYUI', 'ratón, cargador', 'NO_DISPONIBLE', 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('m3n4o5p6q7r', '3GH012ASDF', 'ratón, cargador', 'DISPONIBLE', null, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('l8k9j0h1g2f', '7JK345ZXCV', 'ratón, cargador', 'PRESTADO', null, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('t5s4r3q2p1o', '2LM678POIU', 'ratón, cargador', 'NO_DISPONIBLE', 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d7e8f9g0h1i', '8NO901LKJH', 'ratón, cargador', 'DISPONIBLE', null, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('v2w3x4y5z6a', '4PQ234MNBV', 'ratón, cargador', 'PRESTADO', null, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c6b5a4z3y2x', '6RS567QAZX', 'ratón, cargador', 'DISPONIBLE', null, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('n0m9l8k7j6i', '0TU890WSXC', 'ratón, cargador', 'NO_DISPONIBLE', 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
