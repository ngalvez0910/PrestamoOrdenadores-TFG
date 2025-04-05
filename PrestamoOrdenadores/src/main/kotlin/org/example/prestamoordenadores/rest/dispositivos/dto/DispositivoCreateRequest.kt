package org.example.prestamoordenadores.rest.dispositivos.dto

import org.jetbrains.annotations.NotNull

data class DispositivoCreateRequest(
    @NotNull("Componentes no puede ser null")
    var componentes: String
)
