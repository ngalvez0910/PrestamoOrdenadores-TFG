package org.example.prestamoordenadores.rest.dispositivos.dto

import org.example.prestamoordenadores.rest.incidencias.models.Incidencia

data class DispositivoResponseAdmin (
    var guid: String,
    var numeroSerie: String,
    var componentes: String,
    var estado: String,
    var incidencia: Incidencia?,
    var isActivo: Boolean
)