package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion

/**
 * Función de extensión para validar un objeto [SancionRequest].
 *
 * Realiza las siguientes validaciones:
 * - El campo [userGuid] no puede estar en blanco.
 * - El campo [tipoSancion] no puede estar en blanco y debe ser uno de los valores válidos definidos en [TipoSancion].
 *
 * @receiver La instancia de [SancionRequest] a validar.
 * @return Un [Result] que contiene el [SancionRequest] validado si la validación es exitosa ([Ok]),
 * o un [SancionError.SancionValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun SancionRequest.validate(): Result<SancionRequest, SancionError> {
    if (this.userGuid.isBlank()) {
        return Err(SancionError.SancionValidationError("El guid del usuario no puede estar vacío"))
    } else if (this.tipoSancion.isBlank() || this.tipoSancion.uppercase() !in TipoSancion.entries.map { it.name }) {
        return Err(SancionError.SancionValidationError("El tipo de la sanción no puede estar vacío"))
    }

    return Ok(this)
}

/**
 * Función de extensión para validar un objeto [SancionUpdateRequest].
 *
 * Realiza las siguientes validaciones:
 * - El campo [tipoSancion] no puede estar en blanco y debe ser uno de los valores válidos definidos en [TipoSancion].
 *
 * @receiver La instancia de [SancionUpdateRequest] a validar.
 * @return Un [Result] que contiene el [SancionUpdateRequest] validado si la validación es exitosa ([Ok]),
 * o un [SancionError.SancionValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun SancionUpdateRequest.validate(): Result<SancionUpdateRequest, SancionError> {
    if (this.tipoSancion.isBlank() || this.tipoSancion.uppercase() !in TipoSancion.entries.map { it.name }) {
        return Err(SancionError.SancionValidationError("El tipo de la sanción no puede estar vacío"))
    }

    return Ok(this)
}