package org.example.prestamoordenadores.rest.users.dto

import org.jetbrains.annotations.NotNull

/**
 * Representa una solicitud para restablecer la contraseña de un usuario.
 *
 * @property oldPassword La contraseña actual del usuario. No puede estar vacía.
 * @property newPassword La nueva contraseña que se desea establecer. No puede estar vacía.
 * @property confirmPassword Confirmación de la nueva contraseña. No puede estar vacía.
 *
 * @author Natalia González Álvarez
 */
data class UserPasswordResetRequest(
    @NotNull("old password no puede estar vacío")
    var oldPassword: String,

    @NotNull("new password no puede estar vacío")
    var newPassword: String,

    @NotNull("confirm password no puede estar vacío")
    var confirmPassword: String
)
