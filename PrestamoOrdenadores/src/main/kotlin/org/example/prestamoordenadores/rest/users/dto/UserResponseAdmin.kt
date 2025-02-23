package org.example.prestamoordenadores.rest.users.dto

import org.example.prestamoordenadores.rest.users.models.Role

data class UserResponseAdmin (
    val guid: String,
    val username: String,
    val roles: Role,
    val email: String,
    val nombre: String,
    val apellido: String,
    val curso: String,
    val tutor: String,
    val enabled: Boolean,
    var createdDate: String,
    var updatedDate: String,
    var lastLoginDate: String,
    var lastPasswordResetDate: String,
)