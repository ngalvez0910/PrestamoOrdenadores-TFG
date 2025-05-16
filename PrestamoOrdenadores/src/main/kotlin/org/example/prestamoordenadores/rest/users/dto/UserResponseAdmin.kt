package org.example.prestamoordenadores.rest.users.dto

import org.example.prestamoordenadores.rest.users.models.Role

data class UserResponseAdmin (
    val numeroIdentificacion: String,
    val guid: String,
    val email: String,
    val nombre: String,
    val apellidos: String,
    val curso: String,
    val tutor: String,
    val rol: Role,
    val isActivo: Boolean,
    var createdDate: String,
    var updatedDate: String,
    var lastLoginDate: String,
    var lastPasswordResetDate: String,
    var isDeleted: Boolean,
    var isOlvidado: Boolean
)