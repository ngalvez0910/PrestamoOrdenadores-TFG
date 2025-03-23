package org.example.prestamoordenadores.rest.users.dto

data class UserToken(
    val user: UserResponseAdmin,
    val token: String
)