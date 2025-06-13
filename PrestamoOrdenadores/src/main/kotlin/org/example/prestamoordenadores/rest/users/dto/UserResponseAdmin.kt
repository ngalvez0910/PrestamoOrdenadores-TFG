package org.example.prestamoordenadores.rest.users.dto

import org.example.prestamoordenadores.rest.users.models.Role

/**
 * Representa la información detallada de un usuario para respuestas destinadas a administradores.
 *
 * @property numeroIdentificacion El número de identificación único del usuario.
 * @property guid Identificador global único del usuario.
 * @property email Dirección de correo electrónico del usuario.
 * @property nombre Nombre del usuario.
 * @property apellidos Apellidos del usuario.
 * @property curso Curso actual en el que está matriculado el usuario.
 * @property tutor Nombre del tutor asignado al usuario.
 * @property rol Rol asignado al usuario dentro del sistema (por ejemplo, ADMIN o USER).
 * @property isActivo Indica si el usuario está activo en el sistema.
 * @property createdDate Fecha de creación del usuario (formato ISO 8601 recomendado).
 * @property updatedDate Fecha de la última actualización de los datos del usuario.
 * @property lastLoginDate Fecha del último inicio de sesión del usuario.
 * @property lastPasswordResetDate Fecha del último restablecimiento de contraseña del usuario.
 * @property isDeleted Indica si el usuario ha sido marcado como eliminado lógicamente.
 * @property isOlvidado Indica si el usuario ha ejercido el derecho al olvido (GDPR).
 *
 * @author Natalia González Álvarez
 */
data class UserResponseAdmin(
    val numeroIdentificacion: String,
    val guid: String,
    val email: String,
    val nombre: String,
    val apellidos: String,
    val curso: String,
    val tutor: String,
    val rol: Role,
    val isActivo: Boolean,
    var createdDate: String,
    var updatedDate: String,
    var lastLoginDate: String,
    var lastPasswordResetDate: String,
    var isDeleted: Boolean,
    var isOlvidado: Boolean
)
