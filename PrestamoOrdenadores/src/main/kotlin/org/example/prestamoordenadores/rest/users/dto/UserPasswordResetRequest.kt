package org.example.prestamoordenadores.rest.users.dto

import org.jetbrains.annotations.NotNull

data class UserPasswordResetRequest (
    @NotNull("old password no puede estar vacío")
    var oldPassword: String,
    @NotNull("new password no puede estar vacío")
    var newPassword: String,
    @NotNull("confirm password no puede estar vacío")
    var confirmPassword: String
)