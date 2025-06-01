package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError

/**
 * Función de extensión para validar un objeto [IncidenciaCreateRequest].
 *
 * Realiza las siguientes validaciones:
 * - El campo [asunto] no puede estar en blanco.
 * - El campo [descripcion] no puede estar en blanco.
 *
 * @receiver La instancia de [IncidenciaCreateRequest] a validar.
 * @return Un [Result] que contiene el [IncidenciaCreateRequest] validado si la validación es exitosa ([Ok]),
 * o un [IncidenciaError.IncidenciaValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun IncidenciaCreateRequest.validate(): Result<IncidenciaCreateRequest, IncidenciaError> {
    if (this.asunto.isBlank()) {
        return Err(IncidenciaError.IncidenciaValidationError("El asunto no puede estar vacío"))
    } else if (this.descripcion.isBlank())
        return Err(IncidenciaError.IncidenciaValidationError("La descripcion no puede estar vacía"))

    return Ok(this)
}

/**
 * Función de extensión para validar un objeto [IncidenciaUpdateRequest].
 *
 * Realiza las siguientes validaciones:
 * - El campo [estadoIncidencia] no puede estar en blanco.
 *
 * @receiver La instancia de [IncidenciaUpdateRequest] a validar.
 * @return Un [Result] que contiene el [IncidenciaUpdateRequest] validado si la validación es exitosa ([Ok]),
 * o un [IncidenciaError.IncidenciaValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun IncidenciaUpdateRequest.validate(): Result<IncidenciaUpdateRequest, IncidenciaError> {
    if (this.estadoIncidencia.isBlank()) {
        return Err(IncidenciaError.IncidenciaValidationError("El estado de la incidencia no puede estar vacío"))
    }

    return Ok(this)
}