package org.example.prestamoordenadores.rest.sanciones.dto

data class SancionRequest (
    val userGuid : String,
    var tipoSancion : String,
)