package org.example.prestamoordenadores.rest.prestamos.dto

import org.jetbrains.annotations.NotNull

data class PrestamoUpdateRequest(
    @NotNull("Estado no puede ser null")
    val estado: String,
)
