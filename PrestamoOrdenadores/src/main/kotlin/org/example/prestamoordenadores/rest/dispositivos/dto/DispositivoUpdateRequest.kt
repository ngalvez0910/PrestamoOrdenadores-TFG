package org.example.prestamoordenadores.rest.dispositivos.dto


data class DispositivoUpdateRequest (
    var componentes: String,
    var estado: String,
    var incidenciaGuid: String,
    var stock: Int
)