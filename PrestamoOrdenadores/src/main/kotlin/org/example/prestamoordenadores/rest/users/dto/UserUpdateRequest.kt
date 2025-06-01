package org.example.prestamoordenadores.rest.users.dto

/**
 * Representa una solicitud para actualizar ciertos datos de un usuario.
 *
 * @property rol El nuevo rol del usuario. Puede ser nulo si no se actualiza.
 * @property isActivo Indica si el usuario está activo o no. Puede ser nulo si no se actualiza.
 *
 * @author Natalia González Álvarez
 */
data class UserUpdateRequest(
    val rol: String?,
    val isActivo: Boolean?
)
