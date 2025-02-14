package org.example.prestamoordenadores.rest.users.dto

data class UserLoginRequest (
    val username: String,
    val password: String
)