package org.example.prestamoordenadores.rest.incidencias.dto

data class IncidenciaResponse(
    val guid : String,
    val asunto : String,
    val descripcion : String,
    val estadoIncidencia : String,
    val userGuid : String,
    val createdDate : String
)
