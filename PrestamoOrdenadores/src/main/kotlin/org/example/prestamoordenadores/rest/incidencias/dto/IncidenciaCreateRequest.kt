package org.example.prestamoordenadores.rest.incidencias.dto

import org.jetbrains.annotations.NotNull

data class IncidenciaCreateRequest (
    @NotNull("Asunto no puede estar vacio")
    val asunto : String,
    @NotNull("Descripcion no puede estar vacía")
    val descripcion : String,
)