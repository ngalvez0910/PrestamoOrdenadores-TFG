package org.example.prestamoordenadores.rest.users.dto

data class UserPasswordResetRequest (
    var oldPassword: String,
    var newPassword: String,
    var confirmPassword: String
)