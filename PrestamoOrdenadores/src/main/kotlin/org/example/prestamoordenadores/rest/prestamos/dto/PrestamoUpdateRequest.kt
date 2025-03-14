package org.example.prestamoordenadores.rest.prestamos.dto

import org.example.prestamoordenadores.rest.prestamos.models.Estado

data class PrestamoUpdateRequest(
    val estado: Estado,
)
