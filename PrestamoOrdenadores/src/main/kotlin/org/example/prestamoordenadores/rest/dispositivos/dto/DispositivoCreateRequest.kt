package org.example.prestamoordenadores.rest.dispositivos.dto

import org.jetbrains.annotations.NotNull

data class DispositivoCreateRequest(
    @NotNull("Numero de serie no puede ser null")
    var numeroSerie: String,
    @NotNull("Componentes no puede ser null")
    var componentes: String
)
