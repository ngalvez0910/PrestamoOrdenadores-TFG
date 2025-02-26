package org.example.prestamoordenadores.rest.dispositivos.dto

data class DispositivoCreateRequest(
    var componentes: String,
    var stock: Int,
)
