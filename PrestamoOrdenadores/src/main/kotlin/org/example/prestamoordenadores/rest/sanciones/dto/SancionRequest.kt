package org.example.prestamoordenadores.rest.sanciones.dto

data class SancionRequest (
    val userGuid : String,
    val tipoSancion : String,
)