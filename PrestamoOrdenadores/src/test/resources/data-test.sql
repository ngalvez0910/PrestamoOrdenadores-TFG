-- Crear BBDD para tests
CREATE DATABASE "prestamosDB-test";

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