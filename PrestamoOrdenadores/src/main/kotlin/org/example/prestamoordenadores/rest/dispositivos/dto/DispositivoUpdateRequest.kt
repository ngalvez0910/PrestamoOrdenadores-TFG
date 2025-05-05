package org.example.prestamoordenadores.rest.dispositivos.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DispositivoUpdateRequest (
    var componentes: String? = null,
    var estadoDispositivo: String? = null,
    var incidenciaGuid: String? = null,
    var isActivo: Boolean? = null
)