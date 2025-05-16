package org.example.prestamoordenadores.rest.incidencias.dto

import org.example.prestamoordenadores.rest.users.dto.UserResponse

data class IncidenciaResponseAdmin(
    val guid : String,
    val asunto : String,
    val descripcion : String,
    val estadoIncidencia : String,
    val user : UserResponse,
    val createdDate : String,
    val updatedDate : String,
    val isDeleted: Boolean
)
