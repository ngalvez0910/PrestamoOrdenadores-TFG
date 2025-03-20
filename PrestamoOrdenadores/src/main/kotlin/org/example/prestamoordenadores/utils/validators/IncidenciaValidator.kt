package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError

fun IncidenciaCreateRequest.validate(): Result<IncidenciaCreateRequest, IncidenciaError> {
    if (this.asunto.isBlank()) {
        return Err(IncidenciaError.IncidenciaValidationError("El asunto no puede estar vacío"))
    } else if (this.descripcion.isBlank())
        return Err(IncidenciaError.IncidenciaValidationError("La descripcion no puede estar vacía"))

    return Ok(this)
}

fun IncidenciaUpdateRequest.validate(): Result<IncidenciaUpdateRequest, IncidenciaError> {
    if (this.estadoIncidencia.isBlank()) {
        return Err(IncidenciaError.IncidenciaValidationError("El estado de la incidencia no puede estar vacío"))
    }

    return Ok(this)
}