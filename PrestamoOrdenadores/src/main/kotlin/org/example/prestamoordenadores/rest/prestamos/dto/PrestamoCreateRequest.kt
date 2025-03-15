package org.example.prestamoordenadores.rest.prestamos.dto

import org.jetbrains.annotations.NotNull

data class PrestamoCreateRequest(
    @NotNull("User Guid no puede ser null")
    var userGuid: String
)