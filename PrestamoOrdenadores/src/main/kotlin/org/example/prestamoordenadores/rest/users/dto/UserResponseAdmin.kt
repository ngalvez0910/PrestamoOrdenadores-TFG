package org.example.prestamoordenadores.rest.users.dto

import org.example.prestamoordenadores.rest.users.models.Role

data class UserResponseAdmin (
    val guid: String,
    val username: String,
    val password: String,
    val roles: Role,
    val enabled: Boolean,
    var createdDate: String,
    var updatedDate: String,
    var lastLoginDate: String,
    var lastPasswordResetDate: String,
)