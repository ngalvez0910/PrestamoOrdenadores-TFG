package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo

/**
 * Función de extensión para validar un objeto [PrestamoUpdateRequest].
 *
 * Realiza las siguientes validaciones:
 * - El campo [estadoPrestamo] no puede estar en blanco y debe ser uno de los valores válidos definidos en [EstadoPrestamo].
 *
 * @receiver La instancia de [PrestamoUpdateRequest] a validar.
 * @return Un [Result] que contiene el [PrestamoUpdateRequest] validado si la validación es exitosa ([Ok]),
 * o un [PrestamoError.PrestamoValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun PrestamoUpdateRequest.validate(): Result<PrestamoUpdateRequest, PrestamoError> {
    if (this.estadoPrestamo.isBlank() || this.estadoPrestamo.uppercase() !in EstadoPrestamo.entries.map { it.name }) {
        return Err(PrestamoError.PrestamoValidationError("El estado del préstamo no puede estar vacío"))
    }

    return Ok(this)
}