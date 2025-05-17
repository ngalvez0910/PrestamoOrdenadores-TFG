package org.example.prestamoordenadores.rest.dispositivos.dto

data class DispositivoCreateRequest(
    val numeroSerie: String,
    val componentes: String
)
