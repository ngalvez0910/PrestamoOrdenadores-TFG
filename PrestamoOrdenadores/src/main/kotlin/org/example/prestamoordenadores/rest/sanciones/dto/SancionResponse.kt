package org.example.prestamoordenadores.rest.sanciones.dto

data class SancionResponse(
    val guid : String,
    val userGuid : String,
    val tipoSancion : String,
    val fechaSancion : String,
)
