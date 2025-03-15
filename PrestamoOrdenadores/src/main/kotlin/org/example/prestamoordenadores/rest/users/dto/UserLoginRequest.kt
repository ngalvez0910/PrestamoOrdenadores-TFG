package org.example.prestamoordenadores.rest.users.dto

import org.jetbrains.annotations.NotNull

data class UserLoginRequest (
    @NotNull("Email no puede ser null")
    val email: String,
    @NotNull("Password no puede estar vacío")
    val password: String
)