package org.example.prestamoordenadores.rest.incidencias.dto

import org.jetbrains.annotations.NotNull

data class IncidenciaUpdateRequest (
    @NotNull("Estado de la incidencia no puede estar vacío")
    val estadoIncidencia : String,
)