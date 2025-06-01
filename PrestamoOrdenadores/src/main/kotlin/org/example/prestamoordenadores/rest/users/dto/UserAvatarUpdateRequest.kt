package org.example.prestamoordenadores.rest.users.dto

import org.jetbrains.annotations.NotNull

/**
 * Representa una solicitud para actualizar el avatar de un usuario.
 *
 * @property avatar La URL o identificador del nuevo avatar. No puede ser null.
 *
 * @author Natalia González Álvarez
 */
data class UserAvatarUpdateRequest(
    @NotNull("Avatar no puede ser null")
    var avatar: String
)