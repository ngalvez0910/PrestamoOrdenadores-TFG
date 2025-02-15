package org.example.prestamoordenadores.rest.users.dto

data class UserResponse (
    var guid : String,
    var username: String,
    var password: String,
    var lastPasswordResetDate: String
)