package org.example.prestamoordenadores.rest.users.dto

data class UserUpdateRequest(
    val rol: String?,
    val isActivo: Boolean?
)