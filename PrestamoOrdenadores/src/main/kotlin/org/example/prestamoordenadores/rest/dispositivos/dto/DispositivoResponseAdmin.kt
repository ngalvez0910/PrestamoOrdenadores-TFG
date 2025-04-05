package org.example.prestamoordenadores.rest.dispositivos.dto

data class DispositivoResponseAdmin (
    var guid: String,
    var numeroSerie: String,
    var componentes: String,
    var estado: String,
    var incidenciaGuid: String?,
    var isActivo: Boolean
)