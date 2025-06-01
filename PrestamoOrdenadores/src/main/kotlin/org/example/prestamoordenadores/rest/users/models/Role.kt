package org.example.prestamoordenadores.rest.users.models

/**
 * Enumera los posibles roles que puede tener un usuario en el sistema.
 *
 * - ADMIN: Usuario con permisos administrativos.
 * - PROFESOR: Usuario con rol de profesor.
 * - ALUMNO: Usuario con rol de alumno.
 *
 * @author Natalia González Álvarez
 */
enum class Role {
    ADMIN,
    PROFESOR,
    ALUMNO
}
