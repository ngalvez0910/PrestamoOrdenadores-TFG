package org.example.prestamoordenadores.rest.users.dto

data class UserLoginRequest (
    val email: String,
    val password: String
)